package com.lemongo97.android.anti.scrcpy.codec;

import com.google.gson.Gson;
import com.lemongo97.android.anti.scrcpy.codec.packet.ScrcpyPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
public abstract class ScrcpyMessageHandler extends SimpleChannelInboundHandler<ScrcpyPacket> {
	protected final WebSocketSession webSocketSession;
	protected final Gson gson;

	public ScrcpyMessageHandler(WebSocketSession webSocketSession, Gson gson) {
		this.webSocketSession = webSocketSession;
		this.gson = gson;
	}

	@Override
	protected final void channelRead0(ChannelHandlerContext ctx, ScrcpyPacket msg) throws Exception {
		this.read(ctx, msg);
	}

	abstract protected void read(ChannelHandlerContext ctx, ScrcpyPacket msg) throws IOException;

	@Override
	public final void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.webSocketSession.close();
		super.channelInactive(ctx);
	}
}
