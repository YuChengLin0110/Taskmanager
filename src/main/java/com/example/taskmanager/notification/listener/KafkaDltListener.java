package com.example.taskmanager.notification.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.dto.NotificationRequest;

@Component
@ConditionalOnProperty(name = "notification.mode", havingValue = "kafka")
public class KafkaDltListener {
	
	private static final Logger log = LoggerFactory.getLogger(KafkaDltListener.class);
	
	@KafkaListener(topics = "notification-topic.DLT", groupId = "notification-group.dlt")
	public void notificationDlt(NotificationRequest request) {
		log.error("DLT 收到無法處理的訊息 : {}", request);
	}
}
