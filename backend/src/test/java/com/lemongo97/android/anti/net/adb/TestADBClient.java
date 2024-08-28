package com.lemongo97.android.anti.net.adb;

import com.lemongo97.android.anti.net.adb.model.ADBDevice;
import com.lemongo97.android.anti.net.adb.model.ADBForwardConfig;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.List;

public class TestADBClient {
	private static final ADBClient client = new ADBClient();
	private static final String deviceSerial = "127.0.0.1:16384";
	@Test
	public void devices() throws IOException {
		List<ADBDevice> devices = client.devices();
		System.out.println(devices);
	}

	@Test
	public void push() throws IOException {
		client.push(deviceSerial, ResourceUtils.getFile("classpath:" + "binary/scrcpy/scrcpy-server"), "/data/local/tmp/scrcpy-server.jar");
	}

	@Test
	public void forward() throws IOException {
		client.forward(deviceSerial, "tcp:27183", "localabstract:scrcpy");
	}

	@Test
	public void forwardList() throws IOException {
		List<ADBForwardConfig> adbForwardConfigs = client.forwardList(deviceSerial);
		System.out.println(adbForwardConfigs);
	}

	@Test
	public void execString() throws IOException {
		String result = client.execString(deviceSerial, "ps -ef -o PID,ARGS|grep '^ *[0-9]* app_process.*scrcpy.Server'|awk 'NR==1 {print $1}'");
		System.out.println(result);
	}

	@Test
	public void execStringByMultipleLines() throws IOException {
		System.out.println(client.execString(deviceSerial, "ps -ef"));
	}

	@Test
	public void killProcess() throws IOException {
		client.killProcess(deviceSerial, 2826);
	}

	@Test
	public void shell() throws IOException, InterruptedException {
		InteractionShellInput input = new InteractionShellInput(System.out::println);
		client.shell(input);
		int i = 0;
		while (i < 20){
			input.write("ls\n");
			System.out.println("input ls ==> ");
			Thread.sleep(2000);
			i++;
		}
	}
}
