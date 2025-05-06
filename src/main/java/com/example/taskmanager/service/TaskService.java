package com.example.taskmanager.service;

import java.util.List;
import java.util.Optional;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.enums.TaskStatusEnum;

public interface TaskService {

	Optional<Task> insertTask(Task task, String username);

	Optional<Task> getTaskById(Long id);

	List<Task> getAllTasks();

	Optional<Task> updateTask(Long id, Task task, Long userId);

	boolean deleteTask(Long id, Long userId);
	
	List<Task> getTasksByUsername(String username);
	
	boolean updateTaskStatus(Long id, TaskStatusEnum status);
}
