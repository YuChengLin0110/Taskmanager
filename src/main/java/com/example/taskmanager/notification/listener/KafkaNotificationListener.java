package com.example.taskmanager.notification.listener;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEnum;
import com.example.taskmanager.entity.enums.NotificationEventType;
import com.example.taskmanager.notification.NotificationStrategyFactory;
import com.example.taskmanager.notification.event.TaskCreatedEvent;
import com.example.taskmanager.notification.resolver.TaskCreatedNotificationResolver;
import com.example.taskmanager.notification.strategy.NotificationStrategy;

@Component
public class KafkaNotificationListener {
	
	private static final Logger log = LoggerFactory.getLogger(KafkaNotificationListener.class);
	
	private final NotificationStrategyFactory strategyFactory;
	
	@Autowired
	public KafkaNotificationListener(NotificationStrategyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}
	
	// Kafka 消費者監聽方法，訂閱 notification-topic 主題
	// 當收到 NotificationRequest 訊息時會被呼叫
	@KafkaListener(topics = "notification-topic", groupId = "notification-group")
	public void notification(NotificationRequest request) {

		// 取得這次要通知的通道
		List<NotificationEnum> channels = getChannels(request.getEventType());

        for (NotificationEnum channel : channels) {
            NotificationStrategy strategy = strategyFactory.getStrategy(channel);
            strategy.send(request);
            
            log.info("Kafka Listener 發送通知");
        }
	}
	
	private List<NotificationEnum> getChannels(NotificationEventType type) {
		switch(type) {
		case TASK_CREATED : return List.of(NotificationEnum.SLACK, NotificationEnum.EMAIL);
		default : return new ArrayList<>();
		}
	}
}
