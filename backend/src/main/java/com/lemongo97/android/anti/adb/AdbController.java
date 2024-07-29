package com.lemongo97.android.anti.adb;

import com.lemongo97.android.anti.services.SseEmitterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import se.vidstige.jadb.ConnectionToRemoteDeviceException;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
@RestController
public class AdbController {
	private final JadbConnection jadbConnection;
	private final SseEmitterService sseEmitterService;

	public AdbController(JadbConnection jadbConnection,
						 SseEmitterService sseEmitterService) {
		this.jadbConnection = jadbConnection;
		this.sseEmitterService = sseEmitterService;
	}

	@PostMapping("/connect")
	public void connect(String host, int port) throws ConnectionToRemoteDeviceException, IOException, JadbException {
		this.jadbConnection.connectToTcpDevice(new InetSocketAddress(host, port));
	}

	@GetMapping("/devices")
	public List<JadbDevice> devices() throws IOException, JadbException {
		return this.jadbConnection.getDevices();
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
