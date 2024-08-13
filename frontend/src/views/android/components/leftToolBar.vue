<template>
  <canvas id="scrcpy" ref="scrcpy" ></canvas>
</template>
<script>
import {createWebSocket} from "@/utils/index.js";
import CryptoJS from 'crypto-js'

export default {
  props: {
    device: {
      type: String,
      default: () => ""
    },
  },
  data() {
    return {
      websocket: undefined,
      videoDecoder: undefined,
      canvasContext: undefined,
    }
  },
  watch: {
    device: {
      immediate: true,
      handler(newVal, oldVal) {
        if (!newVal || newVal === oldVal) {
          return
        }
        this.closeWebSocket()
        this.setupWebSocket(newVal)
      }
    },
  },
  mounted() {
    this.setupCanvas();
    this.setupVideoDecoder();
    this.setupWebSocket(this.device)
  },
  beforeUnmount() {
    console.log("关闭websocket")
    this.closeWebSocket()
  },
  methods: {
    setupWebSocket(device) {
      if (!device) return;
      this.websocket = createWebSocket("/scrcpy?device=" + device)
      this.websocket.onmessage = (e) => {
        let packet = JSON.parse(e.data)
        switch (packet['packetType']) {
          case 'FORWARD': {
            this.handleScrcpyType(packet);
            break
          }
          case 'DEVICE_NAME': {
            this.handleDeviceInfo(packet);
            break
          }
          case 'VIDEO_METADATA': {
            this.handleVideoMetadata(packet);
            break
          }
          case 'AUDIO_METADATA': {
            this.handleAudioMetadata(packet);
            break
          }
          case 'VIDEO_FRAME': {
            this.handleVideoFrame(packet);
            break
          }
          case 'AUDIO_FRAME': {
            this.handleAudioFrame(packet);
            break
          }
        }
      }
      this.websocket.onclose = (e) => {
        console.log("websocket closed")
      }
    },
    closeWebSocket() {
      if (!this.websocket) {
        return
      }
      this.websocket.close()
    },
    setupCanvas(){
      this.canvasContext = this.$refs.scrcpy.getContext('2d')
    },
    setupVideoDecoder() {
      this.videoDecoder = new VideoDecoder({
        output: this.handleDecodedFrame,
        error: this.videoDecoderError
      });
      this.videoDecoder.configure({
        codec: 'avc1.4d002a',
        hardwareAcceleration: 'prefer-hardware',
      })
    },
    handleDecodedFrame(frame) {
      console.log("==========================")
      let canvasDocument = this.$refs.scrcpy
      console.log(canvasDocument)

      // canvasDocument.width = frame.displayWidth
      // canvasDocument.height = frame.displayHeight
      canvasDocument.width = 422
      canvasDocument.height = 749
      this.canvasContext.drawImage(frame, 0, 0, 422, 749);
      frame.close();
    },
    videoDecoderError(error){
      console.error('VideoDecoder error:', error);
    },
    // 处理Scrcpy信息
    handleScrcpyType(packet){

    },
    // 处理设备信息
    handleDeviceInfo(packet){
      console.log("设备名称：", packet.deviceName);
    },
    // 处理视频帧
    handleVideoMetadata(packet){
      console.log("视频编码器：", packet.codec);
      console.log("视频宽：", packet.width);
      console.log("视频高：", packet.height);
    },
    handleVideoFrame(packet){
      console.log("关键帧：", packet.keyFramePacket, "配置帧：", packet.configPacket, )
      if (!this.videoDecoder){
        return
      }
      let arrayBuffer = this.wordArrayToArrayBuffer(CryptoJS.enc.Base64.parse(packet.frame));
      if (packet.configPacket){
        this.videoDecoder.configure({
          codec: 'avc1.4d002a',
          hardwareAcceleration: 'prefer-hardware',
        })
        this.videoDecoder.configPacket = arrayBuffer
        return;
      }
      let chunk;
      if (packet.keyFramePacket){
        chunk = new EncodedVideoChunk({
          type: 'key', // 或 'delta'，取决于帧类型
          timestamp: packet.pts,
          data: this.mergeArrayBuffer(this.videoDecoder.configPacket, arrayBuffer)
        });
      }else {
        chunk = new EncodedVideoChunk({
          type: 'delta', // 或 'delta'，取决于帧类型
          timestamp: packet.pts,
          data: arrayBuffer
        });
      }
      // 将数据块送入解码器
      this.videoDecoder.decode(chunk);
    },
    // 处理音频帧
    handleAudioMetadata(packet){},
    handleAudioFrame(packet){

    },
    wordArrayToArrayBuffer(wordArray) {
      let arrayBuffer = new ArrayBuffer(wordArray.sigBytes);
      // 将 wordArray 的数据拷贝到 ArrayBuffer 中
      let uint8Array = new Uint8Array(arrayBuffer);
      for (let i = 0; i < wordArray.sigBytes; i++) {
        uint8Array[i] = (wordArray.words[i >>> 2] >>> (24 - (i % 4) * 8)) & 0xff;
      }
      return arrayBuffer;
    },
    mergeArrayBuffer(configWordArrayBuffer, keyFrameWordArrayBuffer) {
      let tmp = new Uint8Array(configWordArrayBuffer.byteLength + keyFrameWordArrayBuffer.byteLength);
      tmp.set(new Uint8Array(configWordArrayBuffer), 0);
      tmp.set(new Uint8Array(keyFrameWordArrayBuffer), configWordArrayBuffer.byteLength);
      return tmp.buffer;
    }
  }
}
</script>
