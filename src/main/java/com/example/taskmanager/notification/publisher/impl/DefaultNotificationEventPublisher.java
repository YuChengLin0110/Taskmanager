package com.example.taskmanager.notification.publisher.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.notification.publisher.NotificationEventPublisher;

/*
 * @ConditionalOnProperty
 * name = "notification.mode" 表示要看 Spring Boot 配置裡的 notification.mode
 * havingValue = "default" 當 notification.mode 的值是 "default" 時，這個 Bean 才會被載入
 * matchIfMissing = true 預設值是啟用這個 Bean
 * */
@Component
@ConditionalOnProperty(name = "notification.mode", havingValue = "default", matchIfMissing = true)
public class DefaultNotificationEventPublisher implements NotificationEventPublisher{
	
	// Spring 內建的事件發布工具
	private final ApplicationEventPublisher publisher;
	
	@Autowired
	public DefaultNotificationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

	@Override
	public void publish(NotificationRequest request) {
		
		// 呼叫 Spring 的事件發布功能
		publisher.publishEvent(request);
	}
}
