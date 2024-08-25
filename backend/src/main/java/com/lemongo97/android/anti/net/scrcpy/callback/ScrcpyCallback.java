package com.lemongo97.android.anti.net.scrcpy.callback;

public interface ScrcpyCallback<T> {
	ScrcpyCallbackType getType();

	void received(T packet);
}
