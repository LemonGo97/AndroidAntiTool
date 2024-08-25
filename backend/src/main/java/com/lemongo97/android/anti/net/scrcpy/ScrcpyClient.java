package com.lemongo97.android.anti.net.scrcpy;

import com.lemongo97.android.anti.net.scrcpy.callback.ScrcpyCallback;
import com.lemongo97.android.anti.net.scrcpy.constants.ScrcpySocketType;
import com.lemongo97.android.anti.net.scrcpy.handler.ScrcpyAudioHandshakeHandler;
import com.lemongo97.android.anti.net.scrcpy.handler.ScrcpyHandshakeHandler;
import com.lemongo97.android.anti.net.scrcpy.handler.ScrcpyVideoHandshakeHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.array.ArrayUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class ScrcpyClient {

	private final String host;
	private final int port;
	private final ScrcpyConfig scrcpyConfig;
	private final Map<ScrcpySocketType, ChannelFuture> channelFutureMap = new HashMap<>();
	private final EventLoopGroup sharedGroup = new NioEventLoopGroup();
	private final Collection<ScrcpyCallback<?>> callbacks;

	public ScrcpyClient(String host, int port, ScrcpyConfig scrcpyConfig, Collection<ScrcpyCallback<?>> callbacks) {
		this.host = host;
		this.port = port;
		this.scrcpyConfig = scrcpyConfig;
		this.callbacks = Optional.ofNullable(callbacks).orElse(Collections.emptyList());
	}

	public Map<ScrcpySocketType, ChannelFuture> connect() throws InterruptedException {
		// TODO adb 端口转发 localabstract:scrcpy 到本地 27183 端口
		boolean firstSocketInitialized = false;
		if (scrcpyConfig.enableVideo()) {
			firstSocketInitialized = true;
			ChannelFuture videoSocket = this.createScrcpySocket(ScrcpySocketType.VIDEO, true);
			this.channelFutureMap.put(ScrcpySocketType.VIDEO, videoSocket);
		}
		if (scrcpyConfig.enableAudio()) {
			ChannelFuture audioSocket = this.createScrcpySocket(ScrcpySocketType.AUDIO, !firstSocketInitialized && (firstSocketInitialized = true));
			this.channelFutureMap.put(ScrcpySocketType.AUDIO, audioSocket);
		}
		if (scrcpyConfig.enableControl()) {
			ChannelFuture controlSocket = this.createScrcpySocket(ScrcpySocketType.CONTROL, !firstSocketInitialized);
			this.channelFutureMap.put(ScrcpySocketType.CONTROL, controlSocket);
		}
		return this.channelFutureMap;
	}

	private ChannelFuture createScrcpySocket(ScrcpySocketType socketType, boolean firstSocket, ChannelHandler... channelHandlers) throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap()
				.group(this.sharedGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@SuppressWarnings("NullableProblems")
					@Override
					public void initChannel(SocketChannel channel) {
						if (ArrayUtil.isNotEmpty(channelHandlers)) {
							channel.pipeline().addLast(channelHandlers);
						} else {
							ChannelHandler handshakeHandler = null;
							switch (socketType) {
								case VIDEO -> handshakeHandler = new ScrcpyVideoHandshakeHandler(callbacks);
								case AUDIO -> handshakeHandler = new ScrcpyAudioHandshakeHandler(callbacks);
								// todo 控制socket
							}
							channel.pipeline().addLast(firstSocket ? new ScrcpyHandshakeHandler(handshakeHandler, callbacks) : handshakeHandler);
						}
					}
				});
		return bootstrap.connect(this.host, this.port).sync();
	}

	public void await(ScrcpySocketType socketType) throws InterruptedException {
		ChannelFuture channelFuture = this.channelFutureMap.get(socketType);
		channelFuture.channel().closeFuture().sync();
	}

	public void send(ScrcpySocketType socketType, String command) {
		ChannelFuture channelFuture = this.channelFutureMap.get(socketType);
		if (channelFuture == null) {
			log.error("channelFuture is null");
			return;
		}
		ByteBuf buffer = channelFuture.channel().alloc().buffer();
		buffer.writeCharSequence(command, StandardCharsets.UTF_8);
		channelFuture.channel().writeAndFlush(buffer);
	}

	public void disconnect() {
		this.sharedGroup.shutdownGracefully();
	}

}
