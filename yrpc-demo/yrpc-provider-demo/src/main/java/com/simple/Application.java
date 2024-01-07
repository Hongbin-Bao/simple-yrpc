package com.simple;

import com.simple.discovery.RegistryConfig;
import com.simple.impl.HelloYrpc;
import com.simple.impl.HelloYrpcImpl;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 00:36
 */

public class Application {

    public static void main(String[] args) {
        // 服务提供方，需要注册服务，启动服务
        // 1、封装要发布的服务
        ServiceConfig<HelloYrpc> service = new ServiceConfig<>();
        service.setInterface(HelloYrpc.class);
        service.setRef(new HelloYrpcImpl());
        // 2、定义注册中心

        // 3、通过启动引导程序，启动服务提供方
        //   （1） 配置 -- 应用的名称 -- 注册中心 -- 序列化协议 -- 压缩方式
        //   （2） 发布服务

        YrpcBootstrap.getInstance().application("first-yrpc-provider")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .protocol(new ProtocolConfig("jdk"))
                        .publish(service).start();
    }
}
