package com.simple.discovery;

import com.simple.discovery.Registry;
import com.simple.discovery.impl.NacosRegistry;
import com.simple.discovery.impl.ZookeeperRegistry;
import com.simple.exceptions.DiscoveryException;
import com.sun.jdi.event.StepEvent;
import main.java.com.simple.Constant;

import java.util.Locale;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 20:54
 */
public class RegistryConfig {
    // 定义连接的url zookeeper://127.0.0.1:2181  redis:192.168.12.125:3306


    private final String  connectString;

    public RegistryConfig(String connectString) {
        this.connectString = connectString;
    }

    /**
     * 可以使用简答工厂来完成
     * @return
     */
    public Registry getRegistry() {
        // 1. 需要获取注册中心的类型
        String registryType = getRegistryType(connectString,true).toLowerCase().trim();
        if(registryType.equals("zookeeper")){
            String host = getRegistryType(connectString, false);
            return new ZookeeperRegistry(host, Constant.TIME_OUT);
        } else if(registryType.equals("nacos")){
            String host = getRegistryType(connectString, false);
            return new NacosRegistry(host, Constant.TIME_OUT);
        }
        throw new DiscoveryException("未发现合适的注册中心。");
    }

    private String getRegistryType(String connectString,boolean ifType){
        String[] typeAndHost = connectString.split("://");
        if(typeAndHost.length != 2){
            throw new RuntimeException("给定的注册中心连接url不合法");
        }
        if(ifType){
            return typeAndHost[0];
        } else {
            return typeAndHost[1];
        }

    }
}
