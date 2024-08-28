package se.vidstige.jadb;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class TestJadb {

	private final JadbConnection jadbConnection = new JadbConnection();

	@Test
	public void testHostCommand() throws IOException, JadbException {
		List<JadbDevice> devices = jadbConnection.getDevices();
	}

	@Test
	public void testTransportCommand() throws IOException, JadbException {
		jadbConnection.getAnyDevice().execute("ps");
	}

	@Test
	public void testStartScrcpy() throws IOException, JadbException {
		List<JadbDevice> devices = jadbConnection.getDevices();
		JadbDevice jadbDevice = devices.getFirst();
		jadbDevice.push(ResourceUtils.getFile("classpath:" + "binary/scrcpy/scrcpy-server"), new RemoteFile("/data/local/tmp/scrcpy-server.jar"));
		Transport transport = jadbConnection.createTransport();
		this.send(transport, "host:transport:" + jadbDevice.getSerial() );
		this.send(transport, "host:forward:tcp:27183;localabstract:scrcpy");
		jadbDevice.executeShell("cd /data/local/tmp && nohup app_process -Djava.class.path=scrcpy-server.jar  /data/local/tmp com.genymobile.scrcpy.Server 2.6.1 tunnel_forward=true audio=false control=false > /dev/null &");
		InputStream inputStream = jadbDevice.execute("ps -ef -o PID,ARGS|grep '^ *[0-9]* app_process.*scrcpy.Server'|awk 'NR==1 {print $1}'");
	}

	private void send(Transport transport, String command) throws IOException, JadbException {
		transport.send(command);
		transport.verifyResponse();
	}
}
