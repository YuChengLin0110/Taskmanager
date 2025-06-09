package com.example.taskmanager.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.taskmanager.dataSource.DataSourceContextHolder;
import com.example.taskmanager.entity.enums.DataSourceType;

@Aspect
@Order(0) // 切面執行優先順序，越小越早執行
@Component
public class DataSourceAspect {
	
	private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);
	
	// 攔截所有標註了 @Transactional 的方法
	@Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
	public void transactionalMethod() {}
	
	// 根據 readOnly 屬性決定資料源是主庫或從庫
	@Around("transactionalMethod() && @annotation(tx)")
	public Object beforeTransactional(ProceedingJoinPoint joinPoint, Transactional tx) throws Throwable {
		try {
	        log.info("設定資料源, readOnly=" + tx.readOnly());
	        
	        if(tx.readOnly()) {
	        	// 純讀取 ， 切換到 Slave
	            DataSourceContextHolder.set(DataSourceType.SLAVE);
	            
	            log.info("設定資料源, readOnly=true");
	        } else {
	        	// 切換到 Master
	            DataSourceContextHolder.set(DataSourceType.MASTER);
	            
	            log.info("設定資料源, readOnly=false");
	        }
	        
	        // @Around 一定要有 joinPoint.proceed()
	        // 要 trurn 回去 ， 被攔截的方法才會繼續執行
	        return joinPoint.proceed();
	    } finally {
	    	// 確保最後都會清除 ThreadLocal 中的資料源設定，避免影響下一個請求
	        DataSourceContextHolder.clear();
	        
	        log.info("清除資料源上下文");
	    }
	}
}