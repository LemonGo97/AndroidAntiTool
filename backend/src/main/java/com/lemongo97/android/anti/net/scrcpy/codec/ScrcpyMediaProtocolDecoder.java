package com.lemongo97.android.anti.net.scrcpy.codec;

import com.lemongo97.android.anti.net.scrcpy.callback.IMediaMessageCallback;
import com.lemongo97.android.anti.net.scrcpy.callback.ScrcpyCallback;
import com.lemongo97.android.anti.net.scrcpy.callback.ScrcpyCallbackType;
import com.lemongo97.android.anti.net.scrcpy.constants.ScrcpySocketType;
import com.lemongo97.android.anti.net.scrcpy.packet.ScrcpyMediaPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ScrcpyMediaProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

	private final List<IMediaMessageCallback> callbacks = new ArrayList<>();

	private final ScrcpySocketType socketType;

	public ScrcpyMediaProtocolDecoder(ScrcpySocketType socketType) {
		this.socketType = socketType;
	}

	public ScrcpyMediaProtocolDecoder(ScrcpySocketType socketType, Collection<ScrcpyCallback<?>> callbacks) {
		this.socketType = socketType;
		this.callbacks.addAll(callbacks.stream()
				.filter(callback -> callback.getType() == ScrcpyCallbackType.MEDIA_MESSAGE)
				.map(callback -> (IMediaMessageCallback)callback)
				.collect(Collectors.toSet()));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		ScrcpyMediaPacket scrcpyMediaPacket = new ScrcpyMediaPacket(in, socketType.getPacketType());
		log.info("ScrcpyMediaPacket Info : {}", scrcpyMediaPacket);
		this.callbacks.forEach(callback -> callback.received(scrcpyMediaPacket));
	}
}
