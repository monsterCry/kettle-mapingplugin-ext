package com.yzziot.kettle.plugin;

import io.netty.buffer.ByteBuf;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/5/30
 * @Modified By:
 */
public class NetCmd {
    //4M
    public static final int MAX_CONTENT_SIZE = 1024 * 1024 * 4;
    public static final int HEADER_SIZE = 39;
    public static final int LENGTH_OFFSET = 27;
    public static final int LENGTH_FIELD_SIZE = 4;

    private long magic;
    private long cmd;
    private short version;
    private byte flag;
    /**消息系列**/
    private long opaque;
    private int size;
    private long crc;
    private byte[] content;


    private CmdType type;

    public static void write(ByteBuf buf, NetCmd netCmd) {
        buf.writeLong(netCmd.magic);
        buf.writeLong(netCmd.cmd);
        buf.writeShort(netCmd.version);
        buf.writeByte(netCmd.flag);
        buf.writeLong(netCmd.opaque);
        buf.writeInt(netCmd.size);
        //需要计算
        //暂不使用
        buf.writeLong(netCmd.crc);
        buf.writeBytes(netCmd.content);
    }

    public static Object read(ByteBuf buf) {
        NetCmd result = new NetCmd();
        result.magic = buf.readLong();
        result.cmd = buf.readLong();
        result.version = buf.readShort();
        result.flag = buf.readByte();
        result.opaque = buf.readLong();
        result.size = buf.readInt();
        result.crc = buf.readLong();
        //TODO: 2020/6/1  因为会频繁的使用，考虑使用内存池
        result.content = new byte[result.size - HEADER_SIZE];
        buf.readBytes(result.content);
        if(result.flag == 1) {
            result.type = CmdType.REQUEST;
        } else {
            result.type = CmdType.RESPONSE;
        }
        return result;
    }

    public void prepare() {
        size = HEADER_SIZE + (content == null ? 0: content.length);
        type = CmdType.REQUEST;
    }

    public enum CmdType {
        REQUEST,
        RESPONSE
    }

    public long getMagic() {
        return magic;
    }

    public void setMagic(long magic) {
        this.magic = magic;
    }

    public long getCmd() {
        return cmd;
    }

    public void setCmd(long cmd) {
        this.cmd = cmd;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public CmdType getType() {
        return type;
    }

    public void setType(CmdType type) {
        this.type = type;
    }

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getCrc() {
        return crc;
    }

    public void setCrc(long crc) {
        this.crc = crc;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
