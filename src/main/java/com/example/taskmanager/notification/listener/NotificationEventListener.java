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

import com.example.taskmanager.entity.dto.NotificationRequest;
import com.example.taskmanager.entity.enums.NotificationEnum;
import com.example.taskmanager.entity.enums.NotificationEventType;
import com.example.taskmanager.notification.NotificationStrategyFactory;
import com.example.taskmanager.notification.event.NotificationEvent;
import com.example.taskmanager.notification.resolver.TaskCreatedNotificationResolver;
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
	 */
	@Async
	@EventListener
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
