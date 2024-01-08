package com.simple.discovery;

import com.simple.ServiceConfig;

import java.net.InetSocketAddress;

/**
 *
 *  思考 注册中心应该具有什么样的能力
 * @author Hongbin BAO
 * @Date 2024/1/7 23:11
 */
public interface Registry {
    /**
     * 注册服务
     * @param serviceConfig 服务的配置内容
     */
    public void register(ServiceConfig<?> serviceConfig);

    /**
     * 从注册中心拉取一个可用的服务
     * @param serviceName 服务的名称
     * @return 服务的ip加端口
     */
    InetSocketAddress lookup(String serviceName);
}
