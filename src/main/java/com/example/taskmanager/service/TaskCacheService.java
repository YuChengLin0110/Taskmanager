package com.example.taskmanager.service;

import com.example.taskmanager.entity.Task;

public interface TaskCacheService {
	
	Task getTaskById(Long id);
	
	void setKey(Task task);
	
	void deleteKey(Long id);
}
