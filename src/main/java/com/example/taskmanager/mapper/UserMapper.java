package com.example.taskmanager.mapper;

import org.mapstruct.Mapper;

import com.example.taskmanager.entity.LoginRequestDTO;
import com.example.taskmanager.entity.RegisterRequestDTO;
import com.example.taskmanager.entity.User;

// componentModel = "spring" 將生成的 Mapper 實現類註冊到 Spring 容器中，方便之後使用
@Mapper(componentModel = "spring")
public interface UserMapper {
	
	// 從 LoginRequestDTO 轉換到 User
	User loginRequestToUser(LoginRequestDTO loginRequestDTO);
	
	// 從 RegisterRequestDTO 轉換到 User
	User RegisterRequestToUser(RegisterRequestDTO refisterRequestDTO);
}