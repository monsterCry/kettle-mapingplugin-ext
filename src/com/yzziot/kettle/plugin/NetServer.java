package com.yzziot.kettle.plugin;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/6/1
 * @Modified By:
 */
public class NetServer extends NetBasic implements NetService {

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private boolean useEpoll = false;
    private int port;

    public NetServer(int port) {
        super();
        this.port = port;
        bootstrap = new ServerBootstrap();
        String osName = System.getProperty("os.name");
        //根据操作系统创建group
        if(osName.toLowerCase().contains("window")) {
            bossGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup();
        } else {
            bossGroup = new EpollEventLoopGroup(1);
            workGroup = new EpollEventLoopGroup();
            useEpoll = true;
        }
    }

    @Override
    public void start() {
        ServerBootstrap childHandler = this.bootstrap
                .group(bossGroup,workGroup)
                .localAddress(port)
                .channel(useEpoll? EpollServerSocketChannel.class: NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new NetEncoder());
                        pipeline.addLast(new NetDecoder());
                        //连接管理
                        pipeline.addLast(new NetConnectionHandler());
                        pipeline.addLast(new ServerCmdHandler());
                    }
                });
        try {
            childHandler.bind().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void shutdown() {

    }

    public class ServerCmdHandler extends SimpleChannelInboundHandler<NetCmd> {
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, NetCmd cmd) throws Exception {
            System.out.println(cmd.getSize());
            String str = new String(cmd.getContent());
            System.out.println(str);
            processRecive(cmd);
        }
    }
}
/***
 * 连接管理
 * **/
class NetConnectionHandler extends ChannelDuplexHandler {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("新的连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("连接断开");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(" -------------------" +cause.getMessage());
    }
}
