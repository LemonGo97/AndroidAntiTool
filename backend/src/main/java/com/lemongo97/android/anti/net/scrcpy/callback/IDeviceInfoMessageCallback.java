package com.lemongo97.android.anti.net.scrcpy.callback;

import com.lemongo97.android.anti.net.scrcpy.packet.ScrcpyDeviceInfoPacket;

public abstract class IDeviceInfoMessageCallback implements ScrcpyCallback<ScrcpyDeviceInfoPacket> {

	@Override
	final public ScrcpyCallbackType getType() {
		return ScrcpyCallbackType.DEVICE_INFO;
	}
}