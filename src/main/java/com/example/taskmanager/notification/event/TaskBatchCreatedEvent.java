package com.example.taskmanager.notification.event;

import java.util.List;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.enums.NotificationEventType;

public class TaskBatchCreatedEvent extends AbstractNotificationEvent<List<Task>>{

	public TaskBatchCreatedEvent(List<Task> source) {
		super(source);
	}

	@Override
	public String getMessage() {
		return "批量任務已建立";
	}

	@Override
	public String getKafkaTopic() {
		return "notification-topic";
	}

	@Override
	public NotificationEventType getNotificationEventType() {
		return NotificationEventType.TASK_BATCH_CREATED;
	}

}
