package com.lemongo97.android.anti.scrcpy.constants;

import io.netty.util.AttributeKey;

public interface ScrcpyConstants {
	AttributeKey<Boolean> FORWARD= AttributeKey.valueOf("FORWARD");
	AttributeKey<Boolean> DEVICE_NAME= AttributeKey.valueOf("DEVICE_NAME");
	AttributeKey<Boolean> VIDEO_HEADER= AttributeKey.valueOf("VIDEO_HEADER");
	AttributeKey<Boolean> AUDIO_HEADER = AttributeKey.valueOf("AUDIO_HEADER");

	int DEVICE_NAME_LENGTH = 64;
	int VIDEO_HEADER_LENGTH = 12;
	int AUDIO_HEADER_LENGTH = 4;

	int PTS_FLAG_LENGTH = 8;
	int MEDIA_FRAME_LENGTH_INDEX = 8;
	int MEDIA_FRAME_LENGTH = 4;
	int MEDIA_FRAME_HEADER_LENGTH = PTS_FLAG_LENGTH + MEDIA_FRAME_LENGTH;

}
