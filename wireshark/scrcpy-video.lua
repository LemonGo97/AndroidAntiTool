--协议对象 构造函数第一个参数：显示在协议列，第二个参数：协议描述
local scrcpy_video_request = Proto('Scrcpy', 'Scrcpy Video Protocol')

-- 要显示的字段，构造函数第一个参数：用于上方搜索栏过滤的 第二个参数：显示在下方协议中的字段 第三个字段：显示十进制还是十六进制
local packet_type = ProtoField.string("scrcpy.type", "包类型", base.STRING)

-- 第0个包，可能不存在
local adb = ProtoField.bytes("scrcpy.adb", "ADB", base.HEXDUMP)
-- 第一个包，设备名称
local device_name = ProtoField.string("scrcpy.device.name", "设备名称", base.STRING)

-- 第二个包，视频编码及长宽
local video_codec = ProtoField.string("scrcpy.video.codec", "编码器", base.STRING)
local video_width = ProtoField.uint8("scrcpy.video.width", "宽", base.DEC)
local video_height = ProtoField.uint8("scrcpy.video.height", "高", base.DEC)

-- 接下来的Media包
local video_pts_flags = ProtoField.bytes("scrcpy.video.ptsFlags", "PTS 和 Flags", base.HEXDUMP)
local video_frame_length = ProtoField.uint32("scrcpy.video.length", "视频帧长度", base.DEC)
local video_frame = ProtoField.bytes("scrcpy.video.frame", "视频帧内容", base.HEXDUMP)

-- 将字段添加到协议对象
scrcpy_video_request.fields = { packet_type, adb, device_name, video_codec, video_width, video_height, video_pts_flags, video_frame_length, video_frame }

-- 此方法返回bool,返回true表示自定义的协议验证通过，会传入三个参数
-- buffer:包，去掉继承协议之后的内容。比如继承udp，那udp的报文就被去掉了，buffer只表示udp的应用层内容
-- pinfo:显示抓包内容列表的信息。赋值协议名称时会用到
-- tree:下方的树结构
function scrcpy_video_request.dissector(buffer, pinfo, tree)
    local data_dis = Dissector.get("data")
    pinfo.cols.protocol = scrcpy_video_request.name
    local subtree = tree:add(scrcpy_video_request, buffer(), "Scrcpy Video Protocol")
    local first_byte = buffer(0, 1):uint()


    if (first_byte == 0x00 and buffer:len() == 1) then
        subtree:add(packet_type, 'ADB')
        subtree:add(adb, buffer(0, 1))
        return
    end

    if buffer:len() == 64 then
        subtree:add(packet_type, 'Device Info')
        subtree:add(device_name, buffer(0, 64))
        return
    end

    if buffer:len() == 12 then
        subtree:add(packet_type, 'Video MetaData')
        subtree:add(video_codec, buffer(0, 4))
        subtree:add(video_width, buffer(4, 4))
        subtree:add(video_height, buffer(8, 4))
        return
    end


    if buffer:len() < 12 then
        data_dis:call(buffer, pinfo, tree)
        return
    end

    if (first_byte ~= 0x80 and first_byte ~= 0x40 and first_byte ~= 0x00) then
        data_dis:call(buffer, pinfo, tree)
        return
    end


    if first_byte == 0x80 then
        subtree:add(packet_type, 'Control Video Frame')
        --    控制包
    elseif first_byte == 0x40 then
        subtree:add(packet_type, 'Key Video Frame')
        --    关键帧
    elseif first_byte == 0x00 then
        subtree:add(packet_type, 'Video Frame')
        --    非关键帧
    end
    subtree:add(video_pts_flags, buffer(0, 8))
    subtree:add(video_frame_length, buffer(8, 4))


    local offset = pinfo.desegment_offset or 0

    local len = buffer(8, 4):uint()

    while true do
        local nxtpdu = offset + len

        if nxtpdu > buffer:len() - 12 then
            pinfo.desegment_len = nxtpdu - buffer:len() - 12
            pinfo.desegment_offset = offset
            return
        end

        offset = nxtpdu

        if nxtpdu + 12 == buffer:len() then
            subtree:add(video_frame, buffer(12, buffer(8, 4):uint()))
            return
        end
    end

end

-- 注册，让wireshark解析包的时候会调用checker
-- scrcpy_video_request:register_heuristic("tcp", checker)
local tcp_table = DissectorTable.get("tcp.port")
tcp_table:add(11769, scrcpy_video_request)