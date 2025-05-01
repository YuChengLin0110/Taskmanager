package com.example.taskmanager.initializar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.RoleEnum;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;

import jakarta.annotation.PostConstruct;

// 每次啟動時，初始化一個新的用戶，方便測試用
@Component
public class UserDataInit{
	
	@Autowired
	private  UserService userService;
	
	@PostConstruct // 方法會在 Spring 容器初始化完畢後自動被調用
	public void userDataInit() {
		User user = new User("TestUser", "123456", "test@.com", RoleEnum.USER);
		userService.insertUser(user);
	}
}
