package com.example.taskmanager.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.entity.ApiResponse;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.dto.LoginRequestDTO;
import com.example.taskmanager.entity.dto.RegisterRequestDTO;
import com.example.taskmanager.entity.dto.UserResponseDTO;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserService userService;
	private final UserMapper userMapper;

	@Autowired
	public AuthController(UserService userService, UserMapper userMapper) {
		this.userService = userService;
		this.userMapper = userMapper;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO) {
		User user = userMapper.registerRequestToUser(registerRequestDTO);
		Optional<User> userOpt = userService.insertUser(user);

		if (userOpt.isEmpty()) {
			return ResponseEntity.badRequest().body(ApiResponse.fail("Username is already exist"));
		} else {
			UserResponseDTO userResp = userMapper.userToUserResponse(userOpt.get());

			return ResponseEntity.status(HttpStatus.CREATED)
					.body(ApiResponse.success("User created successfully", userResp));
		}
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<String>> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {

		User user = userMapper.loginRequestToUser(loginRequestDTO);

		Optional<String> tokenOpt = userService.login(user);

		if (tokenOpt.isPresent()) {

			return ResponseEntity.ok(ApiResponse.success(tokenOpt.get()));
		} else {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.fail("User not found"));
		}
	}
}
