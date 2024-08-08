package com.lemongo97.android.anti.scrcpy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ScrcpyMessageHandler extends SimpleChannelInboundHandler<ScrcpyPacket> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ScrcpyPacket msg) throws Exception {
		// 视频流
		System.out.println(msg);
	}
}
