package com.example.taskmanager.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.dto.OutboxEventMarkAsFailedDTO;
import com.example.taskmanager.producer.TaskMessageProducer;
import com.example.taskmanager.service.OutboxEventService;

@Component
public class OutboxEventScheduler {
	
	private static final Logger log = LoggerFactory.getLogger(OutboxEventScheduler.class);
	
	private final OutboxEventService outboxEventService;
	private final TaskMessageProducer taskMessageProducer;
	
	@Autowired
	public OutboxEventScheduler (OutboxEventService outboxEventService, TaskMessageProducer taskMessageProducer) {
		this.outboxEventService = outboxEventService;
		this.taskMessageProducer = taskMessageProducer;
	}
	
	@Scheduled(fixedRate = 5000)  // 每 5 秒執行一次
	public void processOutboxEvents() {
		List<OutboxEvent> pendingEvents = outboxEventService.findPendingEvents(10);
		
		for(OutboxEvent event : pendingEvents) {
			try {
				taskMessageProducer.send(event);
				outboxEventService.markAsSent(event.getId());
				
				log.info("Event ID {} sent and marked as SENT", event.getId());
			}catch(Exception e) {
				log.error("Failed to send event ID {}: {}", event.getId(), e.getMessage(), e);
				
				OutboxEventMarkAsFailedDTO dto = new OutboxEventMarkAsFailedDTO();
				dto.setId(event.getId());
				dto.setLastError(e.getMessage());
				dto.setNextRetryTime(LocalDateTime.now().plusMinutes(1)); // 設定 1 分鐘後再重試
				outboxEventService.markAsFailed(dto);
			}
		}
	}
}