package com.example.taskmanager.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 本類別使用 JPA 註解（@Entity、@Table、@Id、@GeneratedValue）僅作為 自動建表 用途
 * 搭配 Spring Boot 設定 spring.jpa.hibernate.ddl-auto=update/create，自動建立資料表
 * 
 * 
 * @Id 表示該欄位為主鍵（PRIMARY KEY）
 * @GeneratedValue(strategy = GenerationType.IDENTITY) 表示該主鍵使用資料庫自增
 * 雖然是 JPA 註解，但這裡只用來幫 Hibernate 建立欄位為 auto_increment
 * 
 * MyBatis 操作說明：
 *  本專案資料操作皆透過 MyBatis（非 JPA Repository）
 *  如果想在插入資料後自動取得 id （回填至 Java 物件），需在 MyBatis 的 <insert> 中加上：
 *      useGeneratedKeys="true" keyProperty="id"
 *  這樣 insert 執行完後， Task 物件的 id 會自動填入資料庫產生的自 id ，方便後續使用
 * 
 * JPA 註解：僅限用於建表
 * MyBatis 操作：負責所有資料 CRUD，回填 id 需設定 useGeneratedKeys
 */

@Entity
@Table(name = "task")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;

	private String status;

	private LocalDateTime createdTime;
	private LocalDateTime updatedTime;
	
	private Long userId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Override
	public String toString() {
		return "Task{" +
	            "id=" + id +
	            ", title='" + title + 
	            ", description='" + description + 
	            ", status='" + status + 
	            ", createdTime=" + createdTime +
	            ", updatedTime=" + updatedTime +
	            ", userId=" + userId +
	            '}';
	}
}
