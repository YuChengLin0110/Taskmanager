package com.example.taskmanager.notification.event;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.enums.NotificationEventType;

public class TaskCreatedEvent extends AbstractNotificationEvent<Task>{
	
	public TaskCreatedEvent(Task source) {
		super(source); // 呼叫父類別 AbstractNotificationEvent 的建構子，把 Task 存起來，當作事件的來源資料
	}

	@Override
	public String getMessage() {
		return "任務已建立 : " + getSource().getTitle(); // getSource() 是父類別提供的，可以拿到當初傳進來的 Task 物件
	}

	@Override
	public String getKafkaTopic() {
		return "notification-topic";
	}

	@Override
	public NotificationEventType getNotificationEventType() {
		return NotificationEventType.TASK_CREATED;
	}
}