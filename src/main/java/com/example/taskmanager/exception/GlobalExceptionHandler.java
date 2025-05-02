package com.example.taskmanager.exception;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.taskmanager.entity.ApiResponse;

// 讓這個類別變成一個全域的例外處理器，所有的例外都會進到這裡來處理
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException e) {
		
		// HTTP 500 伺服器內部錯誤
		return ResponseEntity.internalServerError().body(ApiResponse.fail("Server error : " + e.getMessage()));
	}
	
	/**
	 * 處理參數驗證錯誤，這是當 DTO 物件上的驗證註解不符合要求時拋出的例外
	 * 搭配 Spring Validation 的 @NotNull 或 @Size 或其他驗證註解使用
	 * 錯誤回應格式範例：
	 * {
	 * 		"status": "fail",
	 * 		"message": "Validation errors: username : must not be empty;
	 * {
	 * 本範例僅取第一個錯誤訊息進行回應
	 * */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException e){
		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append("Validation error ");
		
		// 獲取所有的欄位錯誤
		List<FieldError> errors = e.getBindingResult().getFieldErrors();
		
		// 只取第一個錯誤，可以根據需求擴展為多個錯誤
		if(errors != null && !errors.isEmpty()) {
			FieldError firstError = errors.get(0); 
			
			errorMsg.append(firstError.getField()).append(" : ").append(firstError.getDefaultMessage());
		}
		
		// HTTP 400 請求無效
		return ResponseEntity.badRequest().body(ApiResponse.fail(errorMsg.toString()));
	}
	
	// 處理所有未處理的通用異常
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<?>> handleGeneric(Exception e){
		
		// HTTP 500 伺服器內部錯誤
		return ResponseEntity.internalServerError().body(ApiResponse.fail("Error : " + e.getMessage()));
	}
}