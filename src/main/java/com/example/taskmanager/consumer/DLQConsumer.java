package com.example.taskmanager.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.OutboxEvent;

@Service
public class DLQConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(DLQConsumer.class);
	
	@RabbitListener(queues = "${rabbitmq.queue.task.dlq}")
	public void receiveDLQMessage(OutboxEvent event) {
		log.warn("Received DLQ Message : {}", event);
	}
}
