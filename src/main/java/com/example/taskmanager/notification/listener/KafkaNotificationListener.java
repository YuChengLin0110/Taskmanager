package com.example.taskmanager.notification.listener;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEnum;
import com.example.taskmanager.entity.enums.NotificationEventType;
import com.example.taskmanager.notification.NotificationStrategyFactory;
import com.example.taskmanager.notification.strategy.NotificationStrategy;

@Component
@ConditionalOnProperty(name = "notification.mode", havingValue = "kafka")
public class KafkaNotificationListener {
	
	private static final Logger log = LoggerFactory.getLogger(KafkaNotificationListener.class);
	
	private final NotificationStrategyFactory strategyFactory;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired
	public KafkaNotificationListener(NotificationStrategyFactory strategyFactory, KafkaTemplate<String, Object> kafkaTemplate) {
		this.strategyFactory = strategyFactory;
		this.kafkaTemplate = kafkaTemplate;
	}
	
	@RetryableTopic(
			attempts = "3", // 消費者收到訊息處理失敗後，最多會重試幾次
			backoff = @org.springframework.retry.annotation.Backoff(delay = 1000, multiplier = 2.0), //延遲 1000ms，乘 2.0 → 每次重試延遲會指數增加 (1000 → 2000 → 4000ms)
			autoCreateTopics = "false", // 是否自動建立重試與 DLT Topic ，設為 false 表示 Topic 必須手動建立
			topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE, // 重試 Topic 命名策略，
			dltStrategy = DltStrategy.FAIL_ON_ERROR // 當重試完還是失敗，直接拋出異常
			)
	// Kafka 消費者監聽方法，訂閱 notification-topic 主題
	// 當收到 NotificationRequest 訊息時會被呼叫
	@KafkaListener(topics = "notification-topic", groupId = "notification-group")
	public void notificationListener(NotificationRequest request) {
		
			// 取得這次要通知的通道
			List<NotificationEnum> channels = getChannels(request.getEventType());
			
			// 每個通道單獨 try-catch 保護，避免單個通道失敗影響其他通道
	        for (NotificationEnum channel : channels) {
	        	try {
	        		
	        		NotificationStrategy strategy = strategyFactory.getStrategy(channel);
	        		strategy.send(request);
	        		log.info("Kafka Listener 發送通知");
	            
	        	} catch (Exception e) {
	        		log.error("Kafka Listener 發送通知失敗，Topic = {}, Error = {}", request.getKafkaTopic(), e);
	        		sendToDLT(request);
	        	}
	        }
	}
	
	private List<NotificationEnum> getChannels(NotificationEventType type) {
		switch(type) {
		case TASK_CREATED : return List.of(NotificationEnum.SLACK, NotificationEnum.EMAIL);
		default : return new ArrayList<>();
		}
	}
	
	private void sendToDLT (NotificationRequest request) {
		try {
			String topic = "notification-topic.DLT";
			kafkaTemplate.send(topic, request);
			log.info("失敗訊息已送到 DLT Topic: {}", topic);
		}catch (Exception e){
			log.error("送到 DLT Topic 失敗: {}", e.getMessage(), e);
		}
	}
}
