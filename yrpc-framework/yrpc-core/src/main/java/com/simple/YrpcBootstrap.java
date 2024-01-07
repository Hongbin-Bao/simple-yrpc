package com.simple;

import com.simple.discovery.Registry;
import com.simple.discovery.RegistryConfig;
import com.simple.discovery.impl.ZookeeperRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 20:19
 */
@Slf4j
public class YrpcBootstrap {

    // YrpcBootstrap 是个单例 我们希望每个应用程序只有一个实例
    private static final YrpcBootstrap yrpcBootstrap = new YrpcBootstrap();

    // 定义相关的一些配置
    private String appName = "default";

    private ReferenceConfig referenceConfig;

    private RegistryConfig registryConfig;

    private ProtocolConfig protocolConfig;

    private int port = 8088;


    // 注册中心
    private Registry registry ;


    public YrpcBootstrap() {
        // 构造启动引导程序 时需要做一些什么初始化的事
    }

    public static YrpcBootstrap getInstance() {
        return yrpcBootstrap;
    }

    /**
     * 用来定义当前应用的名字
     *
     * @param appName
     * @return this 当前实例
     */
    public YrpcBootstrap application(String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * 用来配置一个注册中心
     *
     * @param registryConfig 注册中心
     * @return 当前实例
     */
    public YrpcBootstrap registry(RegistryConfig registryConfig) {

        // 这里维护一个zookeeper实例 但是如果这样写就会将zookeeper和当前工程耦合
        // 我们其实是更希望以后可以扩展更多种不同的实现

        // 尝试使用registryConfig 获取一个注册中心  有点工厂设计模式的例子
        this.registry = registryConfig.getRegistry();
        return this;

    }
//
//    public YrpcBootstrap registry(Registry registry){
//
//    }

    /**
     * 配置当前暴露的服务使用的协议
     *
     * @param protocolConfig 协议的封装
     * @return this 当前实例
     */
    public YrpcBootstrap protocol(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
        if (log.isDebugEnabled()) {
            log.debug("当前工程使用了：" + protocolConfig.toString() + "协议进行序列化");
        }
        return this;
    }
    //  服务提供方的相关api

    /**
     * 发布服务 将接口 实现  注册到服务中心
     *
     * @param service 封装的需要发布的服务
     * @return this 当前实例
     */
    public YrpcBootstrap publish(ServiceConfig<?> service) {

        //  我们抽象了注册中心的概念 使用注册中心的一个实现完成注册
        // 有人会想 此时此刻 难道强耦合了吗？
        registry.register(service);

        return this;
    }

    /**
     * 批量发布
     *
     * @param services 封装的需要发布的服务集合
     * @return
     */
    public YrpcBootstrap publish(List<ServiceConfig<?>> services) {
        for (ServiceConfig<?> service : services) {
            this.publish(service);
        }
        return this;
    }

    /**
     * 启动netty 服务
     */
    public void start() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    //  服务调用方的相关api

    public YrpcBootstrap reference(ReferenceConfig<?> reference) {

        // 在这个方法里我们是否可以拿到相关的配置项 注册中心

        // 配置reference 将来调用get 方法时 方便生成代理对象

        return this;

    }


}

