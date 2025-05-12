package com.example.taskmanager.entity.enums;

public enum EventStatusEnum {
	/**
	 * 傳送中
	 */
	PENDING,
	
	/**
	 * 已送出
	 */
	SENT,
	
	/**
	 * 送出失敗 
	 */
	FAILED,
	
	/**
	 * 重試失敗太多次
	 * 標記為死訊息 
	 */
	DEAD;
}
