package com.lemongo97.android.anti.adb.endpoint;

import com.google.gson.Gson;
import com.lemongo97.android.anti.annotation.WebSocketEndpoint;
import com.lemongo97.android.anti.scrcpy.ScrcpyClient;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpyMode;
import com.lemongo97.android.anti.scrcpy.constants.ScrcpySocketType;
import com.lemongo97.android.anti.services.AbstractWebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import se.vidstige.jadb.JadbDevice;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@WebSocketEndpoint("/scrcpy")
public class ScrapyWebSocketEndpoint extends AbstractWebSocketEndpoint {
	private final Gson gson;
	/**
	 * <p>k -> sessionId
	 * <p>v -> WebSocketSession
	 */
	private final static ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	/**
	 * <p>k -> sessionId
	 * <p>v -> 连接时请求参数
	 */
	private final static ConcurrentHashMap<String, MultiValueMap<String, String>> sessionParams = new ConcurrentHashMap<>();

	/**
	 * <p>k -> sessionId
	 * <p>v -> 连接的设备
	 */
	private final static ConcurrentHashMap<String, JadbDevice> sessionDeviceMap = new ConcurrentHashMap<>();
	private final static ConcurrentHashMap<String, ScrcpyClient> sessionADBStreamClientMap = new ConcurrentHashMap<>();

	public ScrapyWebSocketEndpoint(Gson gson) {
		this.gson = gson;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String sessionId = session.getId();
		sessions.put(sessionId, session);
		UriComponents uriComponents = UriComponentsBuilder.fromUri(session.getUri()).build();
		MultiValueMap<String, String> params = uriComponents.getQueryParams();
		sessionParams.put(sessionId, params);




		// TODO 连接scrcpy
		ScrcpyClient scrcpyClient = new ScrcpyClient(ScrcpySocketType.VIDEO, ScrcpyMode.forward, session, this.gson);
		sessionADBStreamClientMap.put(sessionId, scrcpyClient);
		scrcpyClient.connect();
	}

	@Override
	public void onTextMessage(WebSocketSession session, TextMessage message) {
		// todo 完成屏幕展示
		log.info("onTextMessage: {}", message.getPayload());
	}

	@Override
	public void onBinaryMessage(WebSocketSession session, BinaryMessage message) {
		// todo 完成屏幕展示
		log.info("onBinaryMessage: {}", message.getPayload());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		this.closeSession(session, status);
	}

	private void closeSession(WebSocketSession session) throws IOException {
		this.closeSession(session, null);
	}

	private void closeSession(WebSocketSession session, CloseStatus status) {
		String sessionId = session.getId();
		try{
			session.close(Optional.ofNullable(status).orElse(CloseStatus.NOT_ACCEPTABLE));
			ScrcpyClient scrcpyClient = sessionADBStreamClientMap.get(sessionId);
			scrcpyClient.disconnect();
		}catch (Exception e){
			log.error("Error closing websocket session", e);
		}finally {
			sessions.remove(sessionId);
			sessionParams.remove(sessionId);
			sessionDeviceMap.remove(sessionId);
			sessionADBStreamClientMap.remove(sessionId);
		}
	}
}
