# TaskManager  此專案仍在開發中 Still under development.
TaskManager 是一個任務管理系統，讓用戶可以註冊、登入並管理個人任務

它支援創建、查看、更新、刪除任務，並使用 RESTful API 讓用戶可以輕鬆操作

此系統的後端使用 Spring Boot，並結合 JWT 用戶身份驗證機制來保護 API

## 技術
Spring Boot：作為主要框架來構建 RESTful API

Spring Security：用戶身份驗證與授權，使用 JWT 進行 Token 驗證

H2 Database：內嵌型資料庫，適合開發階段使用

MyBatis：資料庫操作框架，使用 XML 文件進行 SQL 查詢

Swagger/OpenAPI：生成 API 文檔，方便開發者了解和測試 API

BCrypt：密碼加密，保證用戶密碼的安全

Redis：緩存方案，減少資料庫查詢

RabbitMQ：消息隊列，用於異步處理、消息傳遞

SLF4J + Logback：用於紀錄系統流程、除錯與問題追蹤
