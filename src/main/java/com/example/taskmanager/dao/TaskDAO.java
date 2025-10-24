package com.example.taskmanager.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.enums.TaskStatusEnum;

@Mapper
public interface TaskDAO {

	int insertTask(Task task);

	Task getTaskById(Long id);

	List<Task> getAllTasks();

	int updateTask(Task task);

	int deleteTask(Long id);
	
	List<Task> getTasksByUserId(Long userId);
	
	List<Task> getTasksByUserName(String username);
	
	int updateTaskStatus(Long id, String status);
	
	int batchInsertForeach(List<Task> tasks);
}
