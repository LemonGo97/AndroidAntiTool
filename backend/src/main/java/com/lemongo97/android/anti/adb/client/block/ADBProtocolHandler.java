package com.lemongo97.android.anti.adb.client.block;

import com.lemongo97.android.anti.adb.client.utils.ADBMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ADBProtocolHandler extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if (byteBuf.readableBytes() < 8) {
			return;
		}
		int length = ADBMessageUtils.getMessageLength(byteBuf.getCharSequence(4, 4, StandardCharsets.UTF_8));
		if (byteBuf.readableBytes() < length + 8) {
			return;
		}
		CharSequence status = byteBuf.getCharSequence(0, 4, StandardCharsets.UTF_8);
		CharSequence message = byteBuf.getCharSequence(8, length, StandardCharsets.UTF_8);

		byteBuf.readerIndex(length + 8);
		list.add(new ADBMessage(status, length, message));
	}
}
