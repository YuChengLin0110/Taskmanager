package com.example.taskmanager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisDistributedLock {
	String key(); // 支援 SpEL 範例： @DistributedLock(key = "'task:lock:' + #taskId")
	long waitTime() default 5; //嘗試獲取鎖的最大等待時間
	long leaseTime() default 10; // 鎖自動過期的時間
}