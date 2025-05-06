package com.example.taskmanager.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.enums.TaskStatusEnum;
import com.example.taskmanager.service.TaskService;

@Service
public class TaskMessageConsumer {
	
	private final TaskService taskService;
	
	public TaskMessageConsumer(TaskService taskService) {
		this.taskService = taskService;
	}
	
	// 監聽 taskCreated 訊息隊列，當有訊息進來時，會觸發這個方法
	// "${}" 會從application.properties 取值 ，類似於 @Value 的效果
	@RabbitListener(queues = "${rabbitmq.queue.taskCreated}")
	public void receiveTaskCreatedMessage(OutboxEvent event) {
		taskService.updateTaskStatus(event.getEntityId(), TaskStatusEnum.PROCESSINGS);
	}
	
	@RabbitListener(queues = "${rabbitmq.queue.taskCompleted}")
	public void receiveTaskCompletedMessage(OutboxEvent event) {
		taskService.updateTaskStatus(event.getEntityId(), TaskStatusEnum.DONE);
	}
}