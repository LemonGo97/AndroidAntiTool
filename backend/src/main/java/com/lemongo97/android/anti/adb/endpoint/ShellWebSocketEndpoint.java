package com.lemongo97.android.anti.adb.endpoint;

import com.lemongo97.android.anti.annotation.WebSocketEndpoint;
import com.lemongo97.android.anti.services.AbstractWebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@WebSocketEndpoint("/shell")
public class ShellWebSocketEndpoint extends AbstractWebSocketEndpoint {


	@Override
	public void onTextMessage(WebSocketSession session, TextMessage message) {
		// todo 完成Shell
		log.info("onTextMessage: {}", message.getPayload());
	}

	@Override
	public void onBinaryMessage(WebSocketSession session, BinaryMessage message) {
		// todo 完成Shell
		log.info("onBinaryMessage: {}", message.getPayload());
	}

}
