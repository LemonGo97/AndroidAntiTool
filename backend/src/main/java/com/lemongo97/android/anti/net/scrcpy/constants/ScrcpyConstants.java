package com.lemongo97.android.anti.net.scrcpy.constants;


public interface ScrcpyConstants {

	int DEVICE_NAME_LENGTH = 64;
	int VIDEO_HEADER_LENGTH = 12;
	int AUDIO_HEADER_LENGTH = 4;

	int PTS_FLAG_LENGTH = 8;
	int MEDIA_FRAME_LENGTH_INDEX = 8;
	int MEDIA_FRAME_LENGTH = 4;
	int MEDIA_FRAME_HEADER_LENGTH = PTS_FLAG_LENGTH + MEDIA_FRAME_LENGTH;

}
