package com.example.taskmanager.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.taskmanager.dao.TaskDAO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;

public class TaskServiceImpl implements TaskService {

	private final TaskDAO taskDAO;

	@Autowired
	public TaskServiceImpl(TaskDAO taskDAO) {

		this.taskDAO = taskDAO;
	}

	@Override
	public Task insertTask(Task task) {
		taskDAO.insertTask(task);
		return task;
	}

	@Override
	public Optional<Task> getTaskById(Long id) {
		return Optional.ofNullable(taskDAO.getTaskById(id));
	}

	@Override
	public List<Task> getAllTasks() {
		return taskDAO.getAllTasks();
	}

	@Override
	public Optional<Task> updateTask(Long id, Task task) {
		task.setId(id);
		
		Optional<Task> existingTask = getTaskById(id);
		
		if(!existingTask.isPresent()) {
			return Optional.empty();
		}
		
		int updated = taskDAO.updateTask(task);
		
		return updated > 0 ? Optional.of(task) : Optional.empty();
	}

	@Override
	public boolean deleteTask(Long id) {
		Optional<Task> existing = getTaskById(id);
		
		if(!existing.isPresent()) {
			return false;
		}
		
		return taskDAO.deleteTask(id) > 0;
	}
}
