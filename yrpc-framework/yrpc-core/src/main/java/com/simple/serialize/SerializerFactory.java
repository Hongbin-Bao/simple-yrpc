package com.simple.serialize;

import com.simple.serialize.impl.HessianSerializer;
import com.simple.serialize.impl.JdkSerializer;
import com.simple.serialize.impl.JsonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hongbin BAO
 * @Date 2024/1/11 20:19
 */
@Slf4j
public class SerializerFactory {

    private final static ConcurrentHashMap<String,SerializerWrapper> SERIALIZER_CACHE = new ConcurrentHashMap<>(8);
    private final static ConcurrentHashMap<Byte,SerializerWrapper> SERIALIZER_CACHE_CODE = new ConcurrentHashMap<>(8);

    static {
        SerializerWrapper jdk = new SerializerWrapper((byte) 1, "jdk", new JdkSerializer());
        SerializerWrapper json = new SerializerWrapper((byte) 2, "json", new JsonSerializer());
        SerializerWrapper hessian = new SerializerWrapper((byte) 3, "hessian", new HessianSerializer());
        SERIALIZER_CACHE.put("jdk",jdk);
        SERIALIZER_CACHE.put("json",json);
        SERIALIZER_CACHE.put("hessian",hessian);

        SERIALIZER_CACHE_CODE.put((byte) 1, jdk);
        SERIALIZER_CACHE_CODE.put((byte) 2, json);
        SERIALIZER_CACHE_CODE.put((byte) 3, hessian);
    }

    /**
     * 使用工厂方法获取一个SerializerWrapper
     * @param serializeType 序列化的类型
     * @return SerializerWrapper
     */
    public static SerializerWrapper getSerializer(String serializeType) {
        SerializerWrapper  serializerWrapper =  SERIALIZER_CACHE.get(serializeType);
        if(serializerWrapper == null){
            if(log.isDebugEnabled()){
                log.error("未找到您配置的【{}】序列化工具,默认选用jdk的序列化方式",serializeType);
            }
            return SERIALIZER_CACHE.get("jdk");
        }
        return SERIALIZER_CACHE.get(serializeType);
    }

    public static SerializerWrapper getSerializer(Byte serializeCode) {
        SerializerWrapper serializerWrapper = SERIALIZER_CACHE_CODE.get(serializeCode);
        if(serializerWrapper == null){
            log.error("未找到您配置的【{}】序列化工具，默认选用jdk的序列化方式。",serializeCode);
            return SERIALIZER_CACHE.get("jdk");
        }
        return SERIALIZER_CACHE_CODE.get(serializeCode);
    }
}
