package com.example.taskmanager.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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

	@Value("${rabbitmq.exchange.dlq}")
	private String dlqExchange;

	@Value("${rabbitmq.routingkey.task.created}")
	private String taskCreatedRoutingKey;

	@Value("${rabbitmq.routingkey.task.assigned}")
	private String taskAssignedRoutingKey;

	@Value("${rabbitmq.routingkey.task.overdue}")
	private String taskOverdueRoutingKey;

	@Value("${rabbitmq.routingkey.task.dlq}")
	private String dlqRoutingKey;

	@Value("${rabbitmq.queue.task.created}")
	private String taskCreatedQueueName;

	@Value("${rabbitmq.queue.task.assigned}")
	private String taskAssignedQueueName;

	@Value("${rabbitmq.queue.task.overdue}")
	private String taskOverdueQueueName;

	@Value("${rabbitmq.queue.task.dlq}")
	private String dlqQueueName;

	@Bean
	public Queue taskCreatedQueue() {
		return buildQueueWithDLQ(taskCreatedQueueName);
	}

	@Bean
	public Queue taskAssignedQueue() {
		return buildQueueWithDLQ(taskAssignedQueueName);
	}

	@Bean
	public Queue taskOverdueQueue() {
		return buildQueueWithDLQ(taskOverdueQueueName);
	}

	@Bean
	public Queue dlqQueue() {
		return new Queue(dlqQueueName, true);
	}

	// 建立一個 DirectExchange，會根據 routing key 分派訊息
	// new DirectExchange(name, durable, autoDelete)
	// durable = true：重啟 RabbitMQ 後仍保留交換器
	// autoDelete = false：即使沒有隊列綁定，也不會自動刪除交換器
	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(exchange, true, false);
	}
	
	@Bean DirectExchange dlqExchange() {
		return new DirectExchange(dlqExchange, true, false);
	}

	// 如果使用 TopicExchange，可以換成下面的設定，但這裡目前使用 DirectExchange
//	@Bean
//	public TopicExchange exchange() {
//		return new TopicExchange(exchange);
//	}

	// 建立 Binding，把 Queue 綁定到 Exchange，並透過指定的 routing key 做連結
	@Bean
	public Binding bindingTaskCreated(Queue taskCreatedQueue, DirectExchange exchange) {
		return BindingBuilder.bind(taskCreatedQueue).to(exchange).with(taskCreatedRoutingKey);
	}

	@Bean
	public Binding bindingTaskAssigned(Queue taskAssignedQueue, DirectExchange exchange) {
		return BindingBuilder.bind(taskAssignedQueue).to(exchange).with(taskAssignedRoutingKey);
	}

	@Bean
	public Binding bindingTaskOverdue(Queue taskOverdueQueue, DirectExchange exchange) {
		return BindingBuilder.bind(taskOverdueQueue).to(exchange).with(taskOverdueRoutingKey);
	}

	@Bean
	public Binding dlqBinding(Queue dlqQueue, DirectExchange dlqExchange) {
		return BindingBuilder.bind(dlqQueue).to(dlqExchange).with(dlqRoutingKey);
	}
	
	// 因為要使用 DLQ 機制，需要自訂消費者行為
	// 所以必須實作 SimpleRabbitListenerContainerFactory 並設定為手動確認模式
	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

		factory.setConnectionFactory(connectionFactory);
		
		// 設定訊息的確認模式為手動確認 AcknowledgeMode.MANUAL
		factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

		return factory;
	}

	// 設定訊息轉換器，把 Java 物件轉成 JSON 格式來傳送，接收時也會把 JSON 轉回物件
	@Bean
	public MessageConverter jackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
	// 建立 Queue 並設置DLQ 的相關配置來處理未成功的訊息
	private Queue buildQueueWithDLQ(String queueName) {
		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", dlqExchange); // 指定死信交換器
		args.put("x-dead-letter-routing-key", dlqRoutingKey); // 指定死信路由鍵
		
		return new Queue(queueName, true, false, false, args);
	}
}
