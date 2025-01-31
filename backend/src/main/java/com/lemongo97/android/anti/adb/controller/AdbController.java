package com.lemongo97.android.anti.adb.controller;

import com.lemongo97.android.anti.adb.model.AndroidDevice;
import com.lemongo97.android.anti.adb.service.ADBService;
import com.lemongo97.android.anti.services.SseEmitterService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
public class AdbController {
	private final ADBService adbService;
	private final JadbConnection jadbConnection;
	private final SseEmitterService sseEmitterService;

	public AdbController(ADBService adbService, JadbConnection jadbConnection,
						 SseEmitterService sseEmitterService) {
		this.adbService = adbService;
		this.jadbConnection = jadbConnection;
		this.sseEmitterService = sseEmitterService;
	}

	@PostMapping("/connect")
	public void connect(@NotBlank(message = "设备IP不能为空") String host, Integer port) {
		this.adbService.connect(host, Optional.ofNullable(port).orElse(5555));
	}

	@GetMapping("/devices")
	public Collection<AndroidDevice> devices() {
		return this.adbService.devices();
	}

	@GetMapping(path = "/logcat", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
	public SseEmitter logcat(String serial) throws IOException, JadbException {
		List<JadbDevice> devices = this.jadbConnection.getDevices();
		JadbDevice device;
		if (StringUtils.hasText(serial)) {
			device = devices.stream().filter(dev -> dev.getSerial().equals(serial)).findFirst().orElse(null);
		} else {
			device = devices.stream().findFirst().orElse(null);
		}
		if (device != null) {
			SseEmitter emitter = this.sseEmitterService.newNoTimeoutSseEmitter();
			new Thread(() -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(device.execute("logcat -c && logcat")))) {
					String line;
					while ((line = reader.readLine()) != null) {
						emitter.send(line);
					}
				} catch (IOException|JadbException e) {
					// 处理异常
					log.error("logcat 数据传输错误！", e);
				} finally {
					emitter.complete();
				}
			}).start();
			return emitter;
		} else {
			throw new RuntimeException("未找到任何连接的Android设备！");
		}
	}

}
