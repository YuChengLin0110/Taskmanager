package com.example.taskmanager.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskmanager.dao.TaskDAO;
import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.enums.EventTypeEnum;
import com.example.taskmanager.entity.enums.TaskStatusEnum;
import com.example.taskmanager.service.OutboxEventService;
import com.example.taskmanager.service.TaskCacheService;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskServiceImpl implements TaskService {
	
	private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
	private final TaskDAO taskDAO;
	private final UserService userService;
	private final TaskCacheService taskCacheService;
	private final OutboxEventService outboxEventService;
	private final ObjectMapper objectMapper;

	@Autowired
	public TaskServiceImpl(TaskDAO taskDAO, UserService userService, TaskCacheService taskCacheService,
			OutboxEventService outboxEventService, ObjectMapper objectMapper) {
		this.taskDAO = taskDAO;
		this.userService = userService;
		this.taskCacheService = taskCacheService;
		this.outboxEventService = outboxEventService;
		this.objectMapper = objectMapper;
	}

	@Override
	@Transactional // 確保所有操作都成功，不然回滾，保證資料的一致性
	public Optional<Task> insertTask(Task task, String username) {
		Optional<User> userOpt = userService.findByUsername(username);

		if (userOpt.isEmpty()) {
			log.warn("User not found for username : {}", username);
			return Optional.empty();
		}

		task.setUserId(userOpt.get().getId());
		task.setStatus(TaskStatusEnum.NEW.name());
		task.setCreatedTime(LocalDateTime.now());
		task.setUpdatedTime(LocalDateTime.now());

		int inserted = taskDAO.insertTask(task);
		log.debug("Inserted task ? {} | Task : {}", inserted > 0, task);

		if (inserted > 0) {

			try {
				// 如果插入 Task 成功，創建 Outbox ，之後由 scheduler 處理發送至 MQ ，避免訊息丟失，保證資料的一致性
				outboxEventService.createEvent(getOutBoxEvent(task));
				log.info("Created outboxEvent for task id : {}", task.getId());
			} catch (Exception e) {
				log.error("Failed to create outboxEvent for task id : {}", task.getId(), e);
				throw new RuntimeException("Failed to serialize task for Outbox Event", e);
			}

			return Optional.of(task);
		}
		log.warn("Failed to insert task : {}", task);
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

	private OutboxEvent getOutBoxEvent(Task task) {
		try {
			OutboxEvent event = new OutboxEvent();
			event.setEntityId(task.getId());
			event.setPayload(objectMapper.writeValueAsString(task));
			event.setEventType(EventTypeEnum.TASK_CREATED.name());
			return event;
		}catch (JsonProcessingException e){
			log.error("Failed to convert task to JSON for outboxEvent: {}", task, e);
			throw new RuntimeException("JSON serialize error", e);
		}
	}
}