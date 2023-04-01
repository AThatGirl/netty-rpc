package com.cj.rpc.comsumer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * 客户端业务处理类
 */
@Component
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<String> implements Callable {

    ChannelHandlerContext context;

    //发送消息
    private String reqMsg;

    //接收消息
    private String respMsg;

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    /**
     * 读取服务端消息
     */
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        respMsg = msg;
        //唤醒等待线程
        notify();
    }

    /**
     *  通道连接就绪事件
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    /**
     * 给服务端发送消息
     */
    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(reqMsg);
        //将线程处于等待状态
        wait();
        return respMsg;
    }
}
