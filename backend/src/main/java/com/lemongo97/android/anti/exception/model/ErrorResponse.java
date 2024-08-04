package com.lemongo97.android.anti.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 错误响应类，可通过静态方法创建或使用new创建
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

	private int code;
	private String message;
	private Object details;

	public static ErrorResponse create(int code, String message, Object details) {
		return new ErrorResponse(code, message, details);
	}

	public static ErrorResponse create(String message, Object details) {
		return create(5001, message, details);
	}

	public static ErrorResponse create(int code, String message) {
		return create(code, message, null);
	}

	public static ErrorResponse create(String message) {
		return create(message, null);
	}

	public static ErrorResponse create() {
		return create(5001,null);
	}

	public static ErrorResponse create(ErrorCode errorCode) {
		return create(errorCode.getCode(), errorCode.getMessage());
	}

	public static ErrorResponse create(ErrorCode errorCode, String message, Object details) {
		return create(errorCode.getCode(), message, details);
	}

	public static ErrorResponse create(ErrorCode errorCode, String message) {
		return create(errorCode.getCode(), message);
	}

	public static ErrorResponse create(ErrorCode errorCode, Throwable cause) {
		return create(errorCode.getCode(), cause.getMessage(), cause);
	}

	/**
	 * 1000-1999：认证和授权错误
	 * 2000-2999：请求参数错误
	 * 3000-3999：资源错误（如找不到资源）
	 * 4000-4999：业务逻辑错误
	 * 5000-5999：服务器内部错误
	 */
	@Getter
	public enum ErrorCode {
		// 认证和授权错误
		UNAUTHORIZED(1001, "Unauthorized", "Access token is missing or invalid."),
		FORBIDDEN(1002, "Forbidden", "You do not have permission to access this resource."),

		// 请求参数错误
		INVALID_PARAMETER(2001, "Invalid input parameter", "The input parameter is invalid."),
		MISSING_PARAMETER(2002, "Missing input parameter", "The required input parameter is missing."),

		// 资源错误
		RESOURCE_NOT_FOUND(3001, "Resource not found", "The requested resource was not found."),
		RESOURCE_CONFLICT(3002, "Resource conflict", "The requested resource conflicts with an existing resource."),

		// 业务逻辑错误
		USERNAME_ALREADY_EXISTS(4001, "Username already exists", "The username is already taken."),
		BUSINESS_LOGIC_ERROR(4002, "Business logic error", "A business logic error occurred."),

		// 服务器内部错误
		INTERNAL_SERVER_ERROR(5001, "Internal server error", "An unexpected error occurred. Please try again later."),
		SERVICE_UNAVAILABLE(5002, "Service unavailable", "The service is currently unavailable. Please try again later.");

		private final int code;
		private final String message;
		private final String details;

		ErrorCode(int code, String message, String details) {
			this.code = code;
			this.message = message;
			this.details = details;
		}

		@Override
		public String toString() {
			return "ErrorCode{" +
					"code=" + code +
					", message='" + message + '\'' +
					", details='" + details + '\'' +
					'}';
		}
	}
}
