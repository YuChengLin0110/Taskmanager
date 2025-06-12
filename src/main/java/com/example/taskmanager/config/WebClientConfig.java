package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	private final String slackWebhookUrl = "";
	
	@Bean
	public WebClient slackWebClient() {
		// 利用 WebClient builder 建立一個基底 URL 為 slackWebhookUrl 的 WebClient
		return WebClient.builder().baseUrl(slackWebhookUrl).build();
	}
}
