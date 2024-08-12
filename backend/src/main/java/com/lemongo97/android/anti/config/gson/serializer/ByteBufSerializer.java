package com.lemongo97.android.anti.config.gson.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.ReferenceCountUtil;
import org.dromara.hutool.core.codec.binary.Base64;

import java.lang.reflect.Type;

public class ByteBufSerializer implements JsonSerializer<ByteBuf> {
	@Override
	public JsonElement serialize(ByteBuf src, Type typeOfSrc, JsonSerializationContext context) {
		byte[] bytes = ByteBufUtil.getBytes(src);
		ReferenceCountUtil.release(src);
		return context.serialize(Base64.encode(bytes));
	}
}
