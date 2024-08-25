package com.lemongo97.android.anti.net.scrcpy.callback;


import com.lemongo97.android.anti.net.scrcpy.packet.ScrcpyVideoMetaDataPacket;

public abstract class IVideoMetadataMessageCallback implements ScrcpyCallback<ScrcpyVideoMetaDataPacket> {

	@Override
	final public ScrcpyCallbackType getType() {
		return ScrcpyCallbackType.VIDEO_METADATA;
	}
}