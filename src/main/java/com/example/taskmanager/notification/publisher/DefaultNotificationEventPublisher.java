package com.example.taskmanager.notification.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.taskmanager.notification.event.NotificationEvent;

@Component
public class DefaultNotificationEventPublisher implements NotificationEventPublisher{
	
	// Spring 內建的事件發布工具
	private final ApplicationEventPublisher publisher;
	
	@Autowired
	public DefaultNotificationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

	@Override
	public void publish(NotificationEvent event) {
		
		// 呼叫 Spring 的事件發布功能
		publisher.publishEvent(event);
	}
}
