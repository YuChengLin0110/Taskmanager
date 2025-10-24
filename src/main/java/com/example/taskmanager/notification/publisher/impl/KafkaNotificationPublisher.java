package com.example.taskmanager.notification.publisher.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.notification.publisher.NotificationEventPublisher;

/*
 * @ConditionalOnProperty
 * name = "notification.mode" 表示要看 Spring Boot 配置裡的 notification.mode
 * havingValue = "kafka" 當 notification.mode 的值是 "kafka" 時，這個 Bean 才會被載入
 * */
@Component
@ConditionalOnProperty(name = "notification.mode", havingValue = "kafka")
public class KafkaNotificationPublisher implements NotificationEventPublisher {
	
	
	private static final Logger log = LoggerFactory.getLogger(KafkaNotificationPublisher.class);
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired
	public KafkaNotificationPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}
	
	/**
     * 發送通知到 Kafka
     * 如果在事務中，會在事務提交後再發送
     * 否則直接發送
     */
	@Override
	public void publish(NotificationRequest request) {
		log.debug("KafkaNotificationPublisher.publish() called, topic={}, request={}", request.getKafkaTopic(), request);
		
		// 檢查當前是否有啟用 Spring 事務
		// 如果在事務中，註冊一個事務同步回調
	    // TransactionSynchronization 允許我們在事務不同階段執行程式碼
		if(TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				// afterCommit 方法會在事務成功提交後執行
				// 避免事務回滾時發送錯誤的消息
				@Override
				public void afterCommit() {
					kafkaTemplate.send(request.getKafkaTopic(), request);
					log.info("Send notification to Kafka: {}", request);
				}
			});
		} else {
			log.info("Send notification to Kafka: {}", request);
			
			kafkaTemplate.send(request.getKafkaTopic(), request);
		}
	}
}
