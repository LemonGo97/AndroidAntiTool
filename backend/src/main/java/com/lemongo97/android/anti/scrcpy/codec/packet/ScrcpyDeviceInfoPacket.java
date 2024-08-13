package com.lemongo97.android.anti.scrcpy.codec.packet;

import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.collection.ListUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.DEVICE_NAME;
import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.MEDIA_FRAME;

@Data
@AllArgsConstructor
public class ScrcpyDeviceInfoPacket implements ScrcpyPacket{

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
