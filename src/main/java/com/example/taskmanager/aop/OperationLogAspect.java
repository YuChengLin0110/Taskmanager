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
	
	@Around("@annotation(operationLog)")
	public Object logOperation(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
		String action = operationLog.value();
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String args = Arrays.toString(joinPoint.getArgs());
		
		log.info("[OPERATION] {} - {}.{} args = {}", action, className, methodName, args);
		return joinPoint.proceed();
	}
}
