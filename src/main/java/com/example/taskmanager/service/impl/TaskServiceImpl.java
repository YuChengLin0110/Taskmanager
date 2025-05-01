package com.example.taskmanager.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dao.TaskDAO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;

@Service
public class TaskServiceImpl implements TaskService {

	private final TaskDAO taskDAO;
	private final UserService userService;

	@Autowired
	public TaskServiceImpl(TaskDAO taskDAO, UserService userService) {
		this.taskDAO = taskDAO;
		this.userService = userService;
	}

	@Override
	public Optional<Task> insertTask(Task task, String username) {
		Optional<User>userOpt = userService.findByUsername(username);
		
		if(userOpt.isEmpty()) {
			return Optional.empty();
		}
		
		task.setUserId(userOpt.get().getId());
		task.setCreatedTime(LocalDateTime.now());
		task.setUpdatedTime(LocalDateTime.now());
		
		int inserted = taskDAO.insertTask(task);
		
		return inserted > 0 ? Optional.of(task) : Optional.empty();
	}

	@Override
	public Optional<Task> getTaskById(Long id) {
		return Optional.ofNullable(taskDAO.getTaskById(id));
	}

	@Override
	public List<Task> getAllTasks() {
		return taskDAO.getAllTasks();
	}

	@Override
	public Optional<Task> updateTask(Long id, Task task, Long userId) {
		Optional<Task> existingTask = getTaskById(id);
		
		if(existingTask.isPresent() && existingTask.get().getUserId().equals(userId)) {
			task.setId(id);
			int updated = taskDAO.updateTask(task);
			
			return updated > 0 ? Optional.of(task) : Optional.empty();
		}
		return Optional.empty();
	}

	@Override
	public boolean deleteTask(Long id, Long userId) {
		Optional<Task> existingTask = getTaskById(id);
		
		if(existingTask.isPresent() && existingTask.get().getUserId().equals(userId)) {
			return taskDAO.deleteTask(id) > 0;
		}
		
		return false;
	}

	@Override
	public List<Task> getTasksByUsername(String username) {
		Optional<User> userOpt = userService.findByUsername(username);
		
		if(userOpt.isEmpty()) {
			throw new UsernameNotFoundException("User not fuond");
		}
		
		return taskDAO.getTasksByUserName(userOpt.get().getUsername());
	}
}
