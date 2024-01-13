package com.simple.loadbalancer;

import java.net.InetSocketAddress;

/**
 * @author Hongbin BAO
 * @Date 2024/1/13 19:17
 */
public interface Selector {

    /**
     * 根据服务列表执行一种算法获取一个服务节点
     * @return 具体的服务节点
     */
    InetSocketAddress getNext();


    // todo 服务动态上线需要进行reBalance
    void reBalance();

}
