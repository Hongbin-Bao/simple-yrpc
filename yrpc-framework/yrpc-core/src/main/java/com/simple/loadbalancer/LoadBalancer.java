package com.simple.loadbalancer;

import java.net.InetSocketAddress;

/**
 *
 * 负载均衡的接口
 *
 *
 * @author Hongbin BAO
 * @Date 2024/1/13 19:16
 */
public interface LoadBalancer {

    // 他应该具备的能力，根据服务列表找到一个可以用的服务

    /**
     * 根据服务名获取一个可用的服务
     * @param serviceName 服务名称
     * @return 服务地址
     */
    InetSocketAddress selectServiceAddress(String serviceName);

}
