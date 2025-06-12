package com.example.taskmanager.notification.strategy.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEnum;
import com.example.taskmanager.notification.strategy.NotificationStrategy;

@Component
public class SlackNotificationStrategy implements NotificationStrategy{
	
	private static final Logger log = LoggerFactory.getLogger(SlackNotificationStrategy.class);
	
	private final WebClient webClient;
	
	@Autowired
	public SlackNotificationStrategy(@Qualifier("slackWebClient") WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public NotificationEnum getChannel() {
		return NotificationEnum.SLACK;
	}

	@Override
	public void send(NotificationRequest request) {
		SlackMessage payload = new SlackMessage(request.getMessage());
		
		webClient.post()
				 .contentType(MediaType.APPLICATION_JSON) // 設定 request 的 Content-Type header
				 .bodyValue(payload) // 傳送的 body 內容 ， 會自動將物件轉換成 Json
				 .retrieve() // 取回 response
				 .bodyToMono(String.class) // 將 response body 轉成 String
				 .doOnSuccess(response -> log.info("Slack Response : {}", response)) // 成功的 callback
				 .doOnError(error -> log.error("Slack Error : {}", error.getMessage())) // 失敗的 callback
				 .subscribe(); // 啟動非同步發送，上面只是設定，要有這個才會執行
		
	}
	
	/* static class
	 * 不需要也不能存取外部類別
	 * 可以當作一個普通的 class 使用，只是作用範圍限制在外部類別內
	 * */
	private static class SlackMessage {
		public String text;
		public SlackMessage(String text) {
			this.text = text;
		}
	}
}
