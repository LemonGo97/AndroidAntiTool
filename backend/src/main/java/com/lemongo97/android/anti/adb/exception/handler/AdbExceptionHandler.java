package com.lemongo97.android.anti.adb.exception.handler;

import com.lemongo97.android.anti.adb.controller.AdbController;
import com.lemongo97.android.anti.adb.exception.AdbServerConnectException;
import com.lemongo97.android.anti.exception.model.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Configuration
@RestControllerAdvice(basePackageClasses = AdbController.class)
public class AdbExceptionHandler {

	@ExceptionHandler({AdbServerConnectException.class})
	public ErrorResponse handlerAdbServerConnectException(AdbServerConnectException e, HttpServletResponse response) {
		if (!response.isCommitted()) response.reset();
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return ErrorResponse.create(ErrorResponse.ErrorCode.INTERNAL_SERVER_ERROR, e);
	}

}
