package com.lemongo97.android.anti.adb.client.utils;

public class ADBMessageUtils {
	public static String getCommandLength(CharSequence command) {
		return String.format("%04x", command.toString().getBytes().length);
	}

	public static int getMessageLength(CharSequence command) {
		return Integer.parseInt(command.toString(), 16);
	}
}
