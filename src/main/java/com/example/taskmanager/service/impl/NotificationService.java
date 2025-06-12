package com.example.taskmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEnum;
import com.example.taskmanager.notification.NotificationStrategyFactory;
import com.example.taskmanager.notification.strategy.NotificationStrategy;

@Service
public class NotificationService {
	
	private final NotificationStrategyFactory strategyFactory;
	
	@Autowired
	public NotificationService(NotificationStrategyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}
	
	public void notify(NotificationEnum channel, NotificationRequest request) {
		NotificationStrategy strategy = strategyFactory.getStrategy(channel);
		strategy.send(request);
	}
}
