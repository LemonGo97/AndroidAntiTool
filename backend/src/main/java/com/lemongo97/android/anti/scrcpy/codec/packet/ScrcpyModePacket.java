package com.lemongo97.android.anti.scrcpy.codec.packet;

import com.google.gson.annotations.JsonAdapter;
import com.lemongo97.android.anti.config.gson.serializer.ByteBufSerializer;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.FORWARD;

@Data
public class ScrcpyModePacket implements ScrcpyPacket{

	@JsonAdapter(ByteBufSerializer.class)
	private ByteBuf byteBuf;
	private final ScrcpyPacketType packetType = FORWARD;

	public ScrcpyModePacket(ByteBuf byteBuf) {
		this.byteBuf = byteBuf.readBytes(1);
	}
}
