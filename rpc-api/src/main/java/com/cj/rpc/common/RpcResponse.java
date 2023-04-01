package com.cj.rpc.common;

import lombok.Data;

@Data
public class RpcResponse {

    //响应id
    private String  responseId;
    //错误信息
    private String error;
    //响应结果
    private Object result;

}
