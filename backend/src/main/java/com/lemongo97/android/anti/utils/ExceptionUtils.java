package com.lemongo97.android.anti.utils;


public class ExceptionUtils {

	/**
	 * 忽略异常并返回Null
	 * @param processor 执行的方法
	 * @return null
	 */
	public static <T> T ignoreExceptionReturnNull(IgnoreExceptionProcessorReturned<T> processor) {
		try {
			return processor.process();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 忽略异常并返回指定值
	 * @param processor 执行的方法
	 * @param defaultValue 默认值
	 * @return 正常执行返回的方法或默认值
	 */
	public static <T> T ignoreExceptionReturnDefault(IgnoreExceptionProcessorReturned<T> processor, T defaultValue) {
		try {
			return processor.process();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 忽略原异常并抛出新异常
	 * @param processor 执行的方法
	 * @param exception 新的异常
	 * @return 正常执行返回值
	 * @throws E 抛出的新异常
	 */
	public static <T, E extends Exception> T ignoreExceptionThrowNew(IgnoreExceptionProcessorReturned<T> processor, E exception) throws E {
		try {
			return processor.process();
		} catch (Exception e) {
			exception.setStackTrace(e.getStackTrace());
			throw exception;
		}
	}

	public static <E extends Exception> void ignoreExceptionVoid(IgnoreExceptionProcessorVoid processor) throws E {
		try {
			processor.process();
		} catch (Exception ignore) {
		}
	}

	public static <E extends Exception> void ignoreExceptionVoidThrowNew(IgnoreExceptionProcessorVoid processor, E exception) throws E {
		try {
			processor.process();
		} catch (Exception e) {
			exception.setStackTrace(e.getStackTrace());
			throw exception;
		}
	}

	public interface IgnoreExceptionProcessorReturned<T> {
		T process() throws Exception;
	}

	public interface IgnoreExceptionProcessorVoid {
		void process() throws Exception;
	}

}
