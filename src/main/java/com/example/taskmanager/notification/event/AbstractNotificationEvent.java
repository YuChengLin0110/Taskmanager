package com.example.taskmanager.notification.event;

// 抽象類別，泛型 T 表示會攜帶哪種類型的資料
// 提供了一個通用的 source 欄位
public abstract class AbstractNotificationEvent<T> implements NotificationEvent {
	
	// source 代表這個事件的來源物件，類型由泛型 T 決定
	private final T source;
	
	// 建構子 ： 建立事件時就要給它一個資料來源
	public AbstractNotificationEvent(T source) {
		this.source = source;
	}
	
	@Override
	public T getSource() {
		return source;
	}
}
