package com.example.taskmanager.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.taskmanager.entity.User;

@Mapper
public interface UserDAO {

	int insertUser(User user);

	User findById(Long id);

	User findByUsername(String username);

	List<User> findAllUsers();

	int updateUser(User user);

	int deleteUser(Long id);
}
