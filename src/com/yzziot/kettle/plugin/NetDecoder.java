package com.yzziot.kettle.plugin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/6/1
 * @Modified By:
 */
public class NetDecoder extends LengthFieldBasedFrameDecoder {

    //lengthAdjustment -
    // 满足公式: 发送的字节数组
    // datalen - lengthFieldLength = datalen + lengthFieldOffset + lengthAdjustment
    //-4=27+x

    private static final int maxFrame = 1024 * 1024 * 20;

    public NetDecoder() {
        super(maxFrame, NetCmd.LENGTH_OFFSET, NetCmd.LENGTH_FIELD_SIZE,-31,0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            if(frame.readableBytes() < NetCmd.HEADER_SIZE) {
                return null;
            }
            System.out.println(frame.readableBytes() + "-------------------");
            return NetCmd.read(frame);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.channel().close();
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
        return null;
    }
}
