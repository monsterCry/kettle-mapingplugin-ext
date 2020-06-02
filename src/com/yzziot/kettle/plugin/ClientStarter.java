package com.yzziot.kettle.plugin;

import io.netty.channel.ChannelFuture;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/6/2
 * @Modified By:
 */
public class ClientStarter {

    public static void main(String[] args) throws Exception {
        NetClient client = new NetClient();
        client.start();
        ChannelFuture future = client.connect("127.0.0.1",520);
        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(4800);
                    NetCmd cmd = new NetCmd();
                    cmd.setContent("这里是测试消息肯定是开发健康减肥课".getBytes());
                    cmd.setMagic(0xc60);
                    System.out.println("发送消息");
                    future.channel().writeAndFlush(cmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        future.sync().awaitUninterruptibly();
    }
}
