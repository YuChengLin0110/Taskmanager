package com.example.taskmanager.service;

import java.util.List;
import java.util.Optional;

import com.example.taskmanager.entity.Task;

public interface TaskService {

	Task insertTask(Task task);

	Optional<Task> getTaskById(Long id);

	List<Task> getAllTasks();

	Optional<Task> updateTask(Long id, Task task);

	boolean deleteTask(Long id);
}
