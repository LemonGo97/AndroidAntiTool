package com.lemongo97.android.anti.adb.service;

import com.lemongo97.android.anti.adb.exception.AdbServerConnectException;
import com.lemongo97.android.anti.adb.model.AndroidDevice;
import com.lemongo97.android.anti.utils.ExceptionUtils;
import org.springframework.stereotype.Service;
import se.vidstige.jadb.JadbConnection;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ADBService {

	private final JadbConnection jadbConnection;

	public ADBService(JadbConnection jadbConnection) {
		this.jadbConnection = jadbConnection;
	}

	public Collection<AndroidDevice> devices() {
		return ExceptionUtils.ignoreExceptionThrowNew(() -> this.jadbConnection.getDevices().stream()
				.map(jadbDevice ->
						new AndroidDevice(
								jadbDevice.getSerial(),
								ExceptionUtils.ignoreExceptionReturnNull(jadbDevice::getState)))
				.collect(Collectors.toList()), new AdbServerConnectException());
	}

	public void connect(String host, int port) {
		ExceptionUtils.ignoreExceptionVoidThrowNew(() ->
				this.jadbConnection.connectToTcpDevice(new InetSocketAddress(host, port)),
				new AdbServerConnectException("Android设备连接失败！"));
	}

}
