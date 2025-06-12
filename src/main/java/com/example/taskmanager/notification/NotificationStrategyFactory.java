package com.example.taskmanager.notification;

import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.taskmanager.entity.enums.NotificationEnum;
import com.example.taskmanager.notification.strategy.NotificationStrategy;

@Component
public class NotificationStrategyFactory {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationStrategyFactory.class);
	
	// 用來存放每個策略實作，下面會轉成 EnumMap 效率更高且類型安全
	private final Map<NotificationEnum, NotificationStrategy> strategyMap;
	
	/**
	 * Spring 會自動注入所有實作 NotificationStrategy 的 Bean ， 以 Bean 名稱為 Key ， 實體為 Value
	 * 將這些 Bean 轉換成以 NotificationEnum 為 Key ， NotificationStrategy 為 Value 的 EnumMap
	 */
	@Autowired
	public NotificationStrategyFactory(Map<String, NotificationStrategy> springBeans) {
		// 建立一個以 NotificationEnum 為鍵的 EnumMap
		this.strategyMap = new EnumMap<>(NotificationEnum.class);
		
		// 根據策略所對應的 Channel 放入 EnumMap
		for(NotificationStrategy strategy : springBeans.values()) {
			strategyMap.put(strategy.getChannel(), strategy);
		}
	}
	
	// 取得對應的 NotificationStrategy 實作
	public NotificationStrategy getStrategy(NotificationEnum channel) {
		NotificationStrategy strategy = strategyMap.get(channel);
		
		if(strategy == null) {
			log.error("找不到對應的通知通道: {}", channel);
			throw new IllegalArgumentException("找不到對應的通知通道: " + channel);
		}
		
		return strategy;
	}
}
