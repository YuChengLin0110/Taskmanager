package com.example.taskmanager.entity;

public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;

	public ApiResponse(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}
	
	public static <T> ApiResponse<T> success(T data){
		return new ApiResponse<>(true, "OK", data);
	}
	
	public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
	
	public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
