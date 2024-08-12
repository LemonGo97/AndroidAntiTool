package com.lemongo97.android.anti.scrcpy.codec.packet;

import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;

import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.VIDEO_FRAME;
import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.VIDEO_METADATA;

@Data
@AllArgsConstructor
public class ScrcpyVideoMetaDataPacket implements ScrcpyPacket {

	private CharSequence codec;
	private int width;
	private int height;

	public ScrcpyVideoMetaDataPacket(ByteBuf buf) {
		this.codec = buf.readCharSequence(4, StandardCharsets.UTF_8);
		this.width = buf.readInt();
		this.height = buf.readInt();
	}

	@Override
	public ScrcpyPacketType getPacketType() {
		return VIDEO_METADATA;
	}
}
