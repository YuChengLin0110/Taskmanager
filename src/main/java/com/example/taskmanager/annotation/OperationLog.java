package com.example.taskmanager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* 搭配 AOP 使用
 * 使用方式 : 
 * @OperationLog("動作")
 * */
// 表示這個註解只能套用在 Method
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
	String value();
}
