package com.simple.impl;

import com.simple.annotation.YrpcApi;

/**
 * @author Hongbin BAO
 * @Date 2024/1/15 00:10
 */
@YrpcApi
public class HelloYrpcImpl2 implements HelloYrpc {
    @Override
    public String sayHi(String msg) {
        return "hi consumer:" + msg;
    }
}
