package com.example.taskmanager.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.taskmanager.annotation.OperationLog;

@Aspect
@Component
public class OperationLogAspect {
	
	private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);
	
	// 只攔截有加上 @OperationLog 的方法
	// 這裡的 operationLog 是對應下面方法參數的名稱
	@Around("@annotation(operationLog)")
	public Object logOperation(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
		String action = operationLog.value();
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String args = Arrays.toString(joinPoint.getArgs());
		
		log.info("[OPERATION] {} - {}.{} args = {}", action, className, methodName, args);
		
		// @Around 一定要有 joinPoint.proceed()
        // 要 trurn 回去 ， 被攔截的方法才會繼續執行
		return joinPoint.proceed();
	}
}
