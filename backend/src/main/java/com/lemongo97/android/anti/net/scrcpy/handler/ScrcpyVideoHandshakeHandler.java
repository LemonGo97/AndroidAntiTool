package com.lemongo97.android.anti.net.scrcpy.handler;

import com.lemongo97.android.anti.net.scrcpy.callback.IAudioMetadataMessageCallback;
import com.lemongo97.android.anti.net.scrcpy.callback.IVideoMetadataMessageCallback;
import com.lemongo97.android.anti.net.scrcpy.callback.ScrcpyCallback;
import com.lemongo97.android.anti.net.scrcpy.callback.ScrcpyCallbackType;
import com.lemongo97.android.anti.net.scrcpy.codec.ScrcpyMediaProtocolDecoder;
import com.lemongo97.android.anti.net.scrcpy.constants.ScrcpySocketType;
import com.lemongo97.android.anti.net.scrcpy.packet.ScrcpyVideoMetaDataPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.lemongo97.android.anti.net.scrcpy.constants.ScrcpyConstants.VIDEO_HEADER_LENGTH;


@Slf4j
public class ScrcpyVideoHandshakeHandler extends ByteToMessageDecoder {
	private final Collection<ScrcpyCallback<?>> allCallbacks;
	private final List<IVideoMetadataMessageCallback> callbacks = new ArrayList<>();

	public ScrcpyVideoHandshakeHandler(Collection<ScrcpyCallback<?>> callbacks) {
		this.allCallbacks = callbacks;
		this.callbacks.addAll(callbacks.stream()
				.filter(callback -> callback.getType() == ScrcpyCallbackType.VIDEO_METADATA)
				.map(callback -> (IVideoMetadataMessageCallback)callback)
				.collect(Collectors.toSet()));
	}

    @Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < VIDEO_HEADER_LENGTH){
			return;
		}
		log.info("pipeline ==> : {}", ctx.pipeline().names());
		ByteBuf byteBuf = in.readBytes(VIDEO_HEADER_LENGTH);
		ScrcpyVideoMetaDataPacket videoMetaDataPacket = new ScrcpyVideoMetaDataPacket(byteBuf);
		this.callbacks.forEach(callback -> callback.received(videoMetaDataPacket));
		ctx.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8, 4));
		ctx.pipeline().addLast(new ScrcpyMediaProtocolDecoder(ScrcpySocketType.VIDEO, this.allCallbacks));
		ctx.pipeline().remove(this);
	}
}
