package com.lemongo97.android.anti.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lemongo97.android.anti.config.gson.serializer.ByteBufSerializer;
import io.netty.buffer.ByteBuf;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.vidstige.jadb.JadbConnection;

@Configuration
@EnableConfigurationProperties(WebSocketProperties.class)
public class ApplicationConfiguration {

	@Bean
	public JadbConnection jadbConnection(){
		return new JadbConnection();
	}

	@Bean
	public Gson gson(){
		return new GsonBuilder()
				.disableHtmlEscaping()
				.registerTypeAdapter(ByteBuf.class, new ByteBufSerializer())
				.create();
	}
}
