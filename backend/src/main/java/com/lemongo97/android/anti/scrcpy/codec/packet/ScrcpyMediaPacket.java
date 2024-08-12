package com.lemongo97.android.anti.scrcpy.codec.packet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.lemongo97.android.anti.config.gson.serializer.ByteBufSerializer;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType.VIDEO_FRAME;

@Data
@AllArgsConstructor
public class ScrcpyMediaPacket implements ScrcpyPacket {

	private long ptsAndFlags;
	private int length;
	@JsonAdapter(ByteBufSerializer.class)
	private ByteBuf frame;
	private boolean configPacket;
	private boolean keyFramePacket;
	private Long pts;

	public ScrcpyMediaPacket(ByteBuf buf) {
		this.ptsAndFlags = buf.readLong();
		this.length = buf.readInt();
		this.frame = buf.readBytes(this.length);
		this.configPacket = (this.ptsAndFlags & PACKET_FLAG_CONFIG)  != 0;
		this.keyFramePacket = (this.ptsAndFlags & PACKET_FLAG_KEY_FRAME)  != 0;
		if (this.configPacket){
			return;
		}
		this.pts = this.ptsAndFlags & PACKET_PTS_MASK;
	}


	public byte[] getFrameBytes() {
		return frame.array();
	}

	@Override
	public ScrcpyPacketType getPacketType() {
		return VIDEO_FRAME;
	}
}
