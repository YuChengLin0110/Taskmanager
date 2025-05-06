package com.example.taskmanager.entity.dto;

import java.time.LocalDateTime;

public class OutboxEventMarkAsFailedDTO {

	private Long id;
	
	private String lastError;
	private LocalDateTime nextRetryTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	public LocalDateTime getNextRetryTime() {
		return nextRetryTime;
	}

	public void setNextRetryTime(LocalDateTime nextRetryTime) {
		this.nextRetryTime = nextRetryTime;
	}
}
