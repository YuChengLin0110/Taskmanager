# TaskManager  此專案仍在開發中 Still under development.

TaskManager 是一個簡潔的任務管理系統，使用者可以透過帳號註冊與登入，來創建、追蹤個人任務的處理狀態

後端採用 Spring Boot 架構，整合 JWT 驗證、RabbitMQ、Redis 等技術

## 功能特色
使用者註冊與登入 JWT 驗證

任務 CRUD

任務狀態管理 NEW → PROCESSINGS → DONE

使用 RabbitMQ 傳遞 任務

Outbox Pattern 確保資料與訊息一致性

任務消費者 Consumer 異步處理業務邏輯

具備重試與錯誤記錄機制

使用 Redis 實作分布式鎖，確保只有一個 Scheduler 執行任務，並結合 Watchdog 自動續期機制來維持鎖


## 技術
Spring Boot：作為主要框架來構建 RESTful API

Spring Security + JWT ：身份驗證與授權

H2 Database：內嵌型資料庫，適合開發階段使用

MyBatis：資料庫操作框架，使用 XML 撰寫 SQL

Swagger/OpenAPI：生成 API 文檔，方便開發者了解和測試 API

BCrypt：使用者密碼加密

Redis：快取與暫存資料

RabbitMQ：任務的非同步訊息處理

SLF4J + Logback：系統日誌與錯誤追蹤

Docker：建立可重現的開發環境與多服務容器管理

## Docker 支援
本專案整合 Docker，快速啟動完整開發環境

## Outbox Pattern 實作
為確保資料一致性與傳遞，實作了 Outbox Pattern：

當任務被創建時，會一併寫入 OutboxEvent 表

Scheduler 會定期撈取 PENDING 狀態事件並送出至 MQ

成功送出則標記為 SENT，失敗則進行重試或標記為 DEAD

消費者 Consumer 接收 MQ 訊息後執行對應業務邏輯

## 分布式鎖與 Watchdog 機制
為確保 OutboxEventScheduler 只會在單一實例上執行，並避免過期鎖造成重複執行，使用了 Redis 分布式鎖並結合 Watchdog 續期機制：

Redis 鎖：確保 Scheduler 只有成功獲得鎖的實例可以執行任務

Watchdog：在任務執行過程中持續續期鎖，防止鎖過期，確保任務執行不會中斷


