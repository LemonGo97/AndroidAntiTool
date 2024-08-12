package com.lemongo97.android.anti.scrcpy.codec;

import com.lemongo97.android.anti.scrcpy.*;
import com.lemongo97.android.anti.scrcpy.codec.packet.*;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpyConstants;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpyMode;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpySocketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;

import java.util.List;
import java.util.Optional;

import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyConstants.*;

/**
 * Scrcpy 协议解析
 */
public class ScrcpyProtocolDecoder extends ByteToMessageDecoder {

	private final ScrcpySocketType socketType;

	public ScrcpyProtocolDecoder(ScrcpyMode scrcpyMode, ScrcpySocketType socketType) {
		this.socketType = socketType;
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

		Attribute<Boolean> FORWARD = channelHandlerContext.channel().attr(ScrcpyConstants.FORWARD);
		if (!Optional.ofNullable(FORWARD.get()).orElse(false)) {
			if (byteBuf.readableBytes() < 1){
				return;
			}
			FORWARD.set(true);
			list.add(new ScrcpyModePacket(byteBuf));
			return;
		}

		Attribute<Boolean> DEVICE_NAME = channelHandlerContext.channel().attr(ScrcpyConstants.DEVICE_NAME);

		// 设备名称
		if (!Optional.ofNullable(DEVICE_NAME.get()).orElse(false)) {
			if (byteBuf.readableBytes() < DEVICE_NAME_LENGTH){
				return;
			}
			DEVICE_NAME.set(true);
			list.add(new ScrcpyDeviceInfoPacket(byteBuf));
			return;
		}

		switch (socketType) {
			case VIDEO -> this.handleVideoSocket(channelHandlerContext, byteBuf, list);
			case AUDIO -> this.handleAudioSocket(channelHandlerContext, byteBuf, list);
			case CONTROL -> this.handleControlSocket(channelHandlerContext, byteBuf, list);
		}
	}

	private void handleVideoSocket(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){
		Attribute<Boolean> VIDEO_HEADER = channelHandlerContext.channel().attr(ScrcpyConstants.VIDEO_HEADER);
		if (!Optional.ofNullable(VIDEO_HEADER.get()).orElse(false)) {
			if (byteBuf.readableBytes() < VIDEO_HEADER_LENGTH){
				return;
			}
			VIDEO_HEADER.set(true);
			list.add(new ScrcpyVideoMetaDataPacket(byteBuf));
			return;
		}
		this.handleMediaSocket(byteBuf, list);
	}

	private void handleAudioSocket(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){
		Attribute<Boolean> AUDIO_HEADER = channelHandlerContext.channel().attr(ScrcpyConstants.AUDIO_HEADER);
		if (!Optional.ofNullable(AUDIO_HEADER.get()).orElse(false)) {
			if (byteBuf.readableBytes() < AUDIO_HEADER_LENGTH){
				return;
			}
			AUDIO_HEADER.set(true);
			list.add(new ScrcpyAudioMetaDataPacket(byteBuf));
			return;
		}
		this.handleMediaSocket(byteBuf, list);
	}

	private void handleMediaSocket(ByteBuf byteBuf, List<Object> list){
		if (byteBuf.readableBytes() < MEDIA_FRAME_HEADER_LENGTH) {
			return;
		}
		int length = byteBuf.getInt(MEDIA_FRAME_LENGTH_INDEX);
		if (byteBuf.readableBytes() < length + MEDIA_FRAME_HEADER_LENGTH) {
			return;
		}
		list.add(new ScrcpyMediaPacket(byteBuf));
	}

	/**
	 * TODO
	 * ControlMessage（从客户端到设备）：
	 * 		序列化：	https://github.com/Genymobile/scrcpy/blob/master/app/tests/test_control_msg_serialize.c
	 * 		反序列化：https://github.com/Genymobile/scrcpy/tree/master/server/src/test/java/com/genymobile/scrcpy/control/ControlMessageReaderTest.java
	 * DeviceMessage（从设备到客户端）
	 * 		序列化：	https://github.com/Genymobile/scrcpy/tree/master/server/src/test/java/com/genymobile/scrcpy/control/DeviceMessageWriterTest.java
	 * 		反序列化：https://github.com/Genymobile/scrcpy/blob/master/app/tests/test_device_msg_deserialize.c
	 *
	 * @param channelHandlerContext
	 * @param byteBuf
	 * @param list
	 */
	private void handleControlSocket(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){
		// TODO 控制流Socket处理 协议参见 https://github.com/Genymobile/scrcpy/tree/master/server/src/test/java/com/genymobile/scrcpy/control
	}
}
