package com.example.taskmanager.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.dataSource.DataSourceContextHolder;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.dto.ApiResponse;
import com.example.taskmanager.entity.dto.RegisterRequestDTO;
import com.example.taskmanager.entity.dto.UpdateUserRequestDTO;
import com.example.taskmanager.entity.dto.UserResponseDTO;
import com.example.taskmanager.entity.enums.DataSourceType;
import com.example.taskmanager.mapper.UserMapper;
import com.example.taskmanager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;

	@Autowired
	public UserController(UserService userService, UserMapper userMapper) {
		this.userService = userService;
		this.userMapper = userMapper;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
		Optional<User> userOpt = userService.findById(id);

		if (userOpt.isPresent()) {
			UserResponseDTO userResp = getUserRespDTO(userOpt.get());
			return ResponseEntity.ok(ApiResponse.success(userResp));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("User not found"));
		}
	}

	@GetMapping("/username/{username}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> getUserByUsername(@PathVariable String username) {
		Optional<User> userOpt = userService.findByUsername(username);

		if (userOpt.isPresent()) {
			UserResponseDTO userResp = getUserRespDTO(userOpt.get());
			
			return ResponseEntity.ok(ApiResponse.success(userResp));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("User not found"));
		}
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
		List<User> users = userService.findAllUsers();
		
		List<UserResponseDTO> userResps = userMapper.UserToUserResponse(users);
		
		return ResponseEntity.ok(ApiResponse.success(userResps));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@RequestBody @Valid RegisterRequestDTO userReq) {
		User user = userMapper.registerRequestToUser(userReq);
		Optional<User> userOpt = userService.insertUser(user);

		if (userOpt.isPresent()) {
			UserResponseDTO userResp = getUserRespDTO(userOpt.get());
			
			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userResp));
		} else {
			return ResponseEntity.badRequest().body(ApiResponse.fail("Username already exists"));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDTO userReq) {
		User user = userMapper.updateRequestToUser(userReq);
		Optional<User> userOpt = userService.updateUser(id, user);

		if (userOpt.isPresent()) {
			UserResponseDTO userResp = getUserRespDTO(userOpt.get());
			
			return ResponseEntity.ok(ApiResponse.success(userResp));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("User not found"));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
		boolean deleted = userService.deleteUser(id);

		if (deleted) {
			return ResponseEntity.ok(ApiResponse.success("Successfully deleted"));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("User not found"));
		}
	}
	
	private UserResponseDTO getUserRespDTO(User user) {
		return userMapper.userToUserResponse(user);
	}
}
