package com.example.taskmanager.service;

import java.util.List;

import com.example.taskmanager.entity.BatchOutbox;
import com.example.taskmanager.entity.Task;

public interface BatchOutboxService {
	Long insert(List<Task> tasks, String username);
	
	List<BatchOutbox> findPending(int limit);
	
	void update(BatchOutbox batchOutbox);
	
	void markAsFailed(BatchOutbox batchOutbox, int retryMax);
}
