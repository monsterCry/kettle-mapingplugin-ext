package com.yzziot.kettle.plugin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/6/1
 * @Modified By:
 */
public class NetEncoder extends MessageToByteEncoder<NetCmd> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NetCmd cmd, ByteBuf byteBuf) throws Exception {
        try {
            if(cmd != null) {
                cmd.prepare();
                NetCmd.write(byteBuf,cmd);
                System.out.println(cmd.getSize());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
