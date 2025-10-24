package com.example.taskmanager.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.dto.ApiResponse;
import com.example.taskmanager.entity.dto.TaskRequestDTO;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.service.BatchOutboxService;
import com.example.taskmanager.service.UserService;

@RestController
@RequestMapping("tasks/batch")
public class BatchTaskController {
	
	private final BatchOutboxService batchOutboxService;
	private final TaskMapper taskMapper;
	
	@Autowired
	public BatchTaskController(BatchOutboxService batchtaskService, TaskMapper taskMapper) {
		this.batchOutboxService = batchtaskService;
		this.taskMapper = taskMapper;
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<String>> insertTasks(@RequestBody List<TaskRequestDTO> taskReqDTOs, Principal principal) {
		if(taskReqDTOs == null || taskReqDTOs.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Request list is empty"));
        }
		
		List<Task> tasks = new ArrayList<>();
		
		for(TaskRequestDTO dto : taskReqDTOs) {
			Task task = taskMapper.taskRequestToTask(dto);
			tasks.add(task);
		}
		
		Long batchId = batchOutboxService.insert(tasks, principal.getName());
		
		
		return ResponseEntity.ok(ApiResponse.success("Batch created, id = " + batchId));
	}

}
