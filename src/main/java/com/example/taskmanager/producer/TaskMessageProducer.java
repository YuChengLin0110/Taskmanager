package com.example.taskmanager.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.Task;

@Service
public class TaskMessageProducer {
	
	private final AmqpTemplate amqpTemplate;
	
	@Value("${rabbitmq.exchange}")
	private String exchange;
	
	@Value("${rabbitmq.routingkey}")
    private String routingkey;
	
	@Autowired
	public TaskMessageProducer(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}
	
	public void send(Task task) {
		
		// 使用 amqpTemplate 發送訊息，會根據 exchange 和 routingkey 發送到 RabbitMQ
		amqpTemplate.convertAndSend(exchange, routingkey, task);;
	}
}
