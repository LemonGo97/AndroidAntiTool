package com.lemongo97.android.anti.net.scrcpy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true, fluent = true)
public class ScrcpyConfig {
	private boolean adbForward = true;
	private boolean enableVideo = true;
	private boolean enableAudio = true;
	private boolean enableControl = true;

	public static ScrcpyConfig DEFAULT = new ScrcpyConfig();
	public static ScrcpyConfig ONLY_VIDEO = new ScrcpyConfig()
			.adbForward(true)
			.enableVideo(true)
			.enableAudio(false)
			.enableControl(false);
}
