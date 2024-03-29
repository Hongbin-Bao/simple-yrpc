package com.simple.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author Hongbin BAO
 * @Date 2024/1/11 20:17
 */
public class SerializeUtil {

    public static byte[] serialize(Object object) {
        // 针对不同的消息类型需要做不同的处理，心跳的请求，没有payload
        if (object == null) {
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
//            log.error("序列化时出现异常");
            throw new RuntimeException(e);
        }
    }

}
