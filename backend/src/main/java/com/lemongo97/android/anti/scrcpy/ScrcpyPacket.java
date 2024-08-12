package com.lemongo97.android.anti.scrcpy;

import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;
import io.netty.buffer.ByteBuf;

public record ScrcpyPacket(ScrcpyPacketType type, ByteBuf byteBuf) {
}
