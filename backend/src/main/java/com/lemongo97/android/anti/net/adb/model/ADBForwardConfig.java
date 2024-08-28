package com.lemongo97.android.anti.net.adb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ADBForwardConfig {
	private String serial;
	private String local;
	private String target;

	public static List<ADBForwardConfig> parse(String str){
		String[] lines = str.split("\n");
		ArrayList<ADBForwardConfig> devices = new ArrayList<>(lines.length);
		for (String line : lines) {
			String[] parts = line.split("\\s");
			if (parts.length > 1) {
				devices.add(new ADBForwardConfig(parts[0], parts[1], parts[2])); // parts[1] is type
			}
		}
		return devices;
	}
}
