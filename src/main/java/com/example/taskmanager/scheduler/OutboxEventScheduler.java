package com.example.taskmanager.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.dto.OutboxEventMarkAsFailedDTO;
import com.example.taskmanager.entity.enums.EventStatusEnum;
import com.example.taskmanager.lock.RedisLockWatchdog;
import com.example.taskmanager.producer.TaskMessageProducer;
import com.example.taskmanager.service.OutboxEventService;
import com.example.taskmanager.service.RedisLockService;

/*
 * 定期處理未處理的 Outbox 事件
 *
 * 確保如果系統部署在多個實例上時，只有一個實例會執行這段邏輯，避免重複發送事件
 * 透過 Redis 分布式鎖來保護整個邏輯區塊
 *
 * 拿到鎖之後，會啟動 RedisLockWatchdog 執行緒，每隔一段時間延長鎖的過期時間
 * 確保處理事件的過程中鎖不會自動過期，避免鎖被其他實例搶走
 *
 * 如果成功取得鎖，會查出處理的事件，並交給執行緒池 ThreadPoolTaskExecutor
 * */
@Component
public class OutboxEventScheduler {
	
	private static final Logger log = LoggerFactory.getLogger(OutboxEventScheduler.class);
	
	private final OutboxEventService outboxEventService;
	private final TaskMessageProducer taskMessageProducer;
	private final RedisLockService redisLockService;
	private final ThreadPoolTaskExecutor taskExecutor;
	
	@Value("${rabbitmq.retry.max}")
	private int RETRY_MAX;
	
	@Value("${redis.lock.key.outboxevent}")
	private String REDIS_LOCK_KEY;
	
	@Value("${redis.lock.expire.outboxevent}")
	private long REDIS_EXPIRE_TIME;
	
	@Autowired
	public OutboxEventScheduler (OutboxEventService outboxEventService, TaskMessageProducer taskMessageProducer, RedisLockService redisLockService, ThreadPoolTaskExecutor taskExecutor) {
		this.outboxEventService = outboxEventService;
		this.taskMessageProducer = taskMessageProducer;
		this.redisLockService = redisLockService;
		this.taskExecutor = taskExecutor;
	}
	
	@Scheduled(fixedRate = 60000)  // 使用 Spring 的定時任務，Application 要有 @EnableScheduling 才會啟動
	public void processOutboxEvents() {
		// 嘗試取得 Redis 鎖
		String lockValue = redisLockService.tryLock(REDIS_LOCK_KEY, REDIS_EXPIRE_TIME);
		
		if (lockValue != null) {
			
			// 創建監聽用的執行緒，用來延長鎖的過時時間，預防還沒處理完就被釋放
			RedisLockWatchdog watchdog = new RedisLockWatchdog(redisLockService.getRedisTemplate(), REDIS_LOCK_KEY, lockValue, REDIS_EXPIRE_TIME);
			Thread watchdogThread = new Thread(watchdog);
			watchdogThread.start();
			
			try {
				List<OutboxEvent> pendingEvents = outboxEventService.findPendingEvents(10);
				
				for (OutboxEvent event : pendingEvents) {
                    taskExecutor.submit(() -> processEvent(event)); // 使用執行緒池處理事件
                }
				
			}finally {
				// 結束後停止監聽用的執行緒，並釋放 Redis 鎖
				watchdog.stop();
				redisLockService.unLock(REDIS_LOCK_KEY, lockValue);
			}
		}else {
			log.warn("Unable to get lock for OutboxEventScheduler, skip this run");
		}
	}
	
	private void processEvent(OutboxEvent event) {
		try {
			taskMessageProducer.send(event);
			outboxEventService.markAsSent(event.getId());
			
			log.info("Event ID {} sent and marked as SENT", event.getId());
		}catch(Exception e) {
			log.error("Failed to send event ID {}: {}", event.getId(), e.getMessage(), e);
			
			OutboxEventMarkAsFailedDTO dto = new OutboxEventMarkAsFailedDTO();
			dto.setId(event.getId());
			dto.setLastError(e.getMessage());
			
			// 如果重試太多次還是失敗，就標記為 DEAD
			if(event.getRetryCount() >= RETRY_MAX) {
				event.setStatus(EventStatusEnum.DEAD);
				outboxEventService.markAsDead(event);
			} else {
				dto.setNextRetryTime(LocalDateTime.now().plusMinutes(1)); // 設定 1 分鐘後再重試
				outboxEventService.markAsFailed(dto);
			}	
		}
	}
}