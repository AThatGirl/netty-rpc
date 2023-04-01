package com.cj.rpc.provider.handler;

import com.alibaba.fastjson.JSON;
import com.cj.rpc.common.RpcRequest;
import com.cj.rpc.common.RpcResponse;
import com.cj.rpc.provider.anno.RpcService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 自定义业务处理类
 * 1.将标有@RpcService的注解的bean进行缓存
 * 2.接收客户端的请求
 * 3.根据传递过来的beanName从缓存中查找
 * 4.通过反射调用bean的方法
 * 5.给客户端响应
 */
@Component
//设置通道共享
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {

    static Map<String, Object> SERVICE_INSTANCE_MAP = new HashMap<>();


    //将标有@RpcService的注解的bean进行缓存
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //通过注解获取Bean
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        //循环遍历
        Set<Map.Entry<String, Object>> entries = serviceMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
//            System.out.println(entry.getKey()+ ":"+entry.getValue());
            Object serviceBean = entry.getValue();
            if (serviceBean.getClass().getInterfaces().length == 0) {
                throw new RuntimeException("对外暴露的服务必须实现接口");
            }
            //默认处理第一个作为缓存bean的名字
            String serviceName = serviceBean.getClass().getInterfaces()[0].getName();
            SERVICE_INSTANCE_MAP.put(serviceName, serviceBean);
        }
    }

    //读取客户端的消息
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        //接收客户端的请求
        RpcRequest rpcRequest = JSON.parseObject(msg, RpcRequest.class);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResponseId(rpcRequest.getRequestId());

        //业务处理
        try {
            rpcResponse.setResult(handler(rpcRequest));
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setError(e.getMessage());
        }
        //响应数据
        channelHandlerContext.writeAndFlush(JSON.toJSONString(rpcResponse));
    }

    private Object handler(RpcRequest rpcRequest) throws InvocationTargetException {
        //根据传递过来的bean的名称查找
        Object serviceBean = SERVICE_INSTANCE_MAP.get(rpcRequest.getClassName());
        //判断这个对象有没有
        if (serviceBean == null){
            throw new RuntimeException("服务端没有找到服务");
        }
        //通过反射调用bean的方法
        FastClass proxy = FastClass.create(serviceBean.getClass());
        FastMethod method = proxy.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        return method.invoke(serviceBean, rpcRequest.getParameters());
    }


}
