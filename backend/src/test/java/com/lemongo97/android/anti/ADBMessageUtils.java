package com.lemongo97.android.anti;

public class ADBMessageUtils {
	public static String getCommandLength(String command) {
		return String.format("%04x", command.getBytes().length);
	}

	public static int getMessageLength(CharSequence command) {
		return Integer.parseInt(command.toString(), 16);
	}

	public static void main(String[] args) {
		System.out.println(String.format("%04x", 473+15));
	}
}
