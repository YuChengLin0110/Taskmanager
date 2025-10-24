package com.example.taskmanager.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskmanager.dao.OutboxEventDAO;
import com.example.taskmanager.entity.OutboxEvent;
import com.example.taskmanager.entity.dto.OutboxEventMarkAsFailedDTO;
import com.example.taskmanager.entity.enums.EventStatusEnum;
import com.example.taskmanager.service.OutboxEventService;

@Service
public class OutboxEventServiceImpl implements OutboxEventService{
	
	private static final Logger log = LoggerFactory.getLogger(OutboxEventServiceImpl.class);
	private final OutboxEventDAO outboxEventDAO;
	private final SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	public OutboxEventServiceImpl(OutboxEventDAO outboxEventDAO, SqlSessionFactory sqlSessionFactory) {
		this.outboxEventDAO = outboxEventDAO;
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	@Override
	public void createEvent(OutboxEvent event) {
		event.setCreateTime(LocalDateTime.now());
		event.setStatus(EventStatusEnum.PENDING);
		outboxEventDAO.insertEvent(event);
	}

	@Override
	@Transactional(readOnly = true)
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
	
	/**
	 * 批次插入 OutboxEvent
	 * 數量 < 100 直接使用 MyBatis 的 forEach
	 * 數量 >= 100 使用 MyBatis 批次 Session 插入，每 100 筆 flush 避免記憶體占用過高
	 */
	@Override
	public void batchInsertForeach(List<OutboxEvent> events) {
		if(events == null || events.isEmpty()) {
			return;
		}
		
		int size = events.size();
		
		if(size < 100) {
			outboxEventDAO.batchInsertForeach(events);
		}else {
			try(SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
				OutboxEventDAO mapper = session.getMapper(OutboxEventDAO.class);
				int count = 0;
				for(OutboxEvent event : events) {
					mapper.insertEvent(event);
					count++;
					
					// 每 100 筆 flushStatements，避免記憶體累積太多
					if(count % 100 == 0) {
						session.flushStatements();
					}
				}
				
				// 手動控制 sqlSession 需要 手動 commit
				session.commit();
				
				log.info("Batch insert {} events successfully", size);
			}catch (Exception e) {
				log.error("Batch Outbox insert failed", e);
                throw new RuntimeException("Batch Outbox insert failed", e);
			}
		}
	}
}
