package com.example.taskmanager.service;

import java.util.List;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.dto.OutboxEventMarkAsFailedDTO;

public interface OutboxEventService {

	void createEvent(OutboxEvent event);

	List<OutboxEvent> findPendingEvents(int limit);

	void markAsSent(Long id);

	void markAsFailed(OutboxEventMarkAsFailedDTO dto);
}
