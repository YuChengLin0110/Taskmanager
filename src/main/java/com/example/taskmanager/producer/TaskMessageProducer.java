package com.example.taskmanager.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.enums.EventTypeEnum;

@Service
public class TaskMessageProducer {

	private final AmqpTemplate amqpTemplate;

	@Value("${rabbitmq.exchange}")
	private String exchange;

	@Value("${rabbitmq.routingkey.taskCreated}")
	private String taskCreatedRoutingKey;

	@Value("${rabbitmq.routingkey.taskCompleted}")
	private String taskCompletedRoutingKey;

	@Autowired
	public TaskMessageProducer(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void send(OutboxEvent event) {

		// 使用 amqpTemplate 發送訊息，會根據 會根據 EventType 選擇正確的 routingkey 發送
		amqpTemplate.convertAndSend(exchange, getTaskRoutingKey(event), event);
		;
	}

	private String getTaskRoutingKey(OutboxEvent event) {
		EventTypeEnum type = EventTypeEnum.valueOf(event.getEventType());
		return switch (type) {
		case TASK_CREATED -> taskCreatedRoutingKey;
		case TASK_COMPLETED -> taskCompletedRoutingKey;
		default -> throw new IllegalArgumentException("Unsupported event type: " + event.getEventType());
		};
	}
}
