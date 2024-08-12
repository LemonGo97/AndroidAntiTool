package com.lemongo97.android.anti.scrcpy;

import com.google.gson.Gson;
import com.lemongo97.android.anti.scrcpy.codec.packet.ScrcpyDeviceInfoPacket;
import com.lemongo97.android.anti.scrcpy.codec.packet.ScrcpyMediaPacket;
import com.lemongo97.android.anti.scrcpy.codec.packet.ScrcpyVideoMetaDataPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootTest
public class TestScrcpy {

	private static final String deviceInfoPacketFilePath = "scrcpy/deviceInfoPacket.bin";
	private static final String videoConfigFramePacketFilePath = "scrcpy/video/videoConfigFramePacket.bin";
	private static final String videoKeyFramePacketFilePath = "scrcpy/video/videoKeyFramePacket.bin";
	private static final String videoMetaDataPacketFilePath = "scrcpy/video/videoMetaDataPacket.bin";

	@Autowired
	private Gson gson;

	@Test
	public void testDeviceInfoPacketParse() throws IOException {
		ByteBuf packetBytebuf = this.getFileByteBuf(deviceInfoPacketFilePath);
		ScrcpyDeviceInfoPacket packet = Assertions.assertDoesNotThrow(() -> new ScrcpyDeviceInfoPacket(packetBytebuf));
		Assertions.assertNotNull(packet);
		Assertions.assertEquals("22021211RC", packet.getDeviceName());
	}

	@Test
	public void testVideoMetaDataPacketParse() throws IOException {
		ByteBuf packetBytebuf = this.getFileByteBuf(videoMetaDataPacketFilePath);
		ScrcpyVideoMetaDataPacket packet = Assertions.assertDoesNotThrow(() -> new ScrcpyVideoMetaDataPacket(packetBytebuf));
		Assertions.assertNotNull(packet);
		Assertions.assertEquals("h264", packet.getCodec());
		Assertions.assertEquals(2160, packet.getWidth());
		Assertions.assertEquals(3840, packet.getHeight());
	}

	@Test
	public void testVideoConfigFramePacketParse() throws IOException {
		ByteBuf packetBytebuf = this.getFileByteBuf(videoConfigFramePacketFilePath);
		ScrcpyMediaPacket packet = Assertions.assertDoesNotThrow(() -> new ScrcpyMediaPacket(packetBytebuf));
		Assertions.assertNotNull(packet);
		Assertions.assertTrue(packet.isConfigPacket());
		Assertions.assertFalse(packet.isKeyFramePacket());
		Assertions.assertNull(packet.getPts());
		Assertions.assertEquals(32, packet.getLength());
		Assertions.assertEquals(32, packet.getFrame().readableBytes());
		System.out.println(this.gson.toJson(packet));
	}

	@Test
	public void testVideoKeyFramePacketParse() throws IOException {
		ByteBuf packetBytebuf = this.getFileByteBuf(videoKeyFramePacketFilePath);
		ScrcpyMediaPacket packet = Assertions.assertDoesNotThrow(() -> new ScrcpyMediaPacket(packetBytebuf));
		Assertions.assertNotNull(packet);
		Assertions.assertFalse(packet.isConfigPacket());
		Assertions.assertTrue(packet.isKeyFramePacket());
		Assertions.assertEquals(1297877072L, packet.getPts());
		Assertions.assertEquals(145713, packet.getLength());
		Assertions.assertEquals(145713, packet.getFrame().readableBytes());
	}

	/**
	 * 从文件构建一个 ByteBuf 对象
	 * @param filePath 文件路径（相对于resource文件夹）
	 * @return ByteBuf
	 * @throws IOException 文件读取异常
	 */
	private ByteBuf getFileByteBuf(String filePath) throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(this.getFile(filePath))) {
			return Unpooled.wrappedBuffer(fileInputStream.readAllBytes());
		}
	}

	/**
	 * 从classpath（resource目录）下获取文件对象
	 * @param filePath 文件路径（相对于resource文件夹）
	 * @return File
	 * @throws IOException 文件读取异常
	 */
	private File getFile(String filePath) throws IOException {
		return ResourceUtils.getFile("classpath:" + filePath);
	}
}
