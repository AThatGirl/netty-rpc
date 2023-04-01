package com.cj.rpc.comsumer.client;

import com.cj.rpc.comsumer.handler.NettyRpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;


/**
 * netty客户端
 * 1.连接服务端
 * 2.关闭资源
 * 3.提供发送消息的方法
 */
@Component
public class NettyRpcClient implements InitializingBean, DisposableBean {

    @Autowired
    NettyRpcClientHandler nettyRpcClientHandler;
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, // 核心线程数
            4, // 最大线程数
            60, TimeUnit.SECONDS, // 线程空闲时间
            new ArrayBlockingQueue<>(100), // 等待队列
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
    );
    EventLoopGroup group = null;
    Channel channel = null;
    /**
     * 1.连接服务端
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        try {
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //添加编解码器
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            socketChannel.pipeline().addLast(nettyRpcClientHandler);

                        }
                    });
            //连接服务
            channel = bootstrap.connect("127.0.0.1", 8899).sync().channel();
        } catch (Exception e) {
            e.printStackTrace();
            if (channel != null){
                channel.close();
            }
            if (group != null){
                group.shutdownGracefully();
            }
        }


    }

    //服务器关闭也会释放
    @Override
    public void destroy() throws Exception {
        if (channel != null){
            channel.close();
        }
        if (group != null){
            group.shutdownGracefully();
        }
    }


    /**
     * 消息发送
     */
    public Object send(String msg) throws ExecutionException, InterruptedException {
        nettyRpcClientHandler.setReqMsg(msg);
        Future submit = executor.submit(nettyRpcClientHandler);
        //submit.get()获取到的就是call方法return的值
        return submit.get();
    }

}
