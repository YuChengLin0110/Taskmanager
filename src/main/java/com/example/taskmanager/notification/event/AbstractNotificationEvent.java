package com.example.taskmanager.notification.event;

// 抽象類別，泛型 T 表示會攜帶哪種類型的資料
// 提供了一個通用的 source 欄位，表示事件的來源資料
public abstract class AbstractNotificationEvent<T> implements NotificationEvent<T> {
	
	// source 代表這個事件的來源物件，類型由泛型 T 決定
	private final T source;
	
	// 建構子 ： 創建事件物件時，必須傳入來源資料 source
	public AbstractNotificationEvent(T source) {
		this.source = source;
	}
	
	// 回傳事件的來源資料
	@Override
	public T getSource() {
		return source;
	}
}
