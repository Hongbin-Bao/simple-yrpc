package com.simple.channelHandler.Handler;

import com.simple.YrpcBootstrap;
import com.simple.transport.message.YrpcResponse;
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
@Slf4j
public class MySimpleChannelInboundHandler extends SimpleChannelInboundHandler<YrpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, YrpcResponse yrpcResponse) throws Exception {

        // 服务提供方，给予的结果
        Object returnValue = yrpcResponse.getBody();

        // todo 需要针对code做处理
        returnValue = returnValue == null ? new Object() : returnValue;

        // 从全局的挂起的请求中寻找与之匹配的待处理的completableFuture
        CompletableFuture<Object> completableFuture = YrpcBootstrap.PENDING_REQUEST.get(yrpcResponse.getRequestId());
        completableFuture.complete(returnValue);
        if (log.isDebugEnabled()) {
            log.debug("以寻找到编号为【{}】的completableFuture，处理响应结果。", yrpcResponse.getRequestId());
        }
    }
}
