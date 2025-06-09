package com.example.taskmanager.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.example.taskmanager.dataSource.RoutingDataSource;
import com.example.taskmanager.entity.enums.DataSourceType;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	
	@Bean
	@ConfigurationProperties("spring.datasource.master") // 讀取 prefix 為 spring.datasource.master 的設定
	public DataSourceProperties masterProperties() {
		return new DataSourceProperties();
	}
	
	// 根據 masterProperties 建立主資料庫的 DataSource ，這裡用 HikariCP 連線池
	@Bean
	@ConfigurationProperties("spring.datasource.master.hikari")
	public DataSource masterDataSource(@Qualifier("masterProperties") DataSourceProperties masterProp) {
		
		// 初始化 DataSource 並指定使用 HikariDataSource
		return masterProp.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	@Bean
	@ConfigurationProperties("spring.datasource.slave")
	public DataSourceProperties slaveProperties() {
		return new DataSourceProperties();
	}
	
	@Bean
	@ConfigurationProperties("spring.datasource.slave.hikari")
	public DataSource slaveDataSource(@Qualifier("slaveProperties") DataSourceProperties slaveProp) {
		return slaveProp.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}
	
	// 主要的 DataSource Bean，使用自訂的 RoutingDataSource 實作動態資料源切換
    // @Primary 表示預設注入
	@Primary
	@Bean
	public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource master, @Qualifier("slaveDataSource") DataSource slave) {
		RoutingDataSource routingDataSource = new RoutingDataSource();
		
		// 將主、從資料庫 DataSource 放進 Map
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put(DataSourceType.MASTER, master);
		targetDataSources.put(DataSourceType.SLAVE, slave);
		
		// 設定預設資料源
		routingDataSource.setDefaultTargetDataSource(master);
		
		// 設定所有可用的目標資料源
		routingDataSource.setTargetDataSources(targetDataSources);
		
		// 設定當資料源 lookup key 不存在時，是否允許使用預設資料源
		routingDataSource.setLenientFallback(false);
		
		return routingDataSource;
	}
	
	// 事務管理器，使用上面的 routingDataSource 來管理
	@Bean
	public DataSourceTransactionManager transactionManager(DataSource routingDataSource) {
		return new DataSourceTransactionManager(routingDataSource);
	}
}