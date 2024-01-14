package com.simple.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 *  服务调用方发起的请求内容
 * @author Hongbin BAO
 * @Date 2024/1/9 18:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class YrpcRequest {

    // 请求的id
    private long requestId;

    // 请求的类型，压缩的类型，序列化的方式
    private byte requestType;
    private byte compressType;
    private byte serializeType;

    private long timeStamp;

    // 具体的消息体
    private RequestPayload requestPayload;


}
