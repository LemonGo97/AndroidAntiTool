package com.lemongo97.android.anti.net.scrcpy.constants;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
public enum ScrcpyPacketType {
	FORWARD(1),
	DEVICE_NAME(2),
	VIDEO_FRAME(3),
	AUDIO_FRAME(4),
	MEDIA_FRAME(5),
	VIDEO_METADATA(6),
	AUDIO_METADATA(7);

	private final int code;

	ScrcpyPacketType(int code) {
		this.code = code;
	}
}