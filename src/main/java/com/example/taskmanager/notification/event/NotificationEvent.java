package com.example.taskmanager.notification.event;

import com.example.taskmanager.entity.enums.NotificationEventType;

public interface NotificationEvent<T> {
	
	T getSource();
	String getMessage();
	String getKafkaTopic();
	NotificationEventType getNotificationEventType();
}
