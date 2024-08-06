package com.lemongo97.android.anti.adb.client.stream;

import com.lemongo97.android.anti.adb.client.utils.ADBMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.lemongo97.android.anti.adb.client.stream.ADBStreamConstants.IS_FIRST_OK;
import static com.lemongo97.android.anti.adb.client.stream.ADBStreamConstants.IS_SECOND_OK;

public class ADBStreamProtocolHandler extends ByteToMessageDecoder {


	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		Attribute<Boolean> isFirstOk = channelHandlerContext.channel().attr(IS_FIRST_OK);
		Attribute<Boolean> isSecondOk = channelHandlerContext.channel().attr(IS_SECOND_OK);
		String command = "shell:";
		if (!Optional.ofNullable(isFirstOk.get()).orElse(false)) {
			if (byteBuf.readableBytes() < 4) {
				return;
			}
			CharSequence charSequence = byteBuf.readCharSequence(4, StandardCharsets.UTF_8);

			if (charSequence.equals("OKAY")) {
				isFirstOk.set(true);
			}
			ByteBuf buffer = channelHandlerContext.channel().alloc().buffer();
			buffer.writeCharSequence(ADBMessageUtils.getCommandLength(command), StandardCharsets.UTF_8);
			buffer.writeCharSequence(command, StandardCharsets.UTF_8);
			channelHandlerContext.writeAndFlush(buffer);
			list.add("");
			return;
		} else if (!Optional.ofNullable(isSecondOk.get()).orElse(false)) {
			if (byteBuf.readableBytes() < 4) {
				return;
			}
			CharSequence charSequence = byteBuf.readCharSequence(4, StandardCharsets.UTF_8);

			if (charSequence.equals("OKAY")) {
				isSecondOk.set(true);
			}
		}
		list.add(byteBuf.readCharSequence(byteBuf.readableBytes(), StandardCharsets.UTF_8));
	}
}
