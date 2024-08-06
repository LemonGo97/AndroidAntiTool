package com.lemongo97.android.anti.stream;

import io.netty.util.AttributeKey;

public interface ADBStreamConstants {
	AttributeKey<Boolean> IS_FIRST_OK= AttributeKey.valueOf("IS_FIRST_OK");
	AttributeKey<Boolean> IS_SECOND_OK = AttributeKey.valueOf("IS_SECOND_OK");
	AttributeKey<Boolean> IS_THIRD_OK = AttributeKey.valueOf("IS_THIRD_OK");
}
