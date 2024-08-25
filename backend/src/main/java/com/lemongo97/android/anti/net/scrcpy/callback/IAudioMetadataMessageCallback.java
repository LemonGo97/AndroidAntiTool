package com.lemongo97.android.anti.net.scrcpy.callback;

import com.lemongo97.android.anti.net.scrcpy.packet.ScrcpyAudioMetaDataPacket;

public abstract class IAudioMetadataMessageCallback implements ScrcpyCallback<ScrcpyAudioMetaDataPacket> {

	@Override
	final public ScrcpyCallbackType getType() {
		return ScrcpyCallbackType.AUDIO_METADATA;
	}

}
