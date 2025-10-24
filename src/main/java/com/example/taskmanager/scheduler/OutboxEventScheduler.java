package com.example.taskmanager.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.BatchOutbox;
import com.example.taskmanager.entity.IOutbox;
import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.dto.OutboxEventMarkAsFailedDTO;
import com.example.taskmanager.entity.enums.EventStatusEnum;
import com.example.taskmanager.producer.TaskMessageProducer;
import com.example.taskmanager.service.BatchOutboxService;
import com.example.taskmanager.service.OutboxEventService;
import com.example.taskmanager.utils.RedisDistributedLockExecutor;

/*
 * 定期處理未處理的 Outbox 事件
 *
 * 當系統部署多個實例時，確保只有一個實例執行這段邏輯，避免事件重複發送
 * 利用 Redisson 的分布式鎖來保護整個處理流程
 * 交由 Redisson 自動管理鎖的續期機制
 *
 * 成功取得鎖後，撈出待處理的事件
 * 使用 ThreadPoolTaskExecutor 執行緒池非同步處理每個事件
 * */
@Component
public class OutboxEventScheduler {
	
	private static final Logger log = LoggerFactory.getLogger(OutboxEventScheduler.class);
	
	private final OutboxEventService outboxEventService;
	private final TaskMessageProducer taskMessageProducer;
	private final ThreadPoolTaskExecutor taskExecutor;
	private final RedisDistributedLockExecutor lockExecutor;
	private final BatchOutboxService batchOutboxService;
	
	@Value("${rabbitmq.retry.max}")
	private int RETRY_MAX;
	
	private String OUTBOX_LOCK_KEY = "outbox:lock";
	
	@Autowired
	public OutboxEventScheduler (OutboxEventService outboxEventService, TaskMessageProducer taskMessageProducer, 
			ThreadPoolTaskExecutor taskExecutor, RedisDistributedLockExecutor lockExecutor,
			BatchOutboxService batchOutboxService) {
		this.outboxEventService = outboxEventService;
		this.taskMessageProducer = taskMessageProducer;
		this.taskExecutor = taskExecutor;
		this.lockExecutor = lockExecutor;
		this.batchOutboxService = batchOutboxService;
	}
	
	@Scheduled(fixedRate = 60000)  // 使用 Spring 的定時任務，Application 要有 @EnableScheduling 才會啟動
	public void processOutboxEvents() {
		try {
			// 嘗試取得分布式鎖
			lockExecutor.executeWithoutResult(OUTBOX_LOCK_KEY, 5, 30, () -> {
				
				List<IOutbox> pending = new ArrayList<>();
				pending.addAll(outboxEventService.findPendingEvents(10));
				pending.addAll(batchOutboxService.findPending(10));
				
				// 對每個事件，交給執行緒池非同步處理
				for(IOutbox event : pending) {
					taskExecutor.submit(() -> processEvent(event));
				}
			});
		} catch (IllegalStateException e) {
			// 如果無法取得鎖，表示其他實例已取得，會捕獲此例外並警告
			log.warn("排程未取得鎖 : {}", e.getMessage());
		} catch (Exception e) {
			// 其他執行錯誤，記錄錯誤日誌
			log.error("排程任務執行失敗: {}", e.getMessage(), e);
		}
	}
	
	private void processEvent(IOutbox  event) {
		try {
			// 發送消息
			taskMessageProducer.send(event);
			
			// 標記事件為已發送
			event.setStatus(EventStatusEnum.SENT);
			event.setSentTime(LocalDateTime.now());
			
			// 更新資料庫
			if(event instanceof OutboxEvent outboxEvent) {
				outboxEventService.markAsSent(event.getId());
			} else if (event instanceof BatchOutbox batchOutbox) {
				batchOutboxService.update(batchOutbox);
			}
			
			
			log.info("Event ID {} sent and marked as SENT", event.getId());
		}catch(Exception e) {
			log.error("Failed to send event ID {}: {}", event.getId(), e.getMessage(), e);
			
			// 單筆事件失敗處理
	        if (event instanceof OutboxEvent outboxEvent) {
	            OutboxEventMarkAsFailedDTO dto = new OutboxEventMarkAsFailedDTO();
	            dto.setId(outboxEvent.getId());
	            dto.setLastError(e.getMessage());

	            if (outboxEvent.getRetryCount() >= RETRY_MAX) {
	                outboxEvent.setStatus(EventStatusEnum.DEAD);
	                outboxEventService.markAsDead(outboxEvent);
	            } else {
	                dto.setNextRetryTime(LocalDateTime.now().plusMinutes(1));
	                outboxEventService.markAsFailed(dto);
	            }
	        
	        // 批次事件失敗處理
	        } else if (event instanceof BatchOutbox batchOutbox) {
	            batchOutbox.setRetryCount(batchOutbox.getRetryCount() == null ? 1 : batchOutbox.getRetryCount() + 1);

	            if (batchOutbox.getRetryCount() >= RETRY_MAX) {
	                batchOutbox.setStatus(EventStatusEnum.DEAD);
	            } else {
	                batchOutbox.setStatus(EventStatusEnum.FAILED);
	                batchOutbox.setNextRetryTime(LocalDateTime.now().plusMinutes(1));
	            }
	            batchOutboxService.update(batchOutbox);
	        }
		}
	}
}