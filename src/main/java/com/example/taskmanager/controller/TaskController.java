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

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

	private final TaskService taskService;
	private final UserService userService;

	@Autowired
	public TaskController(TaskService taskService, UserService userService) {
		this.taskService = taskService;
		this.userService = userService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
		Optional<Task> task = taskService.getTaskById(id);

		if (task.isPresent()) {
			return ResponseEntity.ok(task.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/all")
	public ResponseEntity<List<Task>> getAllTasks() {
		List<Task> tasks = taskService.getAllTasks();

		return ResponseEntity.ok(tasks);
	}
	
	@GetMapping("/user")
	public ResponseEntity<List<Task>> getUserTasks(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		List<Task> tasks = taskService.getTasksByUsername(username);
		
		return ResponseEntity.ok(tasks);
	}
	
	@GetMapping("/username/{username}")
	public ResponseEntity<List<Task>> getTaskByUsername(@PathVariable String username){
		List<Task> tasks = taskService.getTasksByUsername(username);
		
		return ResponseEntity.ok(tasks);
	}
	
	@GetMapping("/my")
	public ResponseEntity<List<Task>> getMyTasks(Principal principal){
		String username = principal.getName();
		List<Task> tasks = taskService.getTasksByUsername(username);
		
		return ResponseEntity.ok(tasks);
	}

	@PostMapping
	public ResponseEntity<?> createTask(@RequestBody Task task, Principal principal) {
	
		Optional<Task> taskOpt = taskService.insertTask(task, principal.getName());

		return taskOpt.isPresent() 
				? ResponseEntity.status(HttpStatus.CREATED).body(taskOpt.get())
				: ResponseEntity.badRequest().body("Failed to create task");
	}

	@PutMapping("/{id}")
	public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task, Principal principal) {
		Long userId = userService.findByUsername(principal.getName()).get().getId();
		
		Optional<Task> updated = taskService.updateTask(id, task, userId);

		if (updated.isPresent()) {
			return ResponseEntity.ok(task);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id, Principal principal) {
		Long userId = userService.findByUsername(principal.getName()).get().getId();
		
		boolean deleted = taskService.deleteTask(id, userId);

		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
