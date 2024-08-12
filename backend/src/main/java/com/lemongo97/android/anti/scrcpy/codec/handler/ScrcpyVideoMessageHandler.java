package com.lemongo97.android.anti.scrcpy.codec.handler;

import com.google.gson.Gson;
import com.lemongo97.android.anti.scrcpy.codec.ScrcpyMessageHandler;
import com.lemongo97.android.anti.scrcpy.codec.packet.ScrcpyPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
public class ScrcpyVideoMessageHandler extends ScrcpyMessageHandler {

	public ScrcpyVideoMessageHandler(WebSocketSession webSocketSession, Gson gson) {
		super(webSocketSession, gson);
	}

	@Override
	protected void read(ChannelHandlerContext ctx, ScrcpyPacket msg) throws IOException {
		this.webSocketSession.sendMessage(new TextMessage(gson.toJson(msg)));
	}

}
