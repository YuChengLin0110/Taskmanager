package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.taskmanager.config.RabbitMQConfig;
import com.example.taskmanager.consumer.TaskMessageConsumer;
import com.example.taskmanager.dao.TaskDAO;
import com.example.taskmanager.dao.UserDAO;
import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.enums.TaskStatusEnum;
import com.example.taskmanager.producer.TaskMessageProducer;
import com.example.taskmanager.scheduler.OutboxEventScheduler;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskServiceIntegrationTest {
	
	@Autowired
    private TaskService taskService;

    @Autowired
    private TaskDAO taskDAO;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private OutboxEventScheduler outboxEventScheduler;
    
    @BeforeEach
    void setUp() {
    	User user = new User();
    	user.setUsername("testUser");
    	user.setPassword("123456");
    	user.setEmail("test@test.com");
    	userDAO.insertUser(user);
    }
	
	@Test
	public void testSendAndReceive() throws InterruptedException {
		Task task = new Task();
		task.setId(999996L);
		task.setTitle("TestMQTitle");
		task.setDescription("TestMQDesc");
		task.setStatus(TaskStatusEnum.NEW);
		String username = "testUser";
		
		Optional<Task> inserted = taskService.insertTask(task, username);
		
		assertTrue(inserted.isPresent());
		
		Long taskId = inserted.get().getId();
		
		outboxEventScheduler.processOutboxEvents();
		
		Thread.sleep(3000);
		
		Task updated = taskDAO.getTaskById(taskId);
		
		assertEquals(TaskStatusEnum.PROCESSINGS.name(), updated.getStatus());
	}
}