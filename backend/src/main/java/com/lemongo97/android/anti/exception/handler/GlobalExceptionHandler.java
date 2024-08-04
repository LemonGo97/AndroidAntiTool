package com.lemongo97.android.anti.exception.handler;

import com.lemongo97.android.anti.adb.controller.AdbController;
import com.lemongo97.android.anti.exception.GlobalException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.ConstraintViolation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import com.lemongo97.android.anti.exception.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@Configuration
@RestControllerAdvice(basePackageClasses = AdbController.class)
public class GlobalExceptionHandler {

	@ExceptionHandler({BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
	public ErrorResponse handleValidatedException(Exception e, HttpServletResponse response) {
		if (!response.isCommitted()) response.reset();
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		if (e instanceof MethodArgumentNotValidException) {
			// BeanValidation exception
			MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
			return ErrorResponse.create(ErrorResponse.ErrorCode.MISSING_PARAMETER, ex.getBindingResult().getAllErrors().stream()
					.map(ObjectError::getDefaultMessage)
					.collect(Collectors.joining("; ")), e);
		} else if (e instanceof ConstraintViolationException) {
			// BeanValidation GET simple param
			ConstraintViolationException ex = (ConstraintViolationException) e;

			return ErrorResponse.create(ErrorResponse.ErrorCode.MISSING_PARAMETER, ex.getConstraintViolations().stream()
					.map(ConstraintViolation::getMessage)
					.collect(Collectors.joining("; ")) , e.getMessage());
		} else if (e instanceof BindException) {
			// BeanValidation GET object param
			BindException ex = (BindException) e;
			return ErrorResponse.create(ErrorResponse.ErrorCode.MISSING_PARAMETER, ex.getAllErrors().stream()
					.map(ObjectError::getDefaultMessage)
					.collect(Collectors.joining("; ")), e.getMessage());
		}
		return ErrorResponse.create(ErrorResponse.ErrorCode.MISSING_PARAMETER, e);
	}

	@ExceptionHandler({GlobalException.class})
	public ErrorResponse handlerGlobalException(GlobalException e, HttpServletResponse response) {
		if (!response.isCommitted()) response.reset();
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return ErrorResponse.create(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
	}

	@ExceptionHandler({Throwable.class})
	public ErrorResponse handlerGlobalException(Throwable th, HttpServletResponse response) {
		if (!response.isCommitted()) response.reset();
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return ErrorResponse.create(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR, "服务器内部错误！", th);
	}

}
