package com.lemongo97.android.anti.net.adb;

import com.lemongo97.android.anti.net.adb.transport.Transport;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("LombokGetterMayBeUsed")
public class ConnectionManager {

	final static String DEFAULT_HOST = "127.0.0.1";
	final static int DEFAULT_PORT = 5037;
	final static ExecutorService threadPool = Executors.newFixedThreadPool(5);


	public static Socket getConnection(String host, int port) throws IOException {
		return new Socket(host, port);
	}

	public static Socket getConnection() throws IOException {
		return getConnection(DEFAULT_HOST, DEFAULT_PORT);
	}

	public static Transport getTransport(String host, int port) throws IOException {
		return new Transport(getConnection(host, port));
	}

	public static Transport getTransport(Socket socket) throws IOException {
		return new Transport(socket);
	}

	public static Transport getTransport() throws IOException {
		return new Transport(getConnection());
	}

	public static Executor getThreadPool() {
		return threadPool;
	}

}
