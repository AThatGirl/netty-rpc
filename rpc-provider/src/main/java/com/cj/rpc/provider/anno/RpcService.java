package com.cj.rpc.provider.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于暴露服务接口
 */

//用于类上
@Target(ElementType.TYPE)
//运行时可以获取到
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {



}
