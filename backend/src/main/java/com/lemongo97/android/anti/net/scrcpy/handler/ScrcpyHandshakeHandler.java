package com.lemongo97.android.anti.net.scrcpy.handler;

import com.lemongo97.android.anti.net.scrcpy.callback.IDeviceInfoMessageCallback;
import com.lemongo97.android.anti.net.scrcpy.callback.ScrcpyCallback;
import com.lemongo97.android.anti.net.scrcpy.callback.ScrcpyCallbackType;
import com.lemongo97.android.anti.net.scrcpy.packet.ScrcpyDeviceInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.lemongo97.android.anti.net.scrcpy.constants.ScrcpyConstants.DEVICE_NAME_LENGTH;

@Slf4j
public class ScrcpyHandshakeHandler extends ByteToMessageDecoder {

	private final ChannelHandler handshakeHandler;
	private final List<IDeviceInfoMessageCallback> callbacks = new ArrayList<>();
	boolean adbForward = false;
	boolean receivedDeviceName = false;

	public ScrcpyHandshakeHandler(ChannelHandler handshakeHandler, Collection<ScrcpyCallback<?>> callbacks) {
		this.handshakeHandler = handshakeHandler;
		this.callbacks.addAll(callbacks.stream()
				.filter(callback -> callback.getType() == ScrcpyCallbackType.DEVICE_INFO)
				.map(callback -> (IDeviceInfoMessageCallback)callback)
				.collect(Collectors.toSet()));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (!this.adbForward) {
			this.handleADBForward(in);
			return;
		}
		if (!this.receivedDeviceName) {
			this.handleReceivedDeviceName(in);
			return;
		}
		ctx.pipeline().addLast(handshakeHandler);
		ctx.pipeline().remove(this);
	}

	private void handleReceivedDeviceName(ByteBuf byteBuf) {
		if (byteBuf.readableBytes() < DEVICE_NAME_LENGTH) {
			return;
		}
		this.receivedDeviceName = true;
		ByteBuf buf = byteBuf.readBytes(DEVICE_NAME_LENGTH);
		ScrcpyDeviceInfoPacket deviceInfoPacket = new ScrcpyDeviceInfoPacket(buf);
		this.callbacks.forEach(callback -> callback.received(deviceInfoPacket));
	}

	private void handleADBForward(ByteBuf byteBuf) {
		if (byteBuf.readableBytes() < 1) {
			return;
		}
		this.adbForward = true;
		byteBuf.skipBytes(1);
	}
}
