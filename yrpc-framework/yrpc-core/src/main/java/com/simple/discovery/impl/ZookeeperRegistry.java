package com.simple.discovery.impl;

import com.simple.ServiceConfig;
import com.simple.discovery.AbstractRegistry;
import com.simple.exceptions.DiscoveryException;
import com.simple.exceptions.NetworkException;
import com.simple.utils.NetUtils;
import com.simple.utils.ZookeeperUtils;
import com.simple.utils.zookeeper.ZookeeperNode;
import lombok.extern.slf4j.Slf4j;
import main.java.com.simple.Constant;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hongbin BAO
 * @Date 2024/1/7 23:16
 */
@Slf4j
public class ZookeeperRegistry extends AbstractRegistry {

    // 维护一个zk实例
    private ZooKeeper zooKeeper;

    public ZookeeperRegistry(ZooKeeper zooKeeper) {
        this.zooKeeper = ZookeeperUtils.createZookeeper();
    }

    public ZookeeperRegistry(String connectString, int timeout) {
        this.zooKeeper = ZookeeperUtils.createZookeeper(connectString, timeout);
    }

    @Override
    public void register(ServiceConfig<?> service) {
        //  服务名称的节点
        String parentNode = main.java.com.simple.Constant.BASE_PROVIDERS_PATH + "/" + service.getInterface().getName();
        //  这个节点应该是一个持久节点
        if (!ZookeeperUtils.exists(zooKeeper, parentNode, null)) {
            ZookeeperNode zookeeperNode = new ZookeeperNode(parentNode, null);
            ZookeeperUtils.createNode(zooKeeper, zookeeperNode, null, CreateMode.PERSISTENT);
        }


        // 创建本机的临时节点 ip port 服务提供方的端口一般自己设定 我们还需要一个获取ip的方法
        // ip 我们通常是需要一个局域网ip 不是127。0。0。1 也不是ipv6
        // 192.168.12.121
        // todo: port 后续处理端口问题
        String node = parentNode + "/" + NetUtils.getIp() + ":" + 8088;

        if (!ZookeeperUtils.exists(zooKeeper, node, null)) {
            ZookeeperNode zookeeperNode = new ZookeeperNode(node, null);
            ZookeeperUtils.createNode(zooKeeper, zookeeperNode, null, CreateMode.EPHEMERAL);
        }

        if (log.isDebugEnabled()) {
            log.debug("服务{}已经被注册", service.getInterface().getName());
        }
        //return this;
    }


    
    @Override
    public InetSocketAddress lookup(String serviceName) {
        // 1。 找到服务对应的节点
        String serviceNode = Constant.BASE_PROVIDERS_PATH + "/" + serviceName;

        //  2。 从zk 中获取他的子节点 192.168.12.123:2151

        List<String> children = ZookeeperUtils.getChildren(zooKeeper, serviceNode, null);
        // 获取了所有的可用的服务列表

        List<InetSocketAddress> inetSocketAddresses = children.stream().map(ipString -> {
            String[] ipAndPort = ipString.split(":");
            String ip = ipAndPort[0];
            int port = Integer.valueOf(ipAndPort[1]);
            return new InetSocketAddress(ip, port);

        }).toList();
        if(inetSocketAddresses.size() == 0){
           throw new DiscoveryException("未发现任何可用的服务主机");
        }

        // todo 问题  我们需要每次调用相关方法的时候都需要去注册中心拉取相关服务列表吗？ 本地缓存+watcher
        //      我们如何合理的选择一个可用的服务 而不是只获取第一个?       负载均衡策略
        //InetAddress address = inetSocketAddresses.get(0).getAddress();
        return inetSocketAddresses.get(0);

    }

    @Override
    public List<InetSocketAddress> lookup(String name, String group) {
        return null;
    }
}
