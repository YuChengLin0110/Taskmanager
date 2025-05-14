package com.example.taskmanager.service;

import org.springframework.data.redis.core.StringRedisTemplate;

public interface RedisLockService {
	
	String tryLock(String key, long expireMillis);
	
	void unLock(String key, String value);
	
	StringRedisTemplate getRedisTemplate();
}
