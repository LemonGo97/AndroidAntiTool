package com.lemongo97.android.anti.net.scrcpy.packet;

import com.lemongo97.android.anti.net.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.lemongo97.android.anti.net.scrcpy.constants.ScrcpyPacketType.DEVICE_NAME;


@Data
@AllArgsConstructor
public class ScrcpyDeviceInfoPacket implements ScrcpyPacket {

	private String deviceName;
	private final ScrcpyPacketType packetType = DEVICE_NAME;

	public ScrcpyDeviceInfoPacket(ByteBuf byteBuf) {
		byte[] bytes = ByteBufUtil.getBytes(byteBuf);
		byte[] result = null;
		for (int i = bytes.length - 1; i >= 0; i--) {
			if (bytes[i] !=0){
				result = Arrays.copyOfRange(bytes, 0, i);
				break;
			}
		}
		this.deviceName = result == null ?  null : new String(result, StandardCharsets.UTF_8);
		ReferenceCountUtil.release(byteBuf);
	}
}
