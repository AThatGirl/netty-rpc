package com.cj.rpc.comsumer.processor;

import com.cj.rpc.comsumer.anno.RpcReference;
import com.cj.rpc.comsumer.proxy.RpcClientProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * bean的后置增强
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    RpcClientProxy rpcClientProxy;

    /**
     * 自定义注解注入
     * <p>
     * 每一个bean都会执行这个增强方法
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //获取所有bean的字段
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            //查看bean的字段中有没有对应的注解RpcReference
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if (annotation != null) {
                //获取代理对象
                Object proxy = rpcClientProxy.getProxy(field.getType());
                //属性注入
                try {
                    field.setAccessible(true);
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
