package com.example.taskmanager.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
	
	@Value("${spring.data.redis.host}")
	private String redisHost;
	
	@Value("${spring.data.redis.port}")
	private int redisPort;
	
	/* useSingleServer() 單台 Redis 伺服器
	 * useClusterServers()  Cluster 架構（例如：3主3從）
	 * useSentinelServers() Sentinel 會監控 master 、 slave 狀態，主機出問題自動切換
	 * useMasterSlaveServers() 自己部署的 Redis 主從，實現讀寫分離
	 * */
	@Bean
	public RedissonClient redissonClient() {
		// 建立 Redisson 的配置
		Config config = new Config();
		
		// 使用單一 Redis 節點模式
		config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);
		
		// 建立並回傳 Redisson Client Bean ， 之後可以 @Autowired 注入使用
		return Redisson.create(config);
	}
}