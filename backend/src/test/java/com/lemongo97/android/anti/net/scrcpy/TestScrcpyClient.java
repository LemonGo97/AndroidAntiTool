package com.lemongo97.android.anti.net.scrcpy;

import com.lemongo97.android.anti.net.scrcpy.ScrcpyClient;
import com.lemongo97.android.anti.net.scrcpy.ScrcpyConfig;
import com.lemongo97.android.anti.net.scrcpy.constants.ScrcpySocketType;
import org.junit.jupiter.api.Test;

public class TestScrcpyClient {

	@Test
	public void connect() throws InterruptedException {
		ScrcpyClient scrcpyClient = new ScrcpyClient("127.0.0.1", 27183, new ScrcpyConfig()
				.adbForward(true)
				.enableVideo(true)
				.enableAudio(false)
				.enableControl(false), null);
		scrcpyClient.connect();
		scrcpyClient.await(ScrcpySocketType.VIDEO);
	}
}
