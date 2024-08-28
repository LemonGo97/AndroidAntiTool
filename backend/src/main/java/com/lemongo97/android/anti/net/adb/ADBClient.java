package com.lemongo97.android.anti.net.adb;

import com.lemongo97.android.anti.net.adb.model.ADBDevice;
import com.lemongo97.android.anti.net.adb.model.ADBForwardConfig;
import com.lemongo97.android.anti.net.adb.transport.SyncTransport;
import com.lemongo97.android.anti.net.adb.transport.Transport;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executor;

@Data
public class ADBClient {

	private static final int DEFAULT_FILE_MODE = 0664;

	private final String host;
	private final int port;
	private final Executor executor;

	public ADBClient(String host, int port, Executor executor) {
		this.host = host;
		this.port = port;
		this.executor = executor;
	}

	public ADBClient(String host, int port) {
		this(host, port, ConnectionManager.getThreadPool());
	}

	public ADBClient(Executor executor){
		this(ConnectionManager.DEFAULT_HOST, ConnectionManager.DEFAULT_PORT, executor);
	}

	public ADBClient(){
		this(ConnectionManager.getThreadPool());
	}

	/**
	 * 列出连接到ADB Server上的设备: adb devices
	 * @return 设备列表
	 * @throws IOException IO异常
	 */
	public List<ADBDevice> devices() throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		try(Transport transport = ConnectionManager.getTransport(socket)){
			transport.send("host:devices");
			transport.verifyResponse();
			return ADBDevice.parse(transport.readString());
		}
	}

	/**
	 * 推送文件: adb push [local-file-path] [remote-file-path]
	 * @param file 本地文件
	 * @param destFilePath 目标文件路径
	 * @throws IOException IO异常
	 */
	public void push(File file, String destFilePath) throws IOException {
		this.push(null, file, destFilePath, DEFAULT_FILE_MODE);
	}

	/**
	 * 推送文件: adb push [local-file-path] [remote-file-path]
	 * @param file 本地文件
	 * @param destFilePath 目标文件路径
	 * @param fileMode 文件权限
	 * @throws IOException IO异常
	 */
	public void push(File file, String destFilePath, int fileMode) throws IOException {
		this.push(null, file, destFilePath, fileMode);
	}

	/**
	 * 推送文件: adb -s device push [local-file-path] [remote-file-path]
	 * @param serial 设备
	 * @param file 本地文件
	 * @param destFilePath 目标文件路径
	 * @throws IOException IO异常
	 */
	public void push(String serial, File file, String destFilePath) throws IOException {
		this.push(serial, file, destFilePath, DEFAULT_FILE_MODE);
	}

	/**
	 * 推送文件: adb -s device push [local-file-path] [remote-file-path]
	 * @param serial 设备
	 * @param file 本地文件
	 * @param destFilePath 目标文件路径
	 * @param fileMode 文件权限
	 * @throws IOException IO异常
	 */
	public void push(String serial, File file, String destFilePath, int fileMode) throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		try (FileInputStream fileStream = new FileInputStream(file);
			 Transport transport = ConnectionManager.getTransport(socket).startTransport(serial)) {
			SyncTransport sync = transport.startSync();
			sync.send("SEND", destFilePath + "," + fileMode);

			sync.sendStream(fileStream);

			sync.sendStatus("DONE", Long.valueOf(file.lastModified()).intValue());
			sync.verifyStatus();
		}
	}

	/**
	 * ADB forward: adb forward [local-address] [device-address]
	 * @param localAddress 本地地址
	 * @param targetAddress Android设备地址
	 * @throws IOException IO异常
	 */
	public void forward(String localAddress, String targetAddress) throws IOException {
		this.forward(null, localAddress, targetAddress);
	}

	/**
	 * ADB forward: adb -s device forward [local-address] [device-address]
	 * @param serial 设备
	 * @param localAddress 本地地址
	 * @param targetAddress Android设备地址
	 * @throws IOException IO异常
	 */
	public void forward(String serial, String localAddress, String targetAddress) throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		try (Transport transport = ConnectionManager.getTransport(socket).startTransport(serial)) {
			transport.send(String.format("host:forward:%s;%s", localAddress, targetAddress));
			transport.verifyResponse();
			transport.verifyResponse();
		}
	}


	/**
	 * 列出 ADB Forward: adb forward --list
	 * @return adb forward 列表
	 * @throws IOException IO异常
	 */
	public List<ADBForwardConfig> forwardList() throws IOException {
		return this.forwardList(null);
	}

	/**
	 * 列出 ADB Forward: adb -s device forward --list
	 * @param serial 设备
	 * @return adb forward 列表
	 * @throws IOException IO异常
	 */
	public List<ADBForwardConfig> forwardList(String serial) throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		try (Transport transport = ConnectionManager.getTransport(socket).startTransport(serial)) {
			transport.send("host:list-forward");
			transport.verifyResponse();
			return ADBForwardConfig.parse(transport.readString());
		}
	}

	/**
	 * 移除 ADB Forward: adb forward --remove [local-address]
	 * @param localAddress 本地地址
	 * @throws IOException IO异常
	 */
	public void forwardRemove(String localAddress) throws IOException {
		this.forwardRemove(null, localAddress);
	}

	/**
	 * 移除 ADB Forward: adb -s device forward --remove [local-address]
	 * @param serial 设备
	 * @param localAddress 本地地址
	 * @throws IOException IO异常
	 */
	public void forwardRemove(String serial, String localAddress) throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		try (Transport transport = ConnectionManager.getTransport(socket).startTransport(serial)) {
			transport.send(String.format("host:killforward:%s", localAddress));
			transport.verifyResponse();
			transport.verifyResponse();
		}
	}

	/**
	 * 打开ADB Shell: adb shell
	 * @throws IOException IO异常
	 * todo 验证是否能够完成
	 */
	public void shell(InteractionShellInput input) throws IOException {
		this.shell(null, input);
	}

	/**
	 * 打开ADB Shell: adb shell
	 * @param serial 设备
	 * @throws IOException IO异常
	 * todo 验证是否能够完成
	 */
	public void shell(String serial, InteractionShellInput input) throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		Transport transport = ConnectionManager.getTransport(socket).startTransport(serial);
		transport.send("shell:");
		transport.verifyResponse();
		input.setTransport(transport);
		this.executor.execute(input);
	}

	/**
	 * 执行shell命令: adb shell command
	 * @param command 命令
	 * @throws IOException IO异常
	 */
	public void shell(String command) throws IOException {
		this.shell(null, command);
	}

	/**
	 * 执行shell命令: adb shell command
	 * @param serial 设备
	 * @param command 命令
	 * @throws IOException IO异常
	 */
	public void shell(String serial, String command) throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		try (Transport transport = ConnectionManager.getTransport(socket).startTransport(serial)) {
			transport.send("shell:" + command);
			transport.verifyResponse();
		}
	}

	/**
	 * 执行shell命令: adb shell command
	 * @param command 命令
	 * @return InputStream
	 * @throws IOException IO异常
	 */
	public InputStream shellStream(String command) throws IOException {
		return this.shellStream(null, command);
	}

	/**
	 * 执行shell命令: adb shell command
	 * @param serial 设备
	 * @param command 执行的命令
	 * @return InputStream
	 * @throws IOException IO异常
	 */
	@SuppressWarnings("resource")
	public InputStream shellStream(String serial, String command) throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		Transport transport = ConnectionManager.getTransport(socket).startTransport(serial);
		transport.send("shell:" + command);
		transport.verifyResponse();
		return new ADBFilterInputStream(new BufferedInputStream(transport.getInputStream()));
	}

	/**
	 * 执行命令
	 * @param command 执行的命令
	 * @throws IOException IO异常
	 */
	public void exec(String command)throws IOException {
		this.exec(null, command);
	}

	/**
	 * 执行命令
	 * @param serial 设备
	 * @param command 执行的命令
	 * @throws IOException IO异常
	 */
	public void exec(String serial, String command)throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		try (Transport transport = ConnectionManager.getTransport(socket).startTransport(serial)) {
			transport.send("exec:" + command);
			transport.verifyResponse();
		}
	}

	/**
	 * 执行命令
	 * @param command 执行的命令
	 * @return 执行结果
	 * @throws IOException IO异常
	 */
	public String execString(String command)throws IOException {
		return this.execString(null, command);
	}

	/**
	 * 执行命令
	 * @param serial 设备
	 * @param command 执行的命令
	 * @return 执行结果
	 * @throws IOException IO异常
	 */
	public String execString(String serial, String command)throws IOException {
		Socket socket = ConnectionManager.getConnection(this.host, this.port);
		try (Transport transport = ConnectionManager.getTransport(socket).startTransport(serial)) {
			transport.send("exec:" + command);
			transport.verifyResponse();
			return transport.readUnknownLengthStringMultiLine();
		}
	}

	/**
	 * 杀死进程(非强制): adb shell kill pid
	 * @param pid 设备进程PID
	 * @throws IOException IO异常
	 */
	public void killProcess(int pid) throws IOException {
		this.killProcess(null, pid);
	}

	/**
	 * 杀死进程(非强制): adb -s device shell kill pid
	 * @param serial 设备
	 * @param pid 设备进程PID
	 * @throws IOException IO异常
	 */
	public void killProcess(String serial, int pid) throws IOException {
		String str = this.execString(serial, "kill " + pid);
		if (StringUtils.hasText(str)){
			throw new IOException(str);
		}
	}

	/**
	 * 杀死进程(强制): adb shell kill -9 pid
	 * @param pid 设备进程PID
	 * @throws IOException IO异常
	 */
	public void killProcessEnforce(int pid) throws IOException {
		this.killProcessEnforce(null, pid);
	}

	/**
	 * 杀死进程(强制): adb -s device shell kill -9 pid
	 * @param serial 设备
	 * @param pid 设备进程PID
	 * @throws IOException IO异常
	 */
	public void killProcessEnforce(String serial, int pid) throws IOException {
		String str = this.execString(serial, "kill -9 " + pid);
		if (StringUtils.hasText(str)){
			throw new IOException(str);
		}
	}
}
