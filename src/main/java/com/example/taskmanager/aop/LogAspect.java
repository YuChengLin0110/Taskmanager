package com.example.taskmanager.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// SpringApplication 要使用 @EnableAspectJAutoProxy
@Aspect
@Component
public class LogAspect {
	
	private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
	
	/* execution攔截方法執行
	 * 以下說明 AspectJ
	 * 1. * ：表示回傳型別不限，任何回傳值都符合條件
	 * 2. com.example.taskmanager.service ：指定這個包名
	 * 3. .. ：表示 service 這個包和它底下所有子包
	 * 4. * ：方法名稱，這邊 * 表示方法名稱不限，任何方法都符合
	 * 5. (..) ：表示方法參數不限，任何參數組合都符合
	 * */
	@Pointcut("execution(* com.example.taskmanager.service..*(..))")
	public void serviceMethods() {}
	
	
	@Around("serviceMethods()")
	public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String args = Arrays.toString(joinPoint.getArgs());
		
		long start = System.currentTimeMillis();
		log.info("[START] {}.{} args = {}", className, methodName, args);
		
		try {
			Object result = joinPoint.proceed();
			long end = System.currentTimeMillis();
			
			log.info("[END] {}.{} Result = {} ({} ms)", className, methodName, result, end - start);
			
			return result; // 攔截後，把結果交回去，讓程式流程繼續
			
		//Throwable 是所有錯誤和例外的頂層
		} catch (Throwable e) {
			long end = System.currentTimeMillis();
			log.error("[ERROR] {}.{} Error = {} ({} ms)", className, methodName, e.getMessage(), e);
			
			throw e; // 一定要往外拋，否則原本的錯誤就被吃掉了
		}
	}
}
