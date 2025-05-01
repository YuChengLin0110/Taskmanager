package com.example.taskmanager.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.entity.LoginRequestDTO;
import com.example.taskmanager.entity.RegisterRequestDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.service.UserService;

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
	public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
		User user = userMapper.RegisterRequestToUser(registerRequestDTO);
		Optional<User> userOpt = userService.insertUser(user);

		if (!userOpt.isPresent()) {
			return ResponseEntity.badRequest().body("Username is already exist");
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {

		User user = userMapper.loginRequestToUser(loginRequestDTO);

		Optional<String> tokenOpt = userService.login(user);

		if (tokenOpt.isPresent()) {
			return ResponseEntity.ok(tokenOpt.get());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
