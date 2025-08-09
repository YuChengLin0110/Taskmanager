package com.example.taskmanager.entity.dto;

import com.example.taskmanager.entity.enums.NotificationEventType;

public class NotificationRequest {
	
	private String message;
	private NotificationEventType eventType;
	private String to;
	private String subject;
	private String kafkaTopic;
	
	/*
	 * Kafka 使用 Jackson 反序列化時，必須有一個無參數的建構子
	 * 如果沒有這個無參數建構子，Jackson 在反序列化時會失敗，拋出異常錯誤
	 * 因為 Jackson 需要先建立物件實例，再填入屬性值
	 * */
	public NotificationRequest() {
		
	}
	
	public NotificationRequest(String message, NotificationEventType eventType, String to, String subject, String kafkaTopic) {
        this.message = message;
        this.eventType = eventType;
        this.to = to;
        this.subject = subject;
        this.kafkaTopic = kafkaTopic;
    }
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public NotificationEventType getEventType() {
		return eventType;
	}
	public void setEventType(NotificationEventType eventType) {
		this.eventType = eventType;
	}

	public String getKafkaTopic() {
		return kafkaTopic;
	}

	public void setKafkaTopic(String kafkaTopic) {
		this.kafkaTopic = kafkaTopic;
	}
	
}
