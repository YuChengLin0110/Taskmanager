package com.example.taskmanager.utils;

import java.lang.reflect.Method;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELUtils {
	
	// 建立 Spring Expression Language 的解析器
	// 它可以把像 "'task:lock:' + #taskId" 這種字串解析出來
	private static final ExpressionParser parser = new SpelExpressionParser();
	
	// 用來取得方法的參數名稱
	private static final ParameterNameDiscoverer parameterNameDiscover = new DefaultParameterNameDiscoverer();
	
	/* spel  要解析的 SpEL 表達式字串，例如 "'task:lock:' + #taskId"）
	 * method 對應的 Java 方法，方便取得方法參數名稱
	 * args   呼叫方法時的實際參數值陣列
	 * 解析後的字串結果，例如 "task:lock:123"
	 * 
	 * 範例：
	 * SpEL 是 "'task:lock:' + #taskId"，方法裡有一個參數叫 taskId，值是 123，
	 * 最後就會回傳 "task:lock:123" 這樣的字串。
	 * */
	public static String parse(String spel, Method method, Object[] args) {
		
		// 建立一個 SpEL 的變數存放空間 EvaluationContext ，裡面會放參數名稱和對應的值
		EvaluationContext context = new StandardEvaluationContext();
		
		// 拿到方法裡所有參數的名字，像 "taskId", "userId"
		String[] paramNames = parameterNameDiscover.getParameterNames(method);
		
		// 如果有取得到參數名稱，把參數名稱和參數值放進 SpEL 的變數空間
		if(paramNames != null) {
			for(int i = 0 ; i < paramNames.length; i++) {
				context.setVariable(paramNames[i], args[i]);
			}
		}
		
		// 利用 parser 解析 SpEL 字串，並在 context 裡取得對應的值，最後轉成 String 回傳
		return parser.parseExpression(spel).getValue(context, String.class);
	}
}