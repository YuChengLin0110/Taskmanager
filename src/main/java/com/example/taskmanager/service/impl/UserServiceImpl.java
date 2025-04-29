package com.example.taskmanager.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dao.UserDAO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	private final UserDAO userDAO;
	
	@Autowired
	public UserServiceImpl(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
    public User insertUser(User user) {
        
        userDAO.insertUser(user);
        
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userDAO.findById(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userDAO.findByUsername(username));
    }

    @Override
    public List<User> findAllUsers() {
        return userDAO.findAllUsers();
    }

    @Override
    public Optional<User> updateUser(Long id, User user) {
    	user.setId(id);
    	
        Optional<User> existingUser = findById(user.getId());
        
        if (!existingUser.isPresent()) {
            return Optional.empty();
        }
        
        int updated = userDAO.updateUser(user);
        
        return updated > 0 ? Optional.of(user) : Optional.empty();
    }

    @Override
    public boolean deleteUser(Long id) {
        Optional<User> existingUser = findById(id);
        
        if (!existingUser.isPresent()) {
            return false;
        }
        
        return userDAO.deleteUser(id) > 0;
    }
}
