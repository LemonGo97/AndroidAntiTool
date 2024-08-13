package com.lemongo97.android.anti.scrcpy.codec.packet;

import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;

import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.AUDIO_METADATA;

@Data
@AllArgsConstructor
public class ScrcpyAudioMetaDataPacket implements ScrcpyPacket {

	private String codec;
	private final ScrcpyPacketType packetType = AUDIO_METADATA;

	public ScrcpyAudioMetaDataPacket(ByteBuf buf) {
		this.codec = buf.readCharSequence(4, StandardCharsets.UTF_8).toString();
	}

}
