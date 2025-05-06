package com.example.taskmanager.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	
	// 讀取 application.properties 檔案中的 RabbitMQ 設定
	@Value("${rabbitmq.exchange}")
	private String exchange;
	
	@Value("${rabbitmq.routingkey.taskCreated}")
	private String taskCreatedroutingKey;
	
	@Value("${rabbitmq.routingkey.taskCompleted}")
	private String taskCompletedroutingKey;
	
	@Value("${rabbitmq.queue.taskCreated}")
	private String taskCreatedQueueName;
	
	@Value("${rabbitmq.queue.taskCompleted}")
	private String taskCompletedQueueName;
	
	// 建立一個 Queue，設定 durable = true 表示 RabbitMQ 重啟，Queue 也會保留
	@Bean
	public Queue taskCreatedQueue() {
			return new Queue(taskCreatedQueueName, true);
	}
	
	@Bean
	public Queue taskCompletedQueue() {
			return new Queue(taskCompletedQueueName, true);
	}
	
    // 建立一個 DirectExchange，會根據 routing key 分派訊息
	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(exchange);
	}
	
	// 如果使用 TopicExchange，可以換成下面的設定，但這裡目前使用 DirectExchange
//	@Bean
//	public TopicExchange exchange() {
//		return new TopicExchange(exchange);
//	}
	
	// 建立 Binding，把 Queue 綁定到 Exchange，並透過指定的 routing key 做連結
	@Bean
	public Binding bindingTaskCreated(Queue taskCreatedQueue, DirectExchange exchange) {
	    return BindingBuilder.bind(taskCreatedQueue).to(exchange).with(taskCreatedroutingKey);
	}
	
	@Bean
	public Binding bindingTaskCompleted(Queue taskCompletedQueue, DirectExchange exchange) {
	    return BindingBuilder.bind(taskCompletedQueue).to(exchange).with(taskCompletedroutingKey);
	}

	
	// 設定訊息轉換器，把 Java 物件轉成 JSON 格式來傳送，接收時也會把 JSON 轉回物件
	@Bean
	public MessageConverter jackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
