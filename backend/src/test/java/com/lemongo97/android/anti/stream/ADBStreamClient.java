package com.lemongo97.android.anti.stream;

import com.lemongo97.android.anti.ADBMessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

public class ADBStreamClient {
	public static void main(String[] args) throws Exception {
		String host = "127.0.0.1";
		int port = 5037;
		String command = "host:transport:127.0.0.1:16384";
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) {
					ch.pipeline().addLast(new ADBStreamProtocolHandler());
					ch.pipeline().addLast(new ADBStreamMessageHandler());
				}
			});

			ChannelFuture f = b.connect(host, port).sync().addListener((ChannelFutureListener) channelFuture -> {
				if (channelFuture.isSuccess()) {
					ByteBuf buffer = channelFuture.channel().alloc().buffer();
					buffer.writeCharSequence(ADBMessageUtils.getCommandLength(command), StandardCharsets.UTF_8);
					buffer.writeCharSequence(command, StandardCharsets.UTF_8);
					channelFuture.channel().writeAndFlush(buffer);
				}
			});
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}
