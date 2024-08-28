```shell
adb push scrcpy-server /data/local/tmp/scrcpy-server.jar
adb forward tcp:27183 localabstract:scrcpy
adb shell CLASSPATH=/data/local/tmp/scrcpy-server.jar app_process / com.genymobile.scrcpy.Server 2.6.1 tunnel_forward=true audio=false control=false
app_process -Djava.class.path=scrcpy-server.jar  /data/local/tmp com.genymobile.scrcpy.Server 2.6.1 tunnel_forward=true audio=false control=false
cd /data/local/tmp && nohup app_process -Djava.class.path=scrcpy-server.jar  /data/local/tmp com.genymobile.scrcpy.Server 2.6.1 tunnel_forward=true audio=false control=false > /dev/null &
```

```shell
scrcpy --force-adb-forward
```

```shell
ps -ef|grep scrcpy
```