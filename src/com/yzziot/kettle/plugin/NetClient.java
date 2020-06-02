package com.yzziot.kettle.plugin;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Description:
 * @Author: admin
 * @Date: 2020/6/1
 * @Modified By:
 */
public class NetClient extends NetBasic implements NetService {

    private Bootstrap bootstrap;
    private EventLoopGroup workGroup;

    public NetClient() {
        bootstrap = new Bootstrap();
        workGroup = new NioEventLoopGroup(1);
    }

    @Override
    public void start() {
        bootstrap
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new NetEncoder());
                        pipeline.addLast(new NetDecoder());
                        pipeline.addLast(new ClientCmdHandler());
                    }
                });
    }

    @Override
    public void shutdown() {

    }

    public ChannelFuture connect(String host, int port) {
        return bootstrap.connect(host,port);
    }

    public class ClientCmdHandler extends SimpleChannelInboundHandler<NetCmd> {
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, NetCmd cmd) throws Exception {
            processRecive(cmd);
        }
    }
}
