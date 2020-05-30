package com.yzziot.kettle.plugin;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/5/30
 * @Modified By:
 */
public class NetCmd {
    //4M
    public static int MAX_CONTENT_SIZE = 1024 * 1024 * 4;
    private long magic;
    private long cmd;
    private short version;
    private long opaque;
    private int size;
    private byte[] content;
}
