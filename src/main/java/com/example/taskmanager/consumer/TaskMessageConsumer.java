package com.example.taskmanager.consumer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.enums.TaskStatusEnum;
import com.example.taskmanager.service.TaskService;
import com.rabbitmq.client.Channel;

@Service
public class TaskMessageConsumer {

	private static final Logger log = LoggerFactory.getLogger(TaskMessageConsumer.class);

	private final TaskService taskService;

	@Autowired
	public TaskMessageConsumer(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * 監聽 taskCreated 訊息佇列 使用 containerFactory =
	 * "rabbitListenerContainerFactory"，代表使用手動確認模式（MANUAL） 若處理成功會手動 ack，若處理失敗則手動
	 * nack，訊息會被送往 DLQ
	 * 
	 * ackMode="MANUAL" 在這裡是裝飾的，因已由 containerFactory 指定，保留可以更加易讀
	 */
	@RabbitListener(queues = "${rabbitmq.queue.task.created}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskCreatedMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		processMessage(event, channel, message);
	}

	@RabbitListener(queues = "${rabbitmq.queue.task.assigned}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskAssignedMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		processMessage(event, channel, message);
	}

	@RabbitListener(queues = "${rabbitmq.queue.task.overdue}", containerFactory = "rabbitListenerContainerFactory", ackMode = "MANUAL")
	public void receiveTaskOverdueMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		processMessage(event, channel, message);
	}

	private void processMessage(OutboxEvent event, Channel channel, Message message) throws IOException {
		// 訊息的唯一編號
		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		try {
			log.info("Received event: {}", event);

			switch (event.getEventType()) {
			case TASK_CREATED -> taskService.updateTaskStatus(event.getEntityId(), TaskStatusEnum.PROCESSINGS);
			case TASK_ASSIGNED -> log.info("Event Type : TASK_ASSIGNED");
			case TASK_OVERDUE -> log.info("Event Type : TASK_OVERDUE");
			default -> log.warn("Unhandled event type: {}", event.getEventType());
			}

			// 手動確認訊息處理成功（ACK）
			// deliveryTag：確認哪一筆訊息
			// multiple = false：只確認這一筆訊息（不批量確認）
			channel.basicAck(deliveryTag, false);

		} catch (Exception e) {
			log.error("Failed to process event: {}", event, e);
			
			// 手動拒絕訊息處理（NACK）
			// multiple = false：只拒絕這一筆
			// requeue = false：不重新放回原本隊列，會送去 DLQ
			channel.basicNack(deliveryTag, false, false);
		}
	}
}