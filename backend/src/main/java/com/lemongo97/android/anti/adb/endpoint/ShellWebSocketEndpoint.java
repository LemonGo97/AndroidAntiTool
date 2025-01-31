package com.lemongo97.android.anti.adb.endpoint;

import com.lemongo97.android.anti.adb.client.stream.ADBStreamClient;
import com.lemongo97.android.anti.annotation.WebSocketEndpoint;
import com.lemongo97.android.anti.services.AbstractWebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@WebSocketEndpoint("/shell")
public class ShellWebSocketEndpoint extends AbstractWebSocketEndpoint {

	private final JadbConnection jadbConnection;

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
	private final static ConcurrentHashMap<String, ADBStreamClient> sessionADBStreamClientMap = new ConcurrentHashMap<>();

	public ShellWebSocketEndpoint(JadbConnection jadbConnection) {
		this.jadbConnection = jadbConnection;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String sessionId = session.getId();
		sessions.put(sessionId, session);
		UriComponents uriComponents = UriComponentsBuilder.fromUri(session.getUri()).build();
		MultiValueMap<String, String> params = uriComponents.getQueryParams();
		sessionParams.put(sessionId, params);
		String deviceSerial = params.getFirst(Params.DEVICE);
		if (!StringUtils.hasText(deviceSerial)) {
			this.closeSession(session);
			return;
		}
		Optional<JadbDevice> jadbDeviceOptional = this.jadbConnection.getDevices()
				.stream()
				.filter(device -> device.getSerial().equals(deviceSerial))
				.findFirst();
		if (jadbDeviceOptional.isEmpty()) {
			this.closeSession(session);
			return;
		}
		JadbDevice jadbDevice = jadbDeviceOptional.get();
		sessionDeviceMap.put(sessionId, jadbDevice);
		log.info("Connecting to device {}", deviceSerial);

		// 连接真正的adb shell
		ADBStreamClient adbStreamClient = new ADBStreamClient(session, deviceSerial);
		sessionADBStreamClientMap.put(sessionId, adbStreamClient);
		adbStreamClient.connect();
		log.info("Connected device {}", deviceSerial);
	}

	@Override
	public void onTextMessage(WebSocketSession session, TextMessage message) {
		// todo 完成Shell
		log.debug("onTextMessage: {}", message.getPayload());
		String sessionId = session.getId();
		ADBStreamClient adbStreamClient = sessionADBStreamClientMap.get(sessionId);
		adbStreamClient.send(message.getPayload());
	}

	@Override
	public void onBinaryMessage(WebSocketSession session, BinaryMessage message) {
		// todo 完成Shell
		log.debug("onBinaryMessage: {}", message.getPayload());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String sessionId = session.getId();
		JadbDevice jadbDevice = sessionDeviceMap.get(sessionId);
		this.closeSession(session, status);
		// todo 关闭与设备的连接进程
		log.info("Disconnecting from device {}", jadbDevice.getSerial());
	}

	private void closeSession(WebSocketSession session) throws IOException {
		this.closeSession(session, null);
	}

	private void closeSession(WebSocketSession session, CloseStatus status) {
		String sessionId = session.getId();
		try{
			session.close(Optional.ofNullable(status).orElse(CloseStatus.NOT_ACCEPTABLE));
			ADBStreamClient adbStreamClient = sessionADBStreamClientMap.get(sessionId);
			adbStreamClient.disconnect();
		}catch (Exception e){
			log.error("Error closing websocket session", e);
		}finally {
			sessions.remove(sessionId);
			sessionParams.remove(sessionId);
			sessionDeviceMap.remove(sessionId);
			sessionADBStreamClientMap.remove(sessionId);
		}
	}

	interface Params{
		String DEVICE = "device";
	}
}
