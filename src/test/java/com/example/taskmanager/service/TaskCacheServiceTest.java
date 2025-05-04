package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatusEnum;

@SpringBootTest
public class TaskCacheServiceTest {
	
	@Autowired
	private TaskCacheService taskCacheService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Test
	public void testSetKeyAndgetKey() {
		Task task = new Task();
		task.setId(9999999L);
		task.setTitle("Test Task Cache");
		task.setDescription("Test Task Cache Desc");
		task.setStatus(TaskStatusEnum.NEW);
		
		taskCacheService.setKey(task);
		
		String key = "task:" + task.getId();
		String cache = redisTemplate.opsForValue().get(key);
		
		assertNotNull(cache);
		assertTrue(cache.contains(task.getId().toString()));
	}
	
	@Test
	public void testDeleteKey() {
		Task task = new Task();
		task.setId(9999997L);
		task.setTitle("Test Task Cache");
		task.setDescription("Test Task Cache Desc");
		task.setStatus(TaskStatusEnum.NEW);
		
		taskCacheService.setKey(task);
		taskCacheService.deleteKey(task.getId());
		
		String key = "task:" + task.getId();
		String cache = redisTemplate.opsForValue().get(key);
		
		assertNull(cache);
	}
}
