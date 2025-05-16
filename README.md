# TaskManager  此專案仍在開發中 Still under development.

TaskManager 是一個簡潔的任務管理系統

透過 Spring Boot、Spring Cloud Config、RabbitMQ、Redis 等技術

實現分布式環境下的任務追蹤、狀態管理與非同步處理，並採用 Docker 容器化部署

## 功能特色
使用者註冊與登入 JWT 驗證

任務 CRUD

任務狀態管理 NEW → PROCESSINGS → DONE

使用 RabbitMQ 傳遞 任務

Outbox Pattern 確保資料與訊息一致性

任務消費者 Consumer 異步處理業務邏輯

具備重試與錯誤記錄機制

使用 Redis 實作分布式鎖，確保只有一個 Scheduler 執行任務，並結合 Watchdog 自動續期機制來維持鎖

利用 Spring Cloud Config Server 從 Git 倉庫統一管理多環境設定，實現配置集中與動態更新

使用 Docker 與 docker-compose 管理多容器服務，方便在開發與生產環境快速部署

## 技術
Spring Boot：作為主要框架來構建 RESTful API

Spring Cloud Config：配置管理，支持多環境動態配置更新

Spring Security + JWT ：身份驗證與授權

H2 Database：內嵌型資料庫，適合開發階段使用

MyBatis：資料庫操作框架，使用 XML 撰寫 SQL

Swagger/OpenAPI：生成 API 文檔，方便開發者了解和測試 API

BCrypt：使用者密碼加密

Redis：快取與暫存資料

RabbitMQ：任務的非同步訊息處理

SLF4J + Logback：系統日誌與錯誤追蹤

Docker & docker-compose：容器化部署管理

## Docker 支援
本專案整合 Docker，快速啟動完整開發環境

## Outbox Pattern 實作
為確保資料一致性與傳遞

當任務被創建時，會一併寫入 OutboxEvent 表

Scheduler 會定期撈取 PENDING 狀態事件並送出至 MQ

成功送出則標記為 SENT，失敗則進行重試或標記為 DEAD

消費者 Consumer 接收 MQ 訊息後執行對應業務邏輯

## 分布式鎖與 Watchdog 機制
為確保 OutboxEventScheduler 只會在單一實例上執行，並避免過期鎖造成重複執行，使用了 Redis 分布式鎖並結合 Watchdog 續期機制：

Redis 鎖：確保 Scheduler 只有成功獲得鎖的實例可以執行任務

Watchdog：在任務執行過程中持續續期鎖，防止鎖過期，確保任務執行不會中斷

## 專案啟動說明（How to Run）
本專案由以下三個部分組成：

[TaskManager](https://github.com/YuChengLin0110/Taskmanager)：主應用程式Spring Boot

[ConfigServer](https://github.com/YuChengLin0110/TaskmanagerConfigServer)：Spring Cloud Config Server，用來集中管理設定

[TaskManagerConfigRepo](https://github.com/YuChengLin0110/TaskManagerConfigRepo)：儲存所有環境設定檔的 Git 倉庫

使用 docker-compose 統一啟動與管理

### 專案目錄結構
你的資料夾/

├── docker-compose.yml

├── .env                    # 機密資料環境變數

├── taskmanager/            # 主應用程式 

│   └── Dockerfile

├── config-server/          # Config Server 

│   └── Dockerfile

請在專案根目錄下建立 .env 檔案， 可參考 env.example

### 建置 jar 檔案
請先在本地建置好 taskmanager 與 config-server 的 jar 檔案

clean package -DskipTests

### 啟動所有服務
回到最外層 docker-compose.yml 所在的位置 執行以下指令：

docker-compose up --build

此指令會啟動以下容器：

Config Server（port: 8888）

TaskManager 主應用（port: 8080）

Redis

RabbitMQ

Prometheus

Grafana
