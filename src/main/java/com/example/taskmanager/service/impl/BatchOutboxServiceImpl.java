package com.example.taskmanager.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.taskmanager.dao.BatchOutboxDAO;
import com.example.taskmanager.entity.BatchOutbox;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.entity.enums.EventStatusEnum;
import com.example.taskmanager.entity.enums.EventTypeEnum;
import com.example.taskmanager.service.BatchOutboxService;
import com.example.taskmanager.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BatchOutboxServiceImpl implements BatchOutboxService{
	private final static Logger log = LoggerFactory.getLogger(BatchOutboxServiceImpl.class);
	private final UserService userService;
	private final BatchOutboxDAO batchOutboxDAO;
	private final ObjectMapper objectMapper;
	
	@Autowired
	public BatchOutboxServiceImpl(UserService userService, BatchOutboxDAO batchOutboxDAO, ObjectMapper objectMapper) {
		this.userService = userService;
		this.batchOutboxDAO = batchOutboxDAO;
		this.objectMapper = objectMapper;
	}

	@Override
	public Long insert(List<Task> tasks, String username) {
		try {
			Optional<User> userOpt = userService.findByUsername(username);
			
			if(userOpt.isEmpty()) {
				log.warn("User not found for username : {}", username);
				throw new RuntimeException("User not found");
			}
			
			// 將 Tasks轉成 JSON 字串，用於 BatchOutbox payload
			String payload = objectMapper.writeValueAsString(tasks);
			
			// 建立 BatchOutbox
			BatchOutbox batchOutbox = new BatchOutbox();
			batchOutbox.setEventType(EventTypeEnum.TASK_BATCH_CREATED);
            batchOutbox.setPayload(payload);
            batchOutbox.setPayloadSize(payload.length());
            batchOutbox.setStatus(EventStatusEnum.PENDING);
            batchOutbox.setCreatedBy(userOpt.get().getId());
            batchOutbox.setCreatedTime(LocalDateTime.now());
            batchOutbox.setFirstTitle(tasks.get(0).getTitle());
            
            batchOutboxDAO.insert(batchOutbox);
			
			return batchOutbox.getId();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create batch", e);
		}
	}


	@Override
	public List<BatchOutbox> findPending(int limit) {
		return batchOutboxDAO.findPending(limit);
		
	}

	@Override
	public void update(BatchOutbox batchOutbox) {
		batchOutboxDAO.update(batchOutbox);
		
	}

	@Override
	public void markAsFailed(BatchOutbox batchOutbox, int retryMax) {
		if (batchOutbox.getRetryCount() >= retryMax) {
            batchOutbox.setStatus(EventStatusEnum.DEAD);
        } else {
            batchOutbox.setStatus(EventStatusEnum.FAILED);
            batchOutbox.setNextRetryTime(batchOutbox.getNextRetryTime());
        }
        batchOutboxDAO.update(batchOutbox);
		
	}
}