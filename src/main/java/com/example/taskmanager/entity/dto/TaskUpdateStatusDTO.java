package com.example.taskmanager.entity.dto;

import com.example.taskmanager.entity.enums.TaskStatusEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class TaskUpdateStatusDTO {
	private Long id;

	@Enumerated(EnumType.STRING)
	private TaskStatusEnum status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TaskStatusEnum getStatus() {
		return status;
	}

	public void setStatus(TaskStatusEnum status) {
		this.status = status;
	}

}
