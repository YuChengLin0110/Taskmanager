package com.example.taskmanager.entity;

import jakarta.validation.constraints.NotNull;

public class TaskRequestDTO {
	
	@NotNull
	private String title;
	
	@NotNull
	private String description;

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
}
