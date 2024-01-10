package com.simple.channelHandler.Handler;

import com.simple.YrpcBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * @author Hongbin BAO
 * @Date 2024/1/9 17:59
 */
public class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        // 服务提供方，给与的结果
        String result = msg.toString(Charset.defaultCharset());
        // 从全局的挂起的请求中寻找与之匹配的待处理的 cf
        CompletableFuture<Object> completableFuture = YrpcBootstrap.PENDING_REQUEST.get(1L);
        completableFuture.complete(result);
    }
}
