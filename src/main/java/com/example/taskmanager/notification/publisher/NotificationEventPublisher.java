package com.example.taskmanager.notification.publisher;

import com.example.taskmanager.entity.dto.NotificationRequest;

public interface NotificationEventPublisher {
	void publish (NotificationRequest request);
}
