package se.vidstige.jadb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ADBTransport {
	private final Transport transport;
	private final JadbDevice device;

	public ADBTransport(Transport transport, JadbDevice device) throws IOException {
		this.transport = transport;
		this.device = device;
	}

	public String readString() throws IOException {
		return this.transport.readString();
	}

	public void readResponseTo(OutputStream output) throws IOException {
		this.transport.readResponseTo(output);
	}

	public InputStream getInputStream() {
		return this.transport.getInputStream();
	}

	public void verifyResponse() throws IOException, JadbException {
		this.transport.verifyResponse();
	}

	public String readString(int length) throws IOException {
		return this.transport.readString(length);
	}

	public void send(String command) throws IOException {
		this.transport.send(command);
	}

	public SyncTransport startSync() throws IOException, JadbException {
		return this.transport.startSync();
	}

	public void connect() throws IOException {
		// todo 发送设备连接
		this.send(this.device.getSerial() == null ? "host:transport-any" : "host:transport:" + this.device.getSerial());
	}

	public void close() throws IOException {
		this.transport.close();
	}
}
