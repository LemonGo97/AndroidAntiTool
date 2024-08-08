# Scrcpy Java开发笔记

以下内容参考自[面向开发人员的 scrcpy](https://github.com/Genymobile/scrcpy/blob/master/doc/develop.md)

## scrcpy-server

`scrcpy-server` 负责启动服务端，服务端负责绑定 Android 设备上的 `localabstract:scrcpy` 并等待链接

> 电脑要连接，需使用 `adb forward` 进行端口转发，然后 PC 连接27183 端口即可获取图像

最多打开 3 个套接字，顺序如下：

- 视频插座
- 音频插座
- 控制插座

每一个都可以被禁用（分别通过--no-video、--no-audio和 --no-control直接或间接禁用）。例如，如果--no-audio设置了 ，则首先打开视频套接字，然后打开控制套接字。

### 启动命令

```shell
CLASSPATH=/data/local/tmp/scrcpy-server.jar app_process / com.genymobile.scrcpy.Server 2.6.1
```

后边的第一个参数：2.6.1 是 scrcpy-server 的版本号，从Github下载时可知道版本号

> 这个启动命令是用于scrcpy自带的client使用，如果手动使用这个命令启动server，会报错
>
> 需要注意的是，scrcpy客户端每次启动后，会自动删除scrcpy-server文件

### 作为独立服务器使用

- `send_device_meta=false`：禁用在第一个套接字上发送的设备元数据（实际上是设备名称）
- `send_frame_meta=false`：禁用每个数据包的 12 字节标头
- `send_dummy_byte`：禁用在前向连接上发送的虚拟字节
- `send_codec_meta`：禁用编解码器信息（以及视频的初始设备大小）
- `raw_stream`：禁用以上所有

```shell
CLASSPATH=/data/local/tmp/scrcpy-server-manual.jar app_process / com.genymobile.scrcpy.Server 2.6.1 tunnel_forward=true audio=false control=false cleanup=false raw_stream=true max_size=1920
```

### 使用ffmpeg测试

```shell
ffplayer -i tcp://127.0.0.1:27183
```