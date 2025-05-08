package com.example.taskmanager.service.impl;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskCacheServiceImpl implements TaskCacheService {
	
	private static final Logger log = LoggerFactory.getLogger(TaskCacheServiceImpl.class);

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	private static final String TASK_REDIS_KEY_PREFIX = "task:";
	private static final Duration CACHE_TTL = Duration.ofMinutes(10);

	@Autowired
	public TaskCacheServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public Task getTaskById(Long id) {
		String key = TASK_REDIS_KEY_PREFIX + id;
		String json = redisTemplate.opsForValue().get(key);
		
		if(json == null) {
			return null;
		}
		
		try {
			Task task = objectMapper.readValue(json, Task.class);

			return task;

		} catch (JsonProcessingException e) {
			log.error("Failed to parse cached task JSON for Json : {}",json, e);
			throw new RuntimeException("Failed to parse cached task JSON", e);
		}
	}

	@Override
	public void setKey(Task task) {
		String key = TASK_REDIS_KEY_PREFIX + task.getId();
		try {
			String json = objectMapper.writeValueAsString(task);
			
			redisTemplate.opsForValue().set(key, json, CACHE_TTL);;
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize task to JSON for Task : {}", task, e);
			throw new RuntimeException("Failed to serialize task to JSON", e);
		}
	}

	@Override
	public void deleteKey(Long id) {
		redisTemplate.delete(TASK_REDIS_KEY_PREFIX + id);
	}
}