package com.example.taskmanager.notification.resolver;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEventType;
import com.example.taskmanager.notification.event.TaskBatchCreatedEvent;
import com.example.taskmanager.service.UserService;

public class TaskBatchNotificationResolver implements NotificationRequestResolver<TaskBatchCreatedEvent>{

private final UserService userService;
	
	@Autowired
	public TaskBatchNotificationResolver(UserService userService) {
		this.userService = userService;
	}
	
	@Override
	public NotificationRequest resolve(TaskBatchCreatedEvent event) {
		List<Task> tasks = event.getSource();
		Task task = tasks.get(0);
		Long userId = task.getUserId();
		String email = Strings.EMPTY;
		Optional<User> user = userService.findById(userId);
		
		if(user.isPresent()) {
			email = user.get().getEmail();
		}
		
		String message = event.getMessage();
		NotificationEventType eventType = event.getNotificationEventType();
		String subject =  "批量任務建立通知";
		String kafkaTopic = event.getKafkaTopic();
		
		
		return new NotificationRequest(message,eventType, email, subject, kafkaTopic);
	}
}
