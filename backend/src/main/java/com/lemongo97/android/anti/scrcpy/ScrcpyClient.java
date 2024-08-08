package com.lemongo97.android.anti.scrcpy;

import com.lemongo97.android.anti.adb.client.stream.ADBStreamMessageHandler;
import com.lemongo97.android.anti.adb.client.stream.ADBStreamProtocolHandler;
import com.lemongo97.android.anti.adb.client.utils.ADBMessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ScrcpyClient {

	private final ScrcpySocketType socketType;
	private static final String DEFAULT_HOST = "127.0.0.1";
	private static final int DEFAULT_PORT = 27183;
	private final String host;
	private final int port;
	private EventLoopGroup workerGroup;
	private ChannelFuture channelFuture;

	public ScrcpyClient(ScrcpySocketType socketType, String host, int port) {
		this.socketType = socketType;
		this.host = host;
		this.port = port;
	}

	public ScrcpyClient(ScrcpySocketType socketType) {
		this(socketType, DEFAULT_HOST, DEFAULT_PORT);
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
				ch.pipeline().addLast(new ScrcpyProtocolHandler(socketType));
				ch.pipeline().addLast(new ScrcpyMessageHandler());
			}
		});
		this.channelFuture = bootstrap.connect(host, port).sync().addListener((ChannelFutureListener) channelFuture -> {
			if (channelFuture.isSuccess()) {
				//TODO 使用adb启动 scrcpy-server
			}
		});
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
