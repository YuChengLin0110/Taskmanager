package com.example.taskmanager.service.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.taskmanager.service.RedisLockService;

@Service
public class RedisLockServiceImpl implements RedisLockService{
	
	private final StringRedisTemplate redisTemplate;
	
	@Autowired
	public RedisLockServiceImpl(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public String tryLock(String key, long expireMillis) {
		// 生成唯一的鎖值，用來標識鎖的擁有者
		String value = UUID.randomUUID().toString();
		
		// setIfAbsent：只有當 key 不存在時，才會設置 key 值並設置過期時間
		// setIfAbsent 回傳 Boolean 因為有特殊情況可能會回傳 null
		Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, expireMillis, TimeUnit.MILLISECONDS);
		
		return Boolean.TRUE.equals(success) ? value : null;
	}
	
	// 解鎖，只有鎖的持有者能解鎖
	@Override
	public void unLock(String key, String value) {
		String currentValue = redisTemplate.opsForValue().get(key);
		
		// 確保只有持有者能解鎖
		if(value.equals(currentValue)) {
			redisTemplate.delete(key);
		}
	}
	
	@Override
	public StringRedisTemplate getRedisTemplate() {
		return redisTemplate;
	}
}