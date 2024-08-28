package com.lemongo97.android.anti.net.adb.transport;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("LombokGetterMayBeUsed")
public class Transport implements Closeable {

	private final Socket socket;
	private final OutputStream outputStream;
	private final InputStream inputStream;
	private final DataInputStream dataInput;
	private final DataOutputStream dataOutput;

	public Transport(Socket socket) throws IOException {
		this.socket = socket;
		this.outputStream = socket.getOutputStream();
		this.inputStream = socket.getInputStream();
		this.dataInput = new DataInputStream(inputStream);
		this.dataOutput = new DataOutputStream(outputStream);
	}

	public String readString() throws IOException {
		String encodedLength = readString(4);
		int length = Integer.parseInt(encodedLength, 16);
		return readString(length);
	}

	public void readResponseTo(OutputStream output) throws IOException {
		byte[] buffer = new byte[1024 * 10];
		int len;
		while ((len = inputStream.read(buffer)) != -1) {
			output.write(buffer, 0, len);
		}
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void verifyResponse() throws IOException, RuntimeException {
		String response = readString(4);
		if (!"OKAY".equals(response)) {
			String error = readString();
			throw new RuntimeException("command failed: " + error);
		}
	}

	public String readString(int length) throws IOException {
		byte[] responseBuffer = new byte[length];
		dataInput.readFully(responseBuffer);
		return new String(responseBuffer, StandardCharsets.UTF_8);
	}

	public String readUnknownLengthStringMultiLine() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataInput))) {
			String line;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			return stringBuilder.toString().trim();
		}
	}

	private String getCommandLength(String command) {
		return String.format("%04x", command.getBytes().length);
	}

	public void send(String command) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
		writer.write(getCommandLength(command));
		writer.write(command);
		writer.flush();
	}

	public OutputStreamWriter getOutputStreamWriter(){
		return new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
	}

	public SyncTransport startSync() throws IOException, RuntimeException {
		send("sync:");
		verifyResponse();
		return new SyncTransport(dataOutput, dataInput);
	}

	public Transport startTransport(String serial) throws IOException {
		try {
			this.send(serial == null ? "host:transport-any" : "host:transport:" + serial);
			this.verifyResponse();
			return this;
		} catch (IOException e) {
			this.close();
			throw e;
		}
	}

	// 貌似为简写方式的transport
	public Transport startTPort(String serial) throws IOException {
		try {
			this.send("host:tport:serial:" + serial);
			this.verifyStartTPortResponse();
			return this;
		} catch (IOException e) {
			this.close();
			throw e;
		}
	}

	// 为简写方式的transport验证Response
	public void verifyStartTPortResponse() throws IOException, RuntimeException {
		this.verifyResponse();
		dataInput.skipBytes(8);
	}


	@Override
	public void close() throws IOException {
		dataInput.close();
		dataOutput.close();
		socket.close();
	}
}
