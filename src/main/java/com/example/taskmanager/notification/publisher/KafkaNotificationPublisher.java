package com.example.taskmanager.notification.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.notification.event.NotificationEvent;
import com.example.taskmanager.notification.event.TaskCreatedEvent;

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
	
	@Override
	public void publish(NotificationRequest request) {
		
		log.info("Send notification to Kafka: {}", request);
		
		kafkaTemplate.send(request.getKafkaTopic(), request);
	}
}
