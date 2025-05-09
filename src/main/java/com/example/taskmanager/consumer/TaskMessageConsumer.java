package com.example.taskmanager.consumer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.enums.EventStatusEnum;
import com.example.taskmanager.entity.enums.TaskStatusEnum;
import com.example.taskmanager.service.TaskService;
import com.rabbitmq.client.Channel;

@Service
public class TaskMessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(TaskMessageConsumer.class);

	private final TaskService taskService;

	public TaskMessageConsumer(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * 監聽 taskCreated 訊息佇列
	 * 使用 containerFactory = "rabbitListenerContainerFactory"，代表使用手動確認模式（MANUAL）
	 * 若處理成功會手動 ack，若處理失敗則手動 nack，訊息會被送往 DLQ
	 * 
	 * ackMode="MANUAL" 在這裡是裝飾的，因已由 containerFactory 指定，保留可以更加易讀
	 */
	@RabbitListener(queues = "${rabbitmq.queue.taskCreated}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskCreatedMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		try {
			log.info("Received 'taskCreated' message: {}", event);
			
			// 如果事件已處理過，直接 ack 掉
			if (event.getEventType() != EventStatusEnum.PENDING.name()) {
				log.info("Skip Event ID {}: already processed with status {}", event.getId(), event.getStatus());
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
				return;
			}

			taskService.updateTaskStatus(event.getEntityId(), TaskStatusEnum.PROCESSINGS);
			log.info("Task status updated to 'PROCESSINGS' for task ID: {}", event.getEntityId());
			
			// 處理成功，手動 ack 訊息
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

		} catch (Exception e) {
			log.error("Failed to process 'taskCreated' message", e);
			
			// 處理失敗，手動 nack，訊息會被送往 DLQ
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		}
	}

	@RabbitListener(queues = "${rabbitmq.queue.taskCompleted}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskCompletedMessage(OutboxEvent event, Channel channel, Message message) throws IOException {

		try {
			log.info("Received 'taskCreated' message: {}", event);
			taskService.updateTaskStatus(event.getEntityId(), TaskStatusEnum.DONE);
			log.info("Task status updated to 'DONE' for task ID: {}", event.getEntityId());
			
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			log.error("Failed to process 'taskCompleted' message", e);
			
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		}
	}
}