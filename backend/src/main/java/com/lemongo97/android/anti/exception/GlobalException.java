package com.lemongo97.android.anti.exception;

public class GlobalException extends RuntimeException {

	public GlobalException() {
	}

	public GlobalException(String message) {
		super(message);
	}

	public GlobalException(String message, Throwable cause) {
		super(message, cause);
	}

	public GlobalException(Throwable cause) {
		super(cause);
	}

}
