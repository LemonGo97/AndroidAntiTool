package com.lemongo97.android.anti.net.scrcpy.packet;


import com.lemongo97.android.anti.net.scrcpy.constants.ScrcpyPacketType;

public interface ScrcpyPacket {
	long PACKET_FLAG_CONFIG = 1L << 63;
	long PACKET_FLAG_KEY_FRAME = 1L << 62;
	long PACKET_PTS_MASK = PACKET_FLAG_KEY_FRAME - 1;

	ScrcpyPacketType getPacketType();
}
