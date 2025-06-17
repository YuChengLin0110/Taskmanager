package com.example.taskmanager.notification.publisher;

import com.example.taskmanager.notification.event.NotificationEvent;

public interface NotificationEventPublisher {
	void publish (NotificationEvent event);
}
