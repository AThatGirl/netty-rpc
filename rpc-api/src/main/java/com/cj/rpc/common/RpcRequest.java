package com.cj.rpc.common;

import lombok.Data;

@Data
public class RpcRequest {

    //请求id
    private String requestId;

    //类名
    private String className;

    //方法名
    private String methodName;

    //参数类型
    private Class<?>[] parameterTypes;

    //参数
    private Object[] parameters;
}
