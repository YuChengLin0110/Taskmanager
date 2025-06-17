 package com.example.taskmanager.notification.resolver;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.notification.event.NotificationEvent;

// T extends NotificationEvent ： T 必須是 NotificationEvent 的 子類別 或 自己
public interface NotificationRequestResolver<T extends NotificationEvent> {
	NotificationRequest resolve(T event);
}
