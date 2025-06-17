package com.example.taskmanager.entity.dto;

import com.example.taskmanager.entity.enums.NotificationEventType;

public class NotificationRequest {
	
	private String message;
	private NotificationEventType eventType;
	private String to;
	private String subject;
	
	public NotificationRequest(String message, NotificationEventType eventType, String to, String subject) {
        this.message = message;
        this.eventType = eventType;
        this.to = to;
        this.subject = subject;
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
	
}
