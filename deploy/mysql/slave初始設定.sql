--1. 
--GTID 必須雙方都開啟（master 與 slave）
--參考 master.cnf  slave.cnf

--2.
--在 master 上要建立複製用的帳號，並給權限
CREATE USER 'account'@'%' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'account'@'%';
FLUSH PRIVILEGES;

--3.
--在 Slave 執行初始化設定

-- 確認 GTID 有開啟
-- GTID 是用來讓主從同步能自動追蹤位置，設定 SOURCE_AUTO_POSITION = 1 時必須開啟
SHOW VARIABLES LIKE 'gtid_mode';

-- 第一步：先停止 slave 的複製行為 確保從伺服器目前沒有在同步中
STOP REPLICA;

-- 第二步：指定 Master 的連線資訊
-- 說明：
-- - SOURCE_HOST：主伺服器的主機名或 IP 通常在 Docker Compose 中會是 service name
-- - SOURCE_PORT：主伺服器的 MySQL port，預設 3306
-- - SOURCE_USER / SOURCE_PASSWORD：授權的同步帳號需在 master 上設定並授權給 slave 使用
-- - SOURCE_AUTO_POSITION = 1：啟用 GTID 模式 自動對齊位置，不需手動指定 binlog name / position
CHANGE REPLICATION SOURCE TO
  SOURCE_HOST='mysql-master',
  SOURCE_PORT=3306,
  SOURCE_USER='account',
  SOURCE_PASSWORD='password',
  SOURCE_AUTO_POSITION = 1;

-- 第三步：開始同步資料
START REPLICA;

-- 第四步：檢查複製狀態是否正常
-- - 看 Replica_IO_Running 和 Replica_SQL_Running 都要是 Yes 才代表正常
-- - 若有錯誤，Last_IO_Error 或 Last_SQL_Error 會顯示詳細錯誤訊息
SHOW REPLICA STATUS;