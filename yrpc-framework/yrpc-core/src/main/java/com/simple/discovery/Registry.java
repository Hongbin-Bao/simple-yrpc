package com.simple.discovery;

import com.simple.ServiceConfig;

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
}
