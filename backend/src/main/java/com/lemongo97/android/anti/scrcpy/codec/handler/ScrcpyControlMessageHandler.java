package com.lemongo97.android.anti.scrcpy.codec.handler;

import com.google.gson.Gson;
import com.lemongo97.android.anti.adb.model.ScrcpyWebSocketMessage;
import com.lemongo97.android.anti.scrcpy.codec.packet.ScrcpyPacket;
import com.lemongo97.android.anti.scrcpy.codec.ScrcpyMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.codec.binary.Base64;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
public class ScrcpyControlMessageHandler extends ScrcpyMessageHandler {

	public ScrcpyControlMessageHandler(WebSocketSession webSocketSession, Gson gson) {
		super(webSocketSession, gson);
	}

	@Override
	protected void read(ChannelHandlerContext ctx, ScrcpyPacket msg) throws IOException {
		this.webSocketSession.sendMessage(new TextMessage(gson.toJson(msg)));
	}
}
