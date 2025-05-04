package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.taskmanager.dao.UserDAO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.impl.UserServiceImpl;

/**
 * 當使用 @Mock 時，Mockito 會創建一個模擬對象
 * 如果沒有定義行為，這些模擬對象的方法不會執行任何操作
 * 要使模擬對象的行為生效，需使用 when() 配合 thenReturn() 設置方法的返回值
 * */
@ExtendWith(MockitoExtension.class) // 讓測試類使用 Mockito 進行擴展，啟用 Mockito 功能
public class UserServiceTest {
	
	@Mock
	private UserDAO userDAO;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	@Test
	void insertUser_returnEmpty() {
		User user = new User();
		user.setUsername("test");
		
		when(userDAO.findByUsername("test")).thenReturn(user);
		
		Optional<User> userOpt = userService.insertUser(user);
		assertTrue(userOpt.isEmpty());
	}
	
	@Test
	void insertUser_saveAndReturnUser() {
		User user = new User();
		user.setUsername("test");
		user.setPassword("123456");
		
		when(userDAO.findByUsername("test")).thenReturn(null);
		when(passwordEncoder.encode("123456")).thenReturn("123");
		when(userDAO.insertUser(any())).thenReturn(1);
		
		Optional<User> userOpt = userService.insertUser(user);
		
		assertTrue(userOpt.isPresent());
		assertEquals("123", userOpt.get().getPassword());
	}
}
