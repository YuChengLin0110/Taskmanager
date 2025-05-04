package com.example.taskmanager.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.taskmanager.dao.UserDAO;
import com.example.taskmanager.entity.RegisterRequestDTO;
import com.example.taskmanager.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc // 設定測試環境時自動配置 MockMvc，用於模擬 HTTP 請求
@SpringBootTest // 啟動 Spring Boot 測試環境，並加載整個應用程式的上下文
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc; // 用於模擬 HTTP 請求的工具
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Test
	void testRegisterSuccess() throws Exception {
		RegisterRequestDTO requestDTO = new RegisterRequestDTO();
        requestDTO.setUsername("test1");
        requestDTO.setPassword("123456");
        requestDTO.setEmail("test@test.com");
        
        mockMvc.perform(post("/auth/register")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(objectMapper.writeValueAsString(requestDTO)))
        	.andExpect(status().isCreated())
        	.andExpect(jsonPath("$.success").value(true)) // 驗證回應的 JSON 中 success 欄位的值是否為 true
        	.andExpect(jsonPath("$.data.username").value("test1`"));
	}
	
	@Test
	void testRegisterFail() throws Exception {
		User user = new User();
		user.setUsername("test2");
		user.setPassword("123456");
		userDAO.insertUser(user);
		
		RegisterRequestDTO request = new RegisterRequestDTO();
		request.setUsername("test2");
		request.setPassword("123");
		
		mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false));
	}
}
