package com.example.taskmanager.lock;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisLockWatchdog implements Runnable{
	private final StringRedisTemplate redisTemplate;
	private final String key;
	private final String value;
	private final long expireMillis;
	// volatile 是讓不同執行緒都能馬上看到這個變數最新的狀態
	// 這樣寫能確保別的執行緒改了 running，大家都會立刻知道
	private volatile boolean running = true;
	
	public RedisLockWatchdog(StringRedisTemplate redisTemplate, String key, String value, long expireMillis) {
		super();
		this.redisTemplate = redisTemplate;
		this.key = key;
		this.value = value;
		this.expireMillis = expireMillis;
		this.running = running;
	}

	@Override
	public void run() {
		try {
			while(running) {
				// 每過鎖的過期時間的三分之一就檢查一次
				Thread.sleep(expireMillis / 3);
				
				String currentValue = redisTemplate.opsForValue().get(key);
				
				// 確保是同一個持有者的鎖
				if(value.equals(currentValue)) {
					// 重新設置過期時間，讓鎖不會過期
					redisTemplate.expire(key, Duration.ofMillis(expireMillis));
				}else {
					// 如果鎖的值變了，表示鎖可能已經被釋放
					break;
				}
			}
		}catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	public void stop() {
		this.running = false;
	}
}
