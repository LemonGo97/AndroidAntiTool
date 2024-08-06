package com.lemongo97.android.anti.adb.client.stream;

import com.lemongo97.android.anti.adb.client.utils.ADBMessageUtils;
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
public class ADBStreamClient {
	private final static String commandPrefix = "host:transport:";
	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_PORT = 5037;
	private final WebSocketSession webSocketSession;
	private final String host;
	private final int port;
	private final String device;
	private EventLoopGroup workerGroup;
	private ChannelFuture channelFuture;

	public ADBStreamClient(WebSocketSession webSocketSession, String device) {
		this(webSocketSession, DEFAULT_HOST, DEFAULT_PORT, device);
	}

	public ADBStreamClient(WebSocketSession webSocketSession, String host, int port, String device) {
		this.webSocketSession = webSocketSession;
		this.host = host;
		this.port = port;
		this.device = device;
	}

	public void connect() throws InterruptedException {

		this.workerGroup = new NioEventLoopGroup();

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) {
				ch.pipeline().addLast(new ADBStreamProtocolHandler());
				ch.pipeline().addLast(new ADBStreamMessageHandler(webSocketSession));
			}
		});
		this.channelFuture = bootstrap.connect(host, port).sync().addListener((ChannelFutureListener) channelFuture -> {
			if (channelFuture.isSuccess()) {
				String command = commandPrefix + device;
				ByteBuf buffer = channelFuture.channel().alloc().buffer();
				buffer.writeCharSequence(ADBMessageUtils.getCommandLength(command), StandardCharsets.UTF_8);
				buffer.writeCharSequence(command, StandardCharsets.UTF_8);
				channelFuture.channel().writeAndFlush(buffer);
			}
		});
//			f.channel().closeFuture().sync();
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
