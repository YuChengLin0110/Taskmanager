package com.example.taskmanager.entity;

import java.time.LocalDateTime;

import com.example.taskmanager.entity.enums.EventStatusEnum;
import com.example.taskmanager.entity.enums.EventTypeEnum;

public interface IOutbox {
	Long getId();
	EventTypeEnum getEventType();
    String getPayload();
    EventStatusEnum getStatus();
    LocalDateTime getSentTime();
    Integer getRetryCount();
    LocalDateTime getNextRetryTime();
    
    void setStatus(EventStatusEnum status);
    void setSentTime(LocalDateTime time);
    void setRetryCount(Integer count);
    void setNextRetryTime(LocalDateTime time);
}
