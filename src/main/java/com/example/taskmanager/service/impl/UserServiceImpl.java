package com.example.taskmanager.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dao.UserDAO;
import com.example.taskmanager.entity.RoleEnum;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.utils.JWTUtils;

@Service
public class UserServiceImpl implements UserService{
	
	private final UserDAO userDAO;
	private final PasswordEncoder passwordEncoder;
	private final JWTUtils jwtUtils;
	
	@Autowired
	public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder, JWTUtils jwtUtils) {
		this.userDAO = userDAO;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtils = jwtUtils;
	}
	
	@Override
    public Optional<User> insertUser(User user) {
		Optional<User> userOpt = findByUsername(user.getUsername());
		
		if(userOpt.isPresent()) {
			return Optional.empty();
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(RoleEnum.USER);
		
        return userDAO.insertUser(user) > 0 ? Optional.of(user) : Optional.empty();
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
    
    @Override
    public Optional<String> login(User user){
    	Optional<User> userOpt = findByUsername(user.getUsername());
    	
    	if(!userOpt.isPresent() || !passwordEncoder.matches(user.getPassword(), userOpt.get().getPassword())) {
    		return Optional.empty();
    	}
    	
    	StringBuilder token = new StringBuilder();
    	token.append("Bearer ").append(jwtUtils.generateToken(user.getUsername()));
    	
    	return Optional.of(token.toString());
    }
}
