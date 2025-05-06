package com.example.taskmanager.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.dto.LoginRequestDTO;
import com.example.taskmanager.entity.dto.RegisterRequestDTO;
import com.example.taskmanager.entity.dto.UpdateUserRequestDTO;
import com.example.taskmanager.entity.dto.UserResponseDTO;

// componentModel = "spring" 將生成的 Mapper 實現類註冊到 Spring 容器中，方便之後使用
@Mapper(componentModel = "spring")
public interface UserMapper {
	
	// 從 LoginRequestDTO 轉換到 User
	User loginRequestToUser(LoginRequestDTO loginRequestDTO);
	
	// 從 RegisterRequestDTO 轉換到 User
	User registerRequestToUser(RegisterRequestDTO registerRequestDTO);
	
	User updateRequestToUser(UpdateUserRequestDTO updateUserRequestDTO);
	
	UserResponseDTO userToUserResponse(User user);
	
	List<UserResponseDTO> UserToUserResponse(List<User> users);
}