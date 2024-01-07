package com.simple;

import com.simple.exceptions.ZookeeperException;
import com.simple.utils.NetUtils;
import com.simple.utils.ZookeeperUtils;
import com.simple.utils.zookeeper.ZookeeperNode;
import lombok.extern.slf4j.Slf4j;
import main.java.com.simple.Constant;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.lang.module.ResolvedModule;
import java.lang.reflect.Proxy;
import java.rmi.registry.Registry;
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

    //  维护一个zookeeper实例
    private ZooKeeper zooKeeper;


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

        zooKeeper = ZookeeperUtils.createZookeeper();
        this.registryConfig = registryConfig;
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


        //  服务名称的节点
        String parentNode = Constant.BASE_PROVIDERS_PATH + "/" + service.getInterface().getName();
        //  这个节点应该是一个持久节点
        if (!ZookeeperUtils.exists(zooKeeper, parentNode, null)) {
            ZookeeperNode zookeeperNode = new ZookeeperNode(parentNode, null);
            ZookeeperUtils.createNode(zooKeeper, zookeeperNode, null, CreateMode.PERSISTENT);
        }


        // 创建本机的临时节点 ip port 服务提供方的端口一般自己设定 我们还需要一个获取ip的方法
        // ip 我们通常是需要一个局域网ip 不是127。0。0。1 也不是ipv6
        // 192.168.12.121
        String node = parentNode + "/" + NetUtils.getIp() + ":" + port;

        if (!ZookeeperUtils.exists(zooKeeper, node, null)) {
            ZookeeperNode zookeeperNode = new ZookeeperNode(node, null);
            ZookeeperUtils.createNode(zooKeeper, zookeeperNode, null, CreateMode.EPHEMERAL);
        }

        if (log.isDebugEnabled()) {
            log.debug("服务{}已经被注册", service.getInterface().getName());
        }
        return this;
    }

    /**
     * 批量发布
     *
     * @param services 封装的需要发布的服务集合
     * @return
     */
    public YrpcBootstrap publish(List<?> services) {

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

