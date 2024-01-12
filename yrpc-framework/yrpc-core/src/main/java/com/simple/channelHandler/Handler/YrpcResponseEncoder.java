package com.simple.channelHandler.Handler;

import com.simple.serialize.Serializer;
import com.simple.serialize.SerializerFactory;
import com.simple.transport.message.MessageFormatConstant;
import com.simple.transport.message.RequestPayload;
import com.simple.transport.message.YrpcRequest;
import com.simple.transport.message.YrpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 *
 *
 * @author Hongbin BAO
 * @Date 2024/1/11 20:24
 */
@Slf4j
public class YrpcResponseEncoder extends MessageToByteEncoder<YrpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, YrpcResponse yrpcResponse, ByteBuf byteBuf) throws Exception {
        // 4个字节的魔数值
        byteBuf.writeBytes(MessageFormatConstant.MAGIC);
        // 1个字节的版本号
        byteBuf.writeByte(MessageFormatConstant.VERSION);
        // 2个字节的头部的长度
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);
        // 总长度不清楚，不知道body的长度 writeIndex(写指针)
        byteBuf.writerIndex(byteBuf.writerIndex() + MessageFormatConstant.FULL_FIELD_LENGTH);
        // 3个类型
        byteBuf.writeByte(yrpcResponse.getCode());
        byteBuf.writeByte(yrpcResponse.getSerializeType());
        byteBuf.writeByte(yrpcResponse.getCompressType());
        // 8字节的请求id
        byteBuf.writeLong(yrpcResponse.getRequestId());
        System.out.println("********");
        System.out.println(yrpcResponse.getRequestId());
        System.out.println("********");


        // 写入请求体（requestPayload）


        // 对响应做序列化
        Serializer serializer = SerializerFactory.getSerializer(yrpcResponse.getSerializeType()).getSerializer();


        byte[] body = serializer.serialize(yrpcResponse.getBody());


        // todo 压缩

        if(body != null){
            byteBuf.writeBytes(body);
        }
        int bodyLength = body == null ? 0 : body.length;

        // 重新处理报文的总长度
        // 先保存当前的写指针的位置
        int writerIndex = byteBuf.writerIndex();
        // 将写指针的位置移动到总长度的位置上
        byteBuf.writerIndex(MessageFormatConstant.MAGIC.length
                + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.HEADER_FIELD_LENGTH
        );
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + bodyLength);
        // 将写指针归位
        byteBuf.writerIndex(writerIndex);

        if(log.isDebugEnabled()){
            log.debug("响应【{}】已经在服务端完成编码工作",yrpcResponse.getRequestId());
        }

    }

//    private byte[] getBodyBytes(Object requestPayload) {
//        // 针对不同的消息类型需要做不同的处理，心跳的请求，没有payload
//        if(requestPayload == null){
//            return null;
//        }
//
//        // 希望可以通过一些设计模式，面向对象的编程，让我们可以配置修改序列化和压缩的方式
//        // 对象怎么变成一个字节数据  序列化  压缩
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
//            outputStream.writeObject(requestPayload);
//
//            // 压缩
//
//            return baos.toByteArray();
//        } catch (IOException e) {
//            log.error("序列化时出现异常");
//            throw new RuntimeException(e);
//        }
//    }
}

