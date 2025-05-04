package com.example.taskmanager.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dao.TaskDAO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatusEnum;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.producer.TaskMessageProducer;
import com.example.taskmanager.service.TaskCacheService;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;

@Service
public class TaskServiceImpl implements TaskService {

	private final TaskDAO taskDAO;
	private final UserService userService;
	private final TaskCacheService taskCacheService;
	private final TaskMessageProducer taskMessageProducer;

	@Autowired
	public TaskServiceImpl(TaskDAO taskDAO, UserService userService, TaskCacheService taskCacheService,
			TaskMessageProducer taskMessageProducer) {
		this.taskDAO = taskDAO;
		this.userService = userService;
		this.taskCacheService = taskCacheService;
		this.taskMessageProducer = taskMessageProducer;
	}

	@Override
	public Optional<Task> insertTask(Task task, String username) {
		Optional<User> userOpt = userService.findByUsername(username);

		if (userOpt.isEmpty()) {
			return Optional.empty();
		}

		task.setUserId(userOpt.get().getId());
		task.setCreatedTime(LocalDateTime.now());
		task.setUpdatedTime(LocalDateTime.now());

		int inserted = taskDAO.insertTask(task);

		if (inserted > 0) {
			taskMessageProducer.send(task);

			return Optional.of(task);
		}

		return Optional.empty();
	}

	@Override
	public Optional<Task> getTaskById(Long id) {
		Task cache = getTaskFromCache(id);

		if (cache != null) {
			return Optional.of(cache);
		}

		Task task = taskDAO.getTaskById(id);

		if (task != null) {

			setTaskToCache(task);

			return Optional.of(task);
		}

		return Optional.empty();
	}

	@Override
	public List<Task> getAllTasks() {
		return taskDAO.getAllTasks();
	}

	@Override
	public Optional<Task> updateTask(Long id, Task task, Long userId) {
		Optional<Task> existingTask = getTaskById(id);

		if (existingTask.isPresent() && existingTask.get().getUserId().equals(userId)) {
			task.setId(id);
			task.setUpdatedTime(LocalDateTime.now());

			int updated = taskDAO.updateTask(task);

			if (updated > 0) {
				taskCacheService.deleteKey(id);

				return Optional.of(task);
			}
		}
		return Optional.empty();
	}

	@Override
	public boolean deleteTask(Long id, Long userId) {
		Optional<Task> existingTask = getTaskById(id);

		if (existingTask.isPresent() && existingTask.get().getUserId().equals(userId)) {

			if (taskDAO.deleteTask(id) > 0) {
				taskCacheService.deleteKey(id);

				return true;
			}
		}

		return false;
	}

	@Override
	public List<Task> getTasksByUsername(String username) {
		Optional<User> userOpt = userService.findByUsername(username);

		if (userOpt.isEmpty()) {
			throw new UsernameNotFoundException("User not fuond");
		}

		return taskDAO.getTasksByUserName(userOpt.get().getUsername());
	}

	@Override
	public boolean updateTaskStatus(Long id, TaskStatusEnum status) {
		int update = taskDAO.updateTaskStatus(id, status);

		return update > 0;
	}

	private Task getTaskFromCache(Long id) {
		try {
			return taskCacheService.getTaskById(id);
		} catch (RuntimeException e) {
			// Log 錯誤，並返回 null
			return null;
		}
	}

	private void setTaskToCache(Task task) {
		try {
			taskCacheService.setKey(task);
		} catch (RuntimeException e) {
			// Log 錯誤
		}
	}
}
