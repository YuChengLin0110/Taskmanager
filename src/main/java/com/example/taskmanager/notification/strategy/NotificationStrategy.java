package com.example.taskmanager.notification.strategy;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEnum;

public interface NotificationStrategy {
	NotificationEnum getChannel();
	void send(NotificationRequest request);
}
