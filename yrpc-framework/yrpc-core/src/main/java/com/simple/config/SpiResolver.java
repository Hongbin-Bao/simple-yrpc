package com.simple.config;

import com.simple.compress.Compressor;
import com.simple.compress.CompressorFactory;
import com.simple.loadbalancer.LoadBalancer;
import com.simple.serialize.Serializer;
import com.simple.serialize.SerializerFactory;
import com.simple.spi.SpiHandler;

import java.util.List;

/**
 * @author Hongbin BAO
 * @Date 2024/1/15 17:32
 */
public class SpiResolver {

    /**
     * 通过spi的方式加载配置项
     * @param configuration 配置上下文
     */
    public void loadFromSpi(Configuration configuration) {

        // 我的spi的文件中配置了很多实现（自由定义，只能配置一个实现，还是多个）
        List<ObjectWrapper<LoadBalancer>> loadBalancerWrappers = SpiHandler.getList(LoadBalancer.class);
        // 将其放入工厂
        if(loadBalancerWrappers != null && loadBalancerWrappers.size() > 0){
            configuration.setLoadBalancer(loadBalancerWrappers.get(0).getImpl());
        }

        List<ObjectWrapper<Compressor>> objectWrappers = SpiHandler.getList(Compressor.class);
        if(objectWrappers != null){
            objectWrappers.forEach(CompressorFactory::addCompressor);
        }

        List<ObjectWrapper<Serializer>> serializerObjectWrappers = SpiHandler.getList(Serializer.class);
        if (serializerObjectWrappers != null){
            serializerObjectWrappers.forEach(SerializerFactory::addSerializer);
        }
    }
}
