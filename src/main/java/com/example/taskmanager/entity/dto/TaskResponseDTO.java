package com.example.taskmanager.entity.dto;

import java.time.LocalDateTime;

import com.example.taskmanager.entity.enums.TaskStatusEnum;

public class TaskResponseDTO {

	private String title;
	private String description;
	private TaskStatusEnum status;
	private LocalDateTime createdTime;
	private LocalDateTime updatedTime;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TaskStatusEnum getStatus() {
		return status;
	}

	public void setStatus(TaskStatusEnum status) {
		this.status = status;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

}
