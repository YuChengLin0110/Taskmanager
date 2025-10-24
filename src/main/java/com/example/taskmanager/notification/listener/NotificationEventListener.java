package com.example.taskmanager.notification.listener;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEnum;
import com.example.taskmanager.entity.enums.NotificationEventType;
import com.example.taskmanager.notification.NotificationStrategyFactory;
import com.example.taskmanager.notification.strategy.NotificationStrategy;

@Component
@ConditionalOnProperty(name = "notification.mode", havingValue = "default", matchIfMissing = true)
public class NotificationEventListener {

	private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

	private final NotificationStrategyFactory strategyFactory;

	@Autowired
	public NotificationEventListener(NotificationStrategyFactory strategyFactory) {
		this.strategyFactory = strategyFactory;
	}
	
	/*
	 * 監聽任務建立事件
	 * 這個方法會自動監聽由 ApplicationEventPublisher 發布的 TaskCreatedEvent
	 * @Async 這個方法會在另一個執行緒裡跑，發事件的那條執行緒不會被阻塞
	 * 如果通知系統這邊有錯誤，也不會影響到 service 的 @Transactional
	 */
	@Async
//	@EventListener 改用 @TransactionalEventListener 搭配 phase = TransactionPhase.AFTER_COMMIT
//	在 Service @Transactional 只有當 Service Transactional 提交成功後才執行，避免事務回滾後仍發送通知
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void notification(NotificationRequest request) {
		
		// 取得這次要通知的通道
		List<NotificationEnum> channels = getChannels(request.getEventType());
		
		// 依序取得策略發送通知
		for (NotificationEnum channel : channels) {
            NotificationStrategy strategy = strategyFactory.getStrategy(channel);
            strategy.send(request);
            
            log.info("EventListener 發送通知");
        }
	}
	
	private List<NotificationEnum> getChannels(NotificationEventType type) {
		switch(type) {
		case TASK_CREATED : return List.of(NotificationEnum.SLACK, NotificationEnum.EMAIL);
		default : return new ArrayList<>();
		}
	}
}
