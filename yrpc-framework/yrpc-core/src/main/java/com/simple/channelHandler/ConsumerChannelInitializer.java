package com.simple.channelHandler;

import com.simple.channelHandler.Handler.MySimpleChannelInboundHandler;
import com.simple.channelHandler.Handler.YrpcRequestEncoder;
import com.simple.channelHandler.Handler.YrpcResponseDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Hongbin BAO
 * @Date 2024/1/9 18:02
 */
public class ConsumerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                // netty自带的日志处理器
                .addLast(new LoggingHandler(LogLevel.DEBUG))
                // 消息编码器
                .addLast(new YrpcRequestEncoder())
                // 入栈的解码器
                .addLast(new YrpcResponseDecoder())
                // 处理结果
                .addLast(new MySimpleChannelInboundHandler());

    }
}

