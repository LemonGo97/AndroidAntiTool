package com.lemongo97.android.anti.net.scrcpy.constants;

import lombok.Getter;

@Getter
public enum ScrcpySocketType {
	AUDIO(ScrcpyPacketType.AUDIO_FRAME),
	VIDEO(ScrcpyPacketType.VIDEO_FRAME),
	CONTROL(null);

	private final ScrcpyPacketType packetType;

	ScrcpySocketType(ScrcpyPacketType packetType) {
		this.packetType = packetType;
	}
}