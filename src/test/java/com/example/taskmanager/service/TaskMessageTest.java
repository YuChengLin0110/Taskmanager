package com.example.taskmanager.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatusEnum;
import com.example.taskmanager.producer.TaskMessageProducer;
import com.example.taskmanager.service.TaskMessageTest.TaskMessageTestConfig;

@SpringBootTest
@Import(TaskMessageTestConfig.class) // 將測試配置類引入，設定需要的 Bean
public class TaskMessageTest {
	
	@Autowired
	private TaskMessageProducer taskMessageProducer;
	
	@Autowired
	private TaskService taskService;
	
	@Test
	public void testSendAndReceive() throws InterruptedException {
		Task task = new Task();
		task.setId(999996L);
		task.setTitle("TestMQTitle");
		task.setDescription("TestMQDesc");
		task.setStatus(TaskStatusEnum.NEW);
		
		taskMessageProducer.send(task);
		
		Thread.sleep(5000);
		
		//verify() 是 Mockito 提供的方法，用來檢查是否有調用 updateTaskStatus 方法，且參數匹配
		verify(taskService).updateTaskStatus(eq(task.getId()),eq(TaskStatusEnum.PROCESSINGS));
	}
	
	public static class TaskMessageTestConfig {
		
		@Bean
		public TaskService taskService() {
			
			// 使用 Mockito.spy 創建 TaskService 的間諜對象，並注入到測試中
			// spy 會保留真實對象的方法實現，但允許你監控方法的執行情況
			return Mockito.spy(TaskService.class);
		}
	}
}
