package com.yzziot.kettle.plugin;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/6/2
 * @Modified By:
 */
public class ServerStarter {
    public static void main(String[] args) {
        NetServer server = new NetServer(520);
        server.start();
    }
}
