package com.example.taskmanager.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@MapperScan("com.example.taskmanager.dao") // 讓 MyBatis 去掃描這個 package 下的所有 @Mapper 接口
public class MyBatisConfig {
	
	@Value("${mybatis.mapper-locations}")
	private String XML_LOCATION;
	
	/* SqlSessionFactory 資料庫連線的工廠
	 * SqlSession 資料庫連線通道，透過它執行 SQL
	 * 指定了 自訂的 routingDataSource
	 * */
	@Bean
	public SqlSessionFactory sqlSessionFactory(@Qualifier("routingDataSource") DataSource dataSource) throws Exception {
		
		// 使用 MyBatis 提供的工廠類別來建立 SqlSessionFactory
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		
		// 設定資料來源，這裡使用的是自訂的 routingDataSource
		factory.setDataSource(dataSource);
		
		// 指定 XML 檔的位置
		// PathMatchingResourcePatternResolver().getResources() 把 String 轉成 Resource[] 給 setMapperLocations 使用
		factory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(XML_LOCATION));
		
		// 回傳 SqlSessionFactory 物件 給 Spring 管理
		return factory.getObject();
	}
}