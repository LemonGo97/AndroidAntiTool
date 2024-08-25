package com.lemongo97.android.anti.net.scrcpy.callback;

import com.lemongo97.android.anti.net.scrcpy.packet.ScrcpyMediaPacket;

public abstract class IMediaMessageCallback implements ScrcpyCallback<ScrcpyMediaPacket> {

	@Override
	final public ScrcpyCallbackType getType() {
		return ScrcpyCallbackType.MEDIA_MESSAGE;
	}
}
