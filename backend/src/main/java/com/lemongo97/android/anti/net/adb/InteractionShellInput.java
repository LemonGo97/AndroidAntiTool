package com.lemongo97.android.anti.net.adb;

import com.lemongo97.android.anti.net.adb.transport.Transport;

import java.io.*;

public class InteractionShellInput implements Runnable, Closeable {

	private Transport transport;
	private OutputStreamWriter writer;
	private final InteractionShellLineHandler handler;

	public InteractionShellInput(InteractionShellLineHandler handler) {
		this.handler = handler;
	}

	public void write(String str) throws IOException {
		writer.write(str);
		writer.flush();
	}

	void setTransport(Transport transport){
		this.transport = transport;
		this.writer = transport.getOutputStreamWriter();
	}

	@Override
	public void close() throws IOException {
		this.transport.close();
		this.writer.close();
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.transport.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				this.handler.received(line + "\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
