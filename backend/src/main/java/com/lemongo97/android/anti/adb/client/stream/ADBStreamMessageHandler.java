package com.lemongo97.android.anti.adb.client.stream;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class ADBStreamMessageHandler extends SimpleChannelInboundHandler<CharSequence> {

	private final WebSocketSession webSocketSession;

	public ADBStreamMessageHandler(WebSocketSession webSocketSession) {
		this.webSocketSession = webSocketSession;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CharSequence msg) throws Exception {
		this.webSocketSession.sendMessage(new TextMessage(msg));
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.webSocketSession.close();
		super.channelInactive(ctx);
	}
}
