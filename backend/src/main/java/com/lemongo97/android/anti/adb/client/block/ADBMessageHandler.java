package com.lemongo97.android.anti.adb.client.block;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ADBMessageHandler extends SimpleChannelInboundHandler<ADBMessage> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ADBMessage msg) throws Exception {
		System.out.println(msg);
	}
}
