package com.lemongo97.android.anti.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SseEmitterService {

	private final List<SseEmitter> sseEmitterList = new ArrayList<>();

	public SseEmitter newSseEmitter(long timeout){
		SseEmitter sseEmitter = new SseEmitter(timeout);
		sseEmitter.onError(throwable -> {
			sseEmitterList.remove(sseEmitter);
			log.error("SSE 连接出现错误！", throwable);
		});
		sseEmitter.onTimeout(() -> {
			sseEmitterList.remove(sseEmitter);
			log.warn("SSE 连接超时！");
		});
		sseEmitter.onCompletion(() -> {
			log.info("SSE 连接已完成...");
		});
		this.sseEmitterList.add(sseEmitter);
		return sseEmitter;
	}

	public SseEmitter newNoTimeoutSseEmitter(){
		return this.newSseEmitter(0);
	}

}
