package com.example.taskmanager.notification.resolver;

import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEventType;
import com.example.taskmanager.notification.event.TaskCreatedEvent;
import com.example.taskmanager.service.UserService;

/*
 * 組裝必要資訊
 * */
@Component
public class TaskCreatedNotificationResolver implements NotificationRequestResolver<TaskCreatedEvent>{
	
	private final UserService userService;
	
	@Autowired
	public TaskCreatedNotificationResolver(UserService userService) {
		this.userService = userService;
	}

	@Override
	public NotificationRequest resolve(TaskCreatedEvent event) {
		
		// 取得傳入的物件
		Task task = event.getSource();
		Long userId = task.getUserId();
		String email = Strings.EMPTY;
		Optional<User> user = userService.findById(userId);
		
		if(user.isPresent()) {
			email = user.get().getEmail();
		}
		
		return new NotificationRequest(event.getMessage(), NotificationEventType.TASK_CREATED, email, "新任務通知：" + task.getTitle());
	}
}
