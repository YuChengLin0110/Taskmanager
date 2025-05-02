package com.example.taskmanager.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.entity.ApiResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskResponseDTO;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

	private final TaskService taskService;
	private final UserService userService;
	private final TaskMapper taskMapper;

	@Autowired
	public TaskController(TaskService taskService, UserService userService, TaskMapper taskMapper) {
		this.taskService = taskService;
		this.userService = userService;
		this.taskMapper = taskMapper;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<TaskResponseDTO>> getTaskById(@PathVariable Long id) {
		Optional<Task> task = taskService.getTaskById(id);

		if (task.isPresent()) {

			return ResponseEntity.ok(ApiResponse.success(getTaskRespDTO(task.get())));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("Task not found"));
		}
	}

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getAllTasks() {
		List<Task> tasks = taskService.getAllTasks();

		return ResponseEntity.ok(ApiResponse.success(getTaskRespDTO(tasks)));
	}

	@GetMapping("/user")
	public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getUserTasks() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<Task> tasks = taskService.getTasksByUsername(username);

		return ResponseEntity.ok(ApiResponse.success(getTaskRespDTO(tasks)));
	}

	@GetMapping("/username/{username}")
	public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getTaskByUsername(@PathVariable String username) {
		List<Task> tasks = taskService.getTasksByUsername(username);

		return ResponseEntity.ok(ApiResponse.success(getTaskRespDTO(tasks)));
	}

	@GetMapping("/my")
	public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getMyTasks(Principal principal) {
		String username = principal.getName();
		List<Task> tasks = taskService.getTasksByUsername(username);

		return ResponseEntity.ok(ApiResponse.success(getTaskRespDTO(tasks)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<TaskResponseDTO>> createTask(@RequestBody Task task, Principal principal) {
		Optional<Task> taskOpt = taskService.insertTask(task, principal.getName());

		if (taskOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(getTaskRespDTO(taskOpt.get())));
		} else {
			return ResponseEntity.badRequest().body(ApiResponse.fail("Failed to create task"));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTask(@PathVariable Long id, @RequestBody Task task,
			Principal principal) {
		Long userId = userService.findByUsername(principal.getName()).get().getId();
		Optional<Task> updated = taskService.updateTask(id, task, userId);

		if (updated.isPresent()) {
			return ResponseEntity.ok(ApiResponse.success(getTaskRespDTO(updated.get())));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("Task not found"));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<String>> deleteTask(@PathVariable Long id, Principal principal) {
		Long userId = userService.findByUsername(principal.getName()).get().getId();
		boolean deleted = taskService.deleteTask(id, userId);

		if (deleted) {
			return ResponseEntity.ok(ApiResponse.success("Successfully deleted"));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail("Task not found"));
		}
	}

	private TaskResponseDTO getTaskRespDTO(Task task) {
		return taskMapper.taskToTaskResponse(task);
	}

	private List<TaskResponseDTO> getTaskRespDTO(List<Task> tasks) {
		return taskMapper.taskToTaskResponse(tasks);
	}
}
