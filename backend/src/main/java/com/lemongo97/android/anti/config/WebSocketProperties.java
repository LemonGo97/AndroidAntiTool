package com.lemongo97.android.anti.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.websocket")
public class WebSocketProperties {
	private String prefix = "/";
}
