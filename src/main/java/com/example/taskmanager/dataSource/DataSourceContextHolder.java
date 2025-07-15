package com.example.taskmanager.dataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.taskmanager.entity.enums.DataSourceType;

/*
 * 用來儲存目前執行緒要用哪一個資料庫，Master or Slave
 * 這邊要拿來給 RoutingDataSource get 資料源
 * DataSourceAspect 去 set 資料源
 * */
public class DataSourceContextHolder {
	
	private static final Logger log = LoggerFactory.getLogger(DataSourceContextHolder.class);
	
	// ThreadLocal 是 每條執行緒 自己的 儲物櫃 彼此不共享
	// 這裡就是每條 thread 都可以各自記住 我要用主庫還是從庫
	private static final ThreadLocal<DataSourceType> contextHolder = new ThreadLocal<>();
	
	// 設定目前執行緒要使用的資料來源
	public static void set(DataSourceType type) {
		log.info("設定資料源: " + type + ", thread=" + Thread.currentThread().getId());
		
		// 實際把資料源類型放進 threadLocal
		contextHolder.set(type);;
	}
	
	// 取得目前執行緒設定的資料來源類型
	public static DataSourceType get() {
		DataSourceType value = contextHolder.get();
		log.info("取得資料源: " + value + ", thread=" + Thread.currentThread().getId());
		
		return value;
	}
	
	// 清除目前執行緒設定的資料源 避免 thread 循環使用時殘留
	public static void clear() {
		log.info("清除資料源, thread=" + Thread.currentThread().getId());
		
		contextHolder.remove();
	}
}