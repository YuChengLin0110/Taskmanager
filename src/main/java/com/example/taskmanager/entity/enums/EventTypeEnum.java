package com.example.taskmanager.entity.enums;

public enum EventTypeEnum {
	/**
	 * 任務被創建
	 */
	TASK_CREATED,

	/**
	 * 任務被指派
	 */
	TASK_ASSIGNED,

	/**
	 * 任務開始
	 */
	TASK_STARTED,

	/**
	 * 任務完成
	 */
	TASK_DONE,

	/**
	 * 任務逾期
	 */
	TASK_OVERDUE,

	/**
	 * 任務關閉
	 */
	TASK_CLOSED
}
