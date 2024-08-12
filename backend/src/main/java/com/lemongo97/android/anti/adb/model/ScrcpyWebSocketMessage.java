package com.lemongo97.android.anti.adb.model;

import com.lemongo97.android.anti.scrcpy.constants.ScrcpyPacketType;

public record ScrcpyWebSocketMessage(ScrcpyPacketType type, String message) {
}
