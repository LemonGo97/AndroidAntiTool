package com.lemongo97.android.anti.config;

import com.lemongo97.android.anti.services.AbstractWebSocketEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSocket
public class ApplicationWebSocketConfigurer implements WebSocketConfigurer {

	private final WebSocketProperties webSocketProperties;
	private final List<AbstractWebSocketEndpoint> endpoints;

	public ApplicationWebSocketConfigurer(WebSocketProperties webSocketProperties, List<AbstractWebSocketEndpoint> webSocketEndpoints) {
		this.webSocketProperties = webSocketProperties;
		this.endpoints = webSocketEndpoints;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		String prefix = getURIPrefix();
		for (AbstractWebSocketEndpoint endpoint : endpoints) {
			String[] uris = endpoint.getURI();
			if (uris == null || uris.length == 0) {
				continue;
			}
			if (!StringUtils.hasText(prefix) || "/".equals(prefix)) {
				registry.addHandler(endpoint, uris).setAllowedOrigins("*");
				continue;
			}
			registry.addHandler(endpoint, StringUtils.toStringArray(Arrays.stream(uris).map(uri -> prefix + uri).toList())).setAllowedOrigins("*");
		}
	}

	/**
	 * 前缀如果不以/开始，那么就增加/
	 * 如果前缀以/结尾，那么删除/
	 * @return 处理完成的前缀
	 */
	private String getURIPrefix() {
		String prefix = this.webSocketProperties.getPrefix();
		if (StringUtils.hasText(prefix) && !prefix.startsWith("/")) {
			prefix = "/" + prefix;
			if (prefix.length() > 1 && prefix.endsWith("/")) {
				prefix = prefix.substring(0, prefix.length() - 1);
			}
		}
		return prefix;
	}
}
