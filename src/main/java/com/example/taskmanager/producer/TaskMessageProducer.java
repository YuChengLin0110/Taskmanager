
package com.example.taskmanager.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.enums.EventTypeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskMessageProducer {

	private static final Logger log = LoggerFactory.getLogger(TaskMessageProducer.class);

	private final AmqpTemplate amqpTemplate;
	
	private final ObjectMapper objectMapper;

	@Value("${rabbitmq.exchange}")
	private String exchange;

	@Value("${rabbitmq.routingkey.task.created}")
	private String taskCreatedRoutingKey;

	
	@Value("${rabbitmq.routingkey.task.assigned}")
	private String taskAssignedRoutingKey;
	
	@Value("${rabbitmq.routingkey.task.overdue}")
	private String taskOverdueRoutingKey;

	@Autowired
	public TaskMessageProducer(AmqpTemplate amqpTemplate, ObjectMapper objectMapper) {
		this.amqpTemplate = amqpTemplate;
		this.objectMapper = objectMapper;
	}

	public void send(OutboxEvent event) {
		String routingKey = getTaskRoutingKey(event);
		
		try {
			byte[] body = objectMapper.writeValueAsBytes(event);
			
			// 建立 RabbitMQ 訊息
			Message message = MessageBuilder.withBody(body)
					.setContentType(MessageProperties.CONTENT_TYPE_JSON) // 內容為 JSON
					.setDeliveryMode(MessageDeliveryMode.PERSISTENT) // 傳遞模式為 PERSISTENT（ RabbitMQ 重啟也不會丟失 ）搭配 durable queue 一起使用
					.build();
			
			log.info("Send message to exchange: {} with routingKey: {} body: {}", exchange, routingKey, new String(body));
			amqpTemplate.send(exchange, routingKey, message);
			log.info("Message sent: {}", event);
					
		}catch(JsonProcessingException  e) {
			log.error("Failed to serialize event: {}", event, e);
			throw new RuntimeException("JSON serialization failed", e);
		}
	}

	private String getTaskRoutingKey(OutboxEvent event) {
		EventTypeEnum type = event.getEventType();
		return switch (type) {
		case TASK_CREATED -> taskCreatedRoutingKey;
		case TASK_ASSIGNED -> taskAssignedRoutingKey; 
		case TASK_OVERDUE -> taskOverdueRoutingKey;
		default -> throw new IllegalArgumentException("Unsupported event type: " + event.getEventType());
		};
	}
}
