package com.lemongo97.android.anti.scrcpy;

import com.google.gson.Gson;
import com.lemongo97.android.anti.scrcpy.codec.*;
import com.lemongo97.android.anti.scrcpy.codec.handler.ScrcpyAudioMessageHandler;
import com.lemongo97.android.anti.scrcpy.codec.handler.ScrcpyControlMessageHandler;
import com.lemongo97.android.anti.scrcpy.codec.handler.ScrcpyVideoMessageHandler;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpyMode;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpySocketType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ScrcpyClient {

	private final ScrcpySocketType socketType;
	private final ScrcpyMode scrcpyMode;
	private final WebSocketSession webSocketSession;
	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_PORT = 27183;
	private final String host;
	private final int port;
	private final Gson gson;
	private EventLoopGroup workerGroup;
	private ChannelFuture channelFuture;

	public ScrcpyClient(ScrcpySocketType socketType, ScrcpyMode scrcpyMode, WebSocketSession webSocketSession, Gson gson, String host, int port) {
		this.socketType = socketType;
		this.scrcpyMode = scrcpyMode;
		this.webSocketSession = webSocketSession;
		this.gson = gson;
		this.host = host;
		this.port = port;
	}

	public ScrcpyClient(ScrcpySocketType socketType, ScrcpyMode scrcpyMode, WebSocketSession webSocketSession, Gson gson) {
		this(socketType, scrcpyMode, webSocketSession, gson, DEFAULT_HOST, DEFAULT_PORT);
	}

	public void connect() throws InterruptedException {
		// TODO adb 端口转发 localabstract:scrcpy 到本地 27183 端口

		this.workerGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) {
				ch.pipeline().addLast(new ScrcpyProtocolDecoder(scrcpyMode, socketType));
				ScrcpyMessageHandler scrcpyMessageHandler;
				switch (socketType){
					case AUDIO -> scrcpyMessageHandler = new ScrcpyAudioMessageHandler(webSocketSession, gson);
					case VIDEO -> scrcpyMessageHandler = new ScrcpyVideoMessageHandler(webSocketSession, gson);
					case CONTROL -> scrcpyMessageHandler = new ScrcpyControlMessageHandler(webSocketSession, gson);
					default -> throw new RuntimeException("Unknown socket type");
				}
				ch.pipeline().addLast(scrcpyMessageHandler);
			}
		});
		this.channelFuture = bootstrap.connect(host, port).addListener((ChannelFutureListener) channelFuture -> {
			if (channelFuture.isSuccess()) {
				//TODO 使用adb启动 scrcpy-server
			} else {
				channelFuture.cause().printStackTrace();
			}
		}).sync();
	}

	public void await() throws InterruptedException {
		this.channelFuture.channel().closeFuture().sync();
	}

	public void send(String command) {
		if (this.channelFuture == null) {
			log.error("channelFuture is null");
			return;
		}
		ByteBuf buffer = channelFuture.channel().alloc().buffer();
		buffer.writeCharSequence(command, StandardCharsets.UTF_8);
		this.channelFuture.channel().writeAndFlush(buffer);
	}

	public void disconnect() {
		this.workerGroup.shutdownGracefully();
	}

}
