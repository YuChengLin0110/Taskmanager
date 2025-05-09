package com.example.taskmanager.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.dto.OutboxEventMarkAsFailedDTO;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface OutboxEventDAO {
	
	void insertEvent(OutboxEvent enent);
	
	List<OutboxEvent> findPendingEvents(int limit);
	
	void markAsSent(@Param(value = "id") Long id, @Param(value = "sentTime") LocalDateTime sentTime);
	
	void markAsFailed(OutboxEventMarkAsFailedDTO dto);
	
	void markAsDead(OutboxEvent enent);
}
