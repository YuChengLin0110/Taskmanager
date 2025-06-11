package com.example.taskmanager.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisDistributedLockExecutor {

	private final Logger log = LoggerFactory.getLogger(RedisDistributedLockExecutor.class);

	public final RedissonClient redissonClient;

	@Autowired
	public RedisDistributedLockExecutor(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	/**
	 * 帶回傳結果的分布式鎖執行方法
	 * 
	 *  key       分布式鎖的 key
	 *  waitTime  嘗試獲取鎖的最大等待時間
	 *  leaseTime 鎖過期時間， Redisson 會自動續期避免過早釋放
	 *  task      需要被鎖保護執行的任務
	 */
	public <T> T execute(String key, int waitTime, int leaseTime, Callable<T> task) throws Exception {
		RLock lock = redissonClient.getLock(key);
		boolean locked = false;

		try {
			locked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

			if (locked) {
				log.info("獲取鎖成功 key = {}", key);
				
				// 鎖成功後執行任務，並回傳結果
				// 任務執行過程發生錯誤會直接往外拋，呼叫者需處理
				return task.call();
			} else {
				log.warn("無法取得鎖，可能其他實例使用中");
				// 沒取得鎖，丟出 IllegalStateException
				throw new IllegalStateException("無法取得鎖，key = " + key);
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("取得鎖時被中斷", e);
		} finally {
			
			// 判斷目前執行緒是否擁有鎖，確保不會解鎖其他執行緒的鎖
			if (locked && lock.isHeldByCurrentThread()) {
				lock.unlock();
				log.info("釋放鎖成功 key = {}", key);
			}
		}
	}

	public void executeWithoutResult(String key, int waitTime, int leaseTime, Runnable task) throws Exception {
		
		// 利用 execute 方法包裝 Runnable 轉 Callable
		execute(key, waitTime, leaseTime, () -> {
			task.run();
			
			// Runnable 無回傳值，這裡必須 return null 來符合 Callable<T> 的接口
			// lambda 寫 () -> {...} ， 如果裡面沒有回傳值，Java 會推斷為 Runnable ， 有 return 就會推斷為 Callable
			return null; 
		});
	}

//	public void execute(String key, int waitTime, int leaseTime, Runnable task) {
//		try {
//			execute(key, waitTime, leaseTime, () -> {
//				task.run();
//				return null;
//			});
//		}catch (Exception e) {
//			log.error("執行鎖定任務失敗 key={}, error={}", key, e.getMessage(), e);
//		}
//	}

}