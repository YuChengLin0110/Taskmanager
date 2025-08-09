package com.example.taskmanager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
	
	public static final String NOTIFACTION_TOPIC = "notification-topic";
	
	// // 用 TopicBuilder 來創建一個 Kafka 的 Topic
	@Bean
	public NewTopic noticactionTopic() {
		return TopicBuilder.name(NOTIFACTION_TOPIC)
				.partitions(1) // 分割槽數設為1，代表只有一個分區
				.replicas(1) // 副本數設為1，表示只有一份資料備份
				.build(); // 完成並回傳這個 Topic 物件
	}
}
