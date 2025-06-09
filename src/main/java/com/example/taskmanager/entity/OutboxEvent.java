package com.example.taskmanager.entity;

import java.time.LocalDateTime;

import com.example.taskmanager.entity.enums.EventStatusEnum;
import com.example.taskmanager.entity.enums.EventTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String eventType;

	private String payload;

	private String status;

	private LocalDateTime createTime;

	private LocalDateTime sentTime;

	private Integer retryCount;

	private String lastError;

	private Long entityId;

	private LocalDateTime nextRetryTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	// 將 String 轉為 EventTypeEnum
	public EventTypeEnum getEventType() {
		return EventTypeEnum.valueOf(this.eventType);
	}
	
	// 將 EventTypeEnum 轉為 String
	public void setEventType(EventTypeEnum eventType) {
		this.eventType = eventType.name();
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public EventStatusEnum getStatus() {
		return EventStatusEnum.valueOf(this.status);
	}

	public void setStatus(EventStatusEnum status) {
		this.status = status.name();
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getSentTime() {
		return sentTime;
	}

	public void setSentTime(LocalDateTime sentTime) {
		this.sentTime = sentTime;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public LocalDateTime getNextRetryTime() {
		return nextRetryTime;
	}

	public void setNextRetryTime(LocalDateTime nextRetryTime) {
		this.nextRetryTime = nextRetryTime;
	}
	
	@Override
	public String toString() {
	    return "ClassName{" +
	            "id=" + id +
	            ", eventType='" + eventType +
	            ", payload='" + payload +
	            ", status='" + status +
	            ", createTime=" + createTime +
	            ", sentTime=" + sentTime +
	            ", retryCount=" + retryCount +
	            ", lastError='" + lastError +
	            ", entityId=" + entityId +
	            ", nextRetryTime=" + nextRetryTime +
	            '}';
	}
}