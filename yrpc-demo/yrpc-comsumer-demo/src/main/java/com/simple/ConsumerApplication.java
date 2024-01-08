package com.simple;

import com.simple.discovery.RegistryConfig;
import com.simple.impl.HelloYrpc;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 00:48
 */
public class ConsumerApplication {
    public static void main(String[] args) {

        // 想尽一切办法获取代理对象 使用referenceconfig 进行封装
        // reference 一定用生成代理的模版方法 get()
        ReferenceConfig<HelloYrpc> reference = new ReferenceConfig<>();
        reference.setInterface(HelloYrpc.class);

        // 代理做了些什么
        // 1. 连接注册中心
        // 2. 拉取服务列表
        // 3. 选择一个服务并建立连接
        // 4. 发送请求 携带一些信息 接口名 参数
        YrpcBootstrap.getInstance()
                .application("first-yrpc-consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .reference(reference);


        // 获取一个代理对象

        HelloYrpc helloYrpc = reference.get();
        helloYrpc.sayHi("你好");
    }
}
