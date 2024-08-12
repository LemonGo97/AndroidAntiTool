package com.lemongo97.android.anti.scrcpy.codec.packet;

import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.FORWARD;

@Data
public class ScrcpyModePacket implements ScrcpyPacket{

	private ByteBuf byteBuf;

	public ScrcpyModePacket(ByteBuf byteBuf) {
		this.byteBuf = byteBuf.readBytes(1);
	}

	@Override
	public ScrcpyPacketType getPacketType() {
		return FORWARD;
	}
}
