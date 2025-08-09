 package com.example.taskmanager.notification.resolver;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.notification.event.AbstractNotificationEvent;

public interface NotificationRequestResolver<T extends AbstractNotificationEvent<?>> {
	NotificationRequest resolve(T event);
}
