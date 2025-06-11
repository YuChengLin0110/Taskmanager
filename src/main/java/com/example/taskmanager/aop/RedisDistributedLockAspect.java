package com.example.taskmanager.aop;


import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.taskmanager.annotation.RedisDistributedLock;
import com.example.taskmanager.utils.SpELUtils;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@Component
public class RedisDistributedLockAspect {
	
	private static final Logger log = LoggerFactory.getLogger(RedisDistributedLockAspect.class);
	
	private final RedissonClient redissonClient;
	
	@Autowired
	public RedisDistributedLockAspect(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}
	
	// 只攔截有加上 @RedisDistributedLock 的方法
	// 這裡的 lockAnnotation 是對應下面方法參數的名稱
	@Around("@annotation(lockAnnotation)")
	public Object redisLock(ProceedingJoinPoint joinPoint, RedisDistributedLock lockAnnotation) throws Throwable {
		
		// 取得當前執行的方法物件
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		
		String key = SpELUtils.parse(lockAnnotation.key(), method, joinPoint.getArgs());
		
		RLock rLock = redissonClient.getLock(key);
		boolean locked = false;
		
		try {
			locked = rLock.tryLock(lockAnnotation.waitTime(), lockAnnotation.leaseTime(), TimeUnit.SECONDS);
			
			if(locked) {
				log.info("獲取鎖 key = {}", key);
				return joinPoint.proceed(); // 執行原本的方法
			}else {
				throw new RuntimeException("無法獲取鎖 key = " + key);
			}
		}finally {
			if(locked && rLock.isHeldByCurrentThread()) {
				rLock.unlock();
			}
		}
	}
}