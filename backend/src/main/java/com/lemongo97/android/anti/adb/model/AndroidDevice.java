package com.lemongo97.android.anti.adb.model;

import se.vidstige.jadb.JadbDevice;

public record AndroidDevice(String serial, JadbDevice.State state) {}
