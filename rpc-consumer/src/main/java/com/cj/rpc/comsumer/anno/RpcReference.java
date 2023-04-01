package com.cj.rpc.comsumer.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 引用代理类
 */
//作用于字段
@Target(ElementType.FIELD)
//运行时可以获取
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
}
