package com.example.taskmanager.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dao.OutboxEventDAO;
import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.dto.OutboxEventMarkAsFailedDTO;
import com.example.taskmanager.entity.enums.EventStatusEnum;
import com.example.taskmanager.service.OutboxEventService;

@Service
public class OutboxEventServiceImpl implements OutboxEventService{
	
	private final OutboxEventDAO outboxEventDAO;
	
	@Autowired
	public OutboxEventServiceImpl(OutboxEventDAO outboxEventDAO) {
		this.outboxEventDAO = outboxEventDAO;
	}
	
	@Override
	public void createEvent(OutboxEvent event) {
		event.setCreateTime(LocalDateTime.now());
		event.setStatus(EventStatusEnum.PENDING);
		outboxEventDAO.insertEvent(event);
	}

	@Override
	public List<OutboxEvent> findPendingEvents(int limit) {
		return outboxEventDAO.findPendingEvents(limit);
	}

	@Override
	public void markAsSent(Long id) {
		outboxEventDAO.markAsSent(id, LocalDateTime.now());
		
	}

	@Override
	public void markAsFailed(OutboxEventMarkAsFailedDTO dto) {
		outboxEventDAO.markAsFailed(dto);
	}
	
	@Override
	public void markAsDead(OutboxEvent enent) {
		outboxEventDAO.markAsDead(enent);
	}
	
}
