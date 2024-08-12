package com.lemongo97.android.anti.scrcpy.codec.packet;

import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;

import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.DEVICE_NAME;

@Data
@AllArgsConstructor
public class ScrcpyDeviceInfoPacket implements ScrcpyPacket{

	private CharSequence deviceName;

	public ScrcpyDeviceInfoPacket(ByteBuf byteBuf) {
		this.deviceName = byteBuf.readCharSequence(byteBuf.indexOf(0, byteBuf.readableBytes(), (byte)0), StandardCharsets.UTF_8);
		byteBuf.readerIndex(byteBuf.readableBytes());
	}

	@Override
	public ScrcpyPacketType getPacketType() {
		return DEVICE_NAME;
	}
}
