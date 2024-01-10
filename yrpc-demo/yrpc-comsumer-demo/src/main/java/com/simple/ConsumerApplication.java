package com.simple;

import com.simple.discovery.RegistryConfig;
import com.simple.impl.HelloYrpc;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 00:48
 */
@Slf4j
public class ConsumerApplication {

    public static void main(String[] args) {
        // 想尽一切办法获取代理对象,使用ReferenceConfig进行封装
        // reference一定用生成代理的模板方法，get()
        ReferenceConfig<HelloYrpc> reference = new ReferenceConfig<>();
        reference.setInterface(HelloYrpc.class);

        // 代理做了些什么，
        // 1、连接注册中心
        // 2、拉取服务列表
        // 3、选择一个服务并建立连接
        // 4、发送请求，携带一些信息（接口名，参数列表，方法的名字），获得结果
        YrpcBootstrap.getInstance()
                .application("first-yrpc-consumer")
                .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
                .reference(reference);

        // 获取一个代理对象
        HelloYrpc helloYrpc = reference.get();
        String sayHi = helloYrpc.sayHi("你好");
        log.info("sayHi-->{}", sayHi);
    }
}


