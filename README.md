# TaskManager

TaskManager 是一個簡潔的任務管理系統

透過 Spring Boot、Spring Cloud Config、RabbitMQ、Redis 等技術

實現分布式環境下的任務追蹤、狀態管理與非同步處理，並採用 Docker 容器化部署

## 功能特色
使用者註冊與登入 JWT 驗證

AOP 切面日誌紀錄，使用 Spring AOP 攔截關鍵方法，紀錄方法參數、回傳結果及執行耗時，方便追蹤與除錯

自訂 Annotation 註解觸發操作日誌，透過 AOP 監聽並攔截帶有該註解的方法，自動記錄操作行為

MySQL 主從架構，透過 AOP 判斷，讀取導向從庫 Slave ， 寫入導向主庫 Master

Outbox Pattern 確保資料與訊息一致性

使用 Redisson 管理分布式鎖，確保多個實例只會有一個 Scheduler 執行任務

使用 RabbitMQ 傳遞 任務

任務消費者 Consumer 異步處理業務邏輯

具備重試與錯誤記錄機制

利用 Spring Cloud Config Server 從 Git 倉庫統一管理多環境設定，實現配置集中與動態更新

使用 Docker 與 docker-compose 管理多容器服務，方便在開發與生產環境快速部署

任務 CRUD

任務狀態管理 NEW → PROCESSINGS → DONE

## 技術
Spring Boot：作為主要框架來構建 RESTful API

Spring Cloud Config：配置管理，支持多環境動態配置更新

Spring Security + JWT ：身份驗證與授權

Spring AOP：攔截切面，監聽特定方法紀錄Log，自訂 @Annotation 註解，搭配 AOP 自動攔截帶有該註解的方法

MySQL 主從架構：讀寫分離，利用 AOP 自動切換資料庫來源

Redisson：管理分布式鎖

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

## 分布式鎖 Redis + Redisson
確保在多實例運行的情況下，Scheduler 只會有一個實例執行，專案使用 Redisson 提供的分布式鎖功能

使用 RedisDistributedLockExecutor 統一管理鎖的獲取與釋放，支援等待超時與鎖定時長設定

執行方法包裹在鎖範圍內，確保任務不會同時被多個實例執行

若無法獲取鎖，拋出異常避免任務重複執行

## MySQL 主從架構說明

用來提高資料庫讀取效能與系統穩定性

使用 Spring AOP 攔截帶有 @Transactional 的方法

根據 readOnly = true 屬性，自動判斷該次操作是純讀取還是寫入

讀取操作會導向從庫 Slave ，減輕主庫壓力

寫入操作會導向主庫 Master ，確保資料一致性

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

請在專案根目錄下建立 .env 檔案， 可參考 .env.example

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
