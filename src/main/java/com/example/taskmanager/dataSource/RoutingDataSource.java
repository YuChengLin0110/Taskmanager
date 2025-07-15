package com.example.taskmanager.dataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

// 自訂的動態資料源類別 繼承 Spring 提供的 AbstractRoutingDataSource
// 根據當前執行緒設定的資料源的類型，決定要用哪一個 DataSource 
public class RoutingDataSource  extends AbstractRoutingDataSource{
	
	private static final Logger log = LoggerFactory.getLogger(RoutingDataSource.class);
	
	/**
     * 每次 Spring 要執行資料庫操作時
     * 都會呼叫這個方法決定目前要用哪個資料源的 key
     * 這個 key 對應到 DataSourceConfig routingDataSource 設定的名稱
     */
    @Override
    protected Object determineCurrentLookupKey() {
    	// 從自訂的 DataSourceContextHolder 取得目前執行緒設定的資料源類型
        Object dataSourceType = DataSourceContextHolder.get();
        
        log.info("RoutingDataSource 決定使用資料源: " + dataSourceType + ", thread=" + Thread.currentThread().threadId());
    	
        // 回傳這個資料源 key 給 AbstractRoutingDataSource 讓它拿對應的 DataSource
        return dataSourceType;
    }
}