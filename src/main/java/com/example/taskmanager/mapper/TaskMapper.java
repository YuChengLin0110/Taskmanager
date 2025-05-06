package com.example.taskmanager.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.dto.TaskRequestDTO;
import com.example.taskmanager.entity.dto.TaskResponseDTO;

@Mapper(componentModel = "spring")
public interface TaskMapper {
	
	Task taskRequestToTask(TaskRequestDTO taskRequest);
	
	TaskResponseDTO taskToTaskResponse(Task task);
	
	List<TaskResponseDTO> taskToTaskResponse (List<Task> tasks);
	
}
