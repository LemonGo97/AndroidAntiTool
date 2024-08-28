package com.lemongo97.android.anti.net.adb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ADBDevice {
	private String serial;
	private String status;

	public static List<ADBDevice> parse(String str){
		String[] lines = str.split("\n");
		ArrayList<ADBDevice> devices = new ArrayList<>(lines.length);
		for (String line : lines) {
			String[] parts = line.split("\t");
			if (parts.length > 1) {
				devices.add(new ADBDevice(parts[0], parts[1])); // parts[1] is type
			}
		}
		return devices;
	}
}
