package com.example.taskmanager.service;

import java.util.List;
import java.util.Optional;

import com.example.taskmanager.entity.User;

public interface UserService {

	Optional<User> insertUser(User user);

	Optional<User> findById(Long id);

	Optional<User> findByUsername(String username);

	List<User> findAllUsers();

	Optional<User> updateUser(Long id, User user);

	boolean deleteUser(Long id);
	
	Optional<String> login(User user);
}
