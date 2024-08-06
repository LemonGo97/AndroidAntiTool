package com.lemongo97.android.anti.block;

import com.lemongo97.android.anti.ADBMessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;

public class ADBClient {
	public static void main(String[] args) throws Exception {
		String host = "127.0.0.1";
		int port = 5037;
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ADBProtocolHandler());
					ch.pipeline().addLast(new ADBMessageHandler());
				}
			});

			ChannelFuture f = b.connect(host, port).sync().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture channelFuture) throws Exception {
					if (channelFuture.isSuccess()) {
						String command = "host:devices";
						ByteBuf buffer = channelFuture.channel().alloc().buffer();
						buffer.writeCharSequence(ADBMessageUtils.getCommandLength(command), StandardCharsets.UTF_8);
						buffer.writeCharSequence(command, StandardCharsets.UTF_8);
						channelFuture.channel().writeAndFlush(buffer);
					}
				}
			});
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}
