# TaskManager

TaskManager 是一個基於 Spring Boot 的分布式任務管理系統，結合 Spring Cloud Config、RabbitMQ、Redis 等技術，實現任務追蹤、狀態管理與非同步處理，並透過 Docker 容器化

## 核心功能
- **使用者認證與授權**  
  採用 Spring Security 結合 JWT，實現安全的使用者註冊與登入流程。

- **操作日誌與 AOP**  
  透過 Spring AOP 攔截關鍵方法，自動記錄方法參數、回傳結果及執行時間。  
  自訂 Annotation 搭配 AOP 自動觸發操作日誌，方便追蹤與除錯。

- **資料庫讀寫分離與主從架構**  
  使用 AOP 攔截 @Transactional 方法，自動根據 readOnly 判斷導向從庫或主庫

- **分布式鎖管理**  
  採用 Redisson 實現分布式鎖，確保多實例運行時 Scheduler 僅有一個實例執行任務，避免重複觸發。

- **Outbox Pattern 保證資料與訊息一致性**  
 確保資料與訊息一致性。任務事件寫入 Outbox 表，由 Scheduler 定期發送至 MQ

- **任務非同步處理**  
  使用 RabbitMQ 傳遞任務，由 Consumer 異步處理業務

- **通知系統策略模式設計**  
  支援多種通知通道 Email、Slack ，由 NotificationStrategyFactory 動態取得對應策略物件，擴充性強。  

- **多環境配置管理**  
  透過 Spring Cloud Config Server 從 Git 倉庫集中管理設定，實現配置動態更新。

- **容器化部署**  
  使用 Docker 與 docker-compose 管理多容器服務，快速構建開發與生產環境。

## 設計模式

強調可維護性與擴充性，持續導入多種設計模式：

- 策略模式 Strategy Pattern  
  將通知通道 Email、Slack 等，封裝為獨立策略，讓通知邏輯根據參數動態切換

- 工廠模式 Factory Pattern  
  建立策略工廠，集中管理並回傳不同策略實作


## 使用技術
- Spring Boot
- Spring Security + JWT
- Spring AOP 方法攔截與日誌
- MySQL 主從架構（讀寫分離）  
- MyBatis
- Redis
- Redisson 分布式鎖
- RabbitMQ 非同步訊息 
- Spring Cloud Config 配置集中管理  
- Swagger/OpenAPI API 文件生成  
- BCrypt 密碼加密  
- SLF4J + Logback 日誌  
- Docker + docker-compose 容器化部署
- 策略模式 通知系統擴充

## 專案包含三個主要部分：

[TaskManager](https://github.com/YuChengLin0110/Taskmanager)：主應用程式 Spring Boot

[ConfigServer](https://github.com/YuChengLin0110/TaskmanagerConfigServer)：Spring Cloud Config Server 集中管理多環境設定 

[TaskManagerConfigRepo](https://github.com/YuChengLin0110/TaskManagerConfigRepo)：儲存所有環境設定檔的 Git 倉庫

### 專案目錄結構
你的資料夾/

├── docker-compose.yml

├── .env                    # 機密資料環境變數

├── taskmanager/            # 主應用程式 

│   └── Dockerfile

├── config-server/          # Config Server 

│   └── Dockerfile

請在專案根目錄下建立 .env 檔案， 可參考 .env.example

### 建置流程：  
請先在本地建置好 taskmanager 與 config-server
1. 本地打包 jar，執行 `mvn clean package -DskipTests` 

2. 使用 docker-compose 在專案根目錄啟動 `docker-compose up --build`

啟動後包含以下容器：  
- Config Server  
- TaskManager 主應用
- Redis  
- RabbitMQ
- MySQL Master & Slave
- Prometheus  
- Grafana
