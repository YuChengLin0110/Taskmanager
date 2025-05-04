package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	
	// 建立一個 RedisTemplate，用來操作 Redis
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		
		// 設定 Redis 連線工廠，這個工廠會根據 application.properties 中的設定，自動建立與 Redis 的連線
		template.setConnectionFactory(factory);
		
		// 建立字串序列化器，用來把 key 轉成字串格式
		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		
		// 建立 JSON 序列化器，會把 Java 物件轉成 JSON 存進 Redis，讀出來再轉回物件
		GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
		
		// 設定 key 和 value 使用的序列化器
		template.setKeySerializer(stringSerializer);
		template.setValueSerializer(jsonSerializer);;
		template.setHashKeySerializer(stringSerializer);
		template.setHashValueSerializer(jsonSerializer);
		
		// 初始化 template，套用上面的設定
		template.afterPropertiesSet();
		
		return template;
	}
}
