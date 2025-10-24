package com.example.taskmanager.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.taskmanager.entity.BatchOutbox;

@Mapper
public interface BatchOutboxDAO {
	
	int insert(BatchOutbox batchOutbox);
	
	List<BatchOutbox> findPending(int limit);
	
	int update(BatchOutbox batchOutbox);
}
