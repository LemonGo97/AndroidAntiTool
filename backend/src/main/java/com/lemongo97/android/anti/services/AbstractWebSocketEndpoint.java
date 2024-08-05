package com.lemongo97.android.anti.services;

import com.lemongo97.android.anti.annotation.WebSocketEndpoint;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public abstract class AbstractWebSocketEndpoint extends AbstractWebSocketHandler {

	@Override
	final protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		this.onTextMessage(session, message);
	}

	@Override
	final protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
		this.onBinaryMessage(session, message);
	}

	public abstract void onTextMessage(WebSocketSession session, TextMessage message);
	public abstract void onBinaryMessage(WebSocketSession session, BinaryMessage message);

	final public String[] getURI() {
		WebSocketEndpoint socketEndpointAnno = this.getClass().getAnnotation(WebSocketEndpoint.class);
		return socketEndpointAnno.value();
	}
}
