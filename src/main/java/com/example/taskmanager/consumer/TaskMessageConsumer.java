package com.example.taskmanager.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatusEnum;
import com.example.taskmanager.service.TaskService;

@Service
public class TaskMessageConsumer {
	
	private final TaskService taskService;
	
	public TaskMessageConsumer(TaskService taskService) {
		this.taskService = taskService;
	}
	
	@RabbitListener(queues = "${rabbitmq.queue}")
	public void receiveTaskMessage(Task task) {
		taskService.updateTaskStatus(task.getId(), TaskStatusEnum.PROCESSINGS);
	}
}