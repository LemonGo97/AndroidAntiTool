package com.lemongo97.android.anti.scrcpy;

import io.netty.buffer.ByteBuf;

public record ScrcpyPacket(ScrcpyPacketType type, ByteBuf byteBuf) {
}
