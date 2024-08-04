package com.lemongo97.android.anti.adb.exception;

import com.lemongo97.android.anti.exception.GlobalException;

public class AdbServerConnectException extends GlobalException {

	private final static String message = "ADB Server 连接失败！";

	public AdbServerConnectException() {
		super(message);
	}

	public AdbServerConnectException(String message) {
		super(message);
	}

	public AdbServerConnectException(String message, Throwable cause) {
		super(message, cause);
	}

	public AdbServerConnectException(Throwable cause) {
		this(message, cause);
	}

}
