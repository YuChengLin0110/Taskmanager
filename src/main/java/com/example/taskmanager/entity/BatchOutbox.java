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
@Table(name = "batch_outbox")
public class BatchOutbox implements IOutbox{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String eventType;
    private String payload;
    private Integer payloadSize;
    private String status;
    private Long createdBy;
    private LocalDateTime createdTime;
    private LocalDateTime sentTime;
    private Integer retryCount;
    private LocalDateTime nextRetryTime;
    private String firstTitle;
	
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public EventTypeEnum getEventType() {
		return EventTypeEnum.valueOf(this.eventType);
	}
	public void setEventType(EventTypeEnum eventType) {
		this.eventType = eventType.name();
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public Integer getPayloadSize() {
		return payloadSize;
	}
	public void setPayloadSize(Integer payloadSize) {
		this.payloadSize = payloadSize;
	}
	public EventStatusEnum getStatus() {
		return EventStatusEnum.valueOf(this.status);
	}
	public void setStatus(EventStatusEnum status) {
		this.status = status.name();
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public LocalDateTime getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
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
	public LocalDateTime getNextRetryTime() {
		return nextRetryTime;
	}
	public void setNextRetryTime(LocalDateTime nextRetryTime) {
		this.nextRetryTime = nextRetryTime;
	}
	public String getFirstTitle() {
		return firstTitle;
	}
	public void setFirstTitle(String firstTitle) {
		this.firstTitle = firstTitle;
	}
}
