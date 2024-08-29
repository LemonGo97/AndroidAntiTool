package com.lemongo97.android.anti.adb.endpoint;

import com.google.gson.Gson;
import com.lemongo97.android.anti.annotation.WebSocketEndpoint;
import com.lemongo97.android.anti.net.adb.ADBClient;
import com.lemongo97.android.anti.net.scrcpy.ScrcpyClient;
import com.lemongo97.android.anti.net.scrcpy.ScrcpyConfig;
import com.lemongo97.android.anti.net.scrcpy.callback.IMediaMessageCallback;
import com.lemongo97.android.anti.net.scrcpy.packet.ScrcpyMediaPacket;
import com.lemongo97.android.anti.services.AbstractWebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import se.vidstige.jadb.JadbDevice;

import java.io.IOException;
import java.util.Collections;
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
		String deviceSerial = params.getFirst(ShellWebSocketEndpoint.Params.DEVICE);
		ADBClient adbClient = new ADBClient();
		adbClient.push(deviceSerial, ResourceUtils.getFile("classpath:" + "binary/scrcpy/scrcpy-server"), "/data/local/tmp/scrcpy-server.jar");
		// TODO 检测本地端口是否占用，若占用，更换可用端口
		adbClient.forward(deviceSerial, "tcp:27183", "localabstract:scrcpy");
		// TODO 检测进程是否已被开启，若被开启，则关掉开启的进程
		// TODO 根据用户的选择，判定是否开启音频流，然后根据判定生成 shell命令 和 ScrcpyClientConfig
		adbClient.exec(deviceSerial, "cd /data/local/tmp && nohup app_process -Djava.class.path=scrcpy-server.jar /data/local/tmp com.genymobile.scrcpy.Server 2.6.1 tunnel_forward=true audio=false control=false > /dev/null &");
		// TODO 将连接scrcpy的相关参数抽取
		// TODO Scrcpy Client应接收ADB Server的Host和Port，而非开启的Scrcpy
		ScrcpyClient scrcpyClient = new ScrcpyClient("127.0.0.1", 27183, ScrcpyConfig.ONLY_VIDEO, Collections.singleton(new ScrcpyMediaMessageCallback(session, this.gson)));
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
			// TODO 检测Android设备scrcpy进程是否仍在运行，若运行，则杀死进程
			// TODO 删除 ADB Forward 规则
		}catch (Exception e){
			log.error("Error closing websocket session", e);
		}finally {
			sessions.remove(sessionId);
			sessionParams.remove(sessionId);
			sessionDeviceMap.remove(sessionId);
			sessionADBStreamClientMap.remove(sessionId);
		}
	}

	static class ScrcpyMediaMessageCallback extends IMediaMessageCallback {

		private final WebSocketSession webSocketSession;
		private final Gson gson;

		ScrcpyMediaMessageCallback(WebSocketSession webSocketSession, Gson gson) {
			this.webSocketSession = webSocketSession;
			this.gson = gson;
		}

		@Override
		public void received(ScrcpyMediaPacket packet) {
			try {
				this.webSocketSession.sendMessage(new TextMessage(gson.toJson(packet)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
