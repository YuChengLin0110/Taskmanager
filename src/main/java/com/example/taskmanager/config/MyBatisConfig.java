package com.example.taskmanager.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.taskmanager.dao") // 讓 MyBatis 去掃描這個 package 下的所有 @Mapper 接口
public class MyBatisConfig {

}
