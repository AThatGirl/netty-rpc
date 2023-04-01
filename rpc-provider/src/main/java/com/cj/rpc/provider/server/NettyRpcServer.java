package com.cj.rpc.provider.server;

import com.cj.rpc.provider.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * netty的服务端
 * 启动服务端，监听端口
 */

@Component
public class NettyRpcServer implements DisposableBean {

    @Autowired
    NettyServerHandler nettyServerHandler;

    EventLoopGroup boosGroup = null;
    EventLoopGroup workerGroup = null;

    //用于启动netty服务端
    public void start(String host, int port) {
        try {
            //创建boosGroup和workerGroup
            boosGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            //设置启动助手

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //添加string的编解码器
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            //添加自定义处理器
                            socketChannel.pipeline().addLast(nettyServerHandler);


                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            System.out.println("============服务端启动成功================");
            //监听通道的关闭状态
            channelFuture.channel().closeFuture();
            //
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (boosGroup != null){
                boosGroup.shutdownGracefully();
            }
            if (workerGroup != null){
                workerGroup.shutdownGracefully();
            }
        }

    }


    //spring容器关闭后执行
    @Override
    public void destroy() throws Exception {
        if (boosGroup != null){
            boosGroup.shutdownGracefully();
        }
        if (workerGroup != null){
            workerGroup.shutdownGracefully();
        }
    }
}
