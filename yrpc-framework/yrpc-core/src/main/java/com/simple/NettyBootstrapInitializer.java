package com.simple;


import com.simple.channelHandler.ConsumerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

/**
 * 提供bootstrap单例
 * todo  这里会有什么问题
 *
 * @author Hongbin BAO
 * @Date 2024/1/9 00:55
 */
@Slf4j
public class NettyBootstrapInitializer {

    private static final Bootstrap bootstrap = new Bootstrap();

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                // 选择初始化一个什么样的channel
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
                                // 服务提供方，给与的结果
                                String result = msg.toString(Charset.defaultCharset());
                                // 从全局的挂起的请求中寻找与之匹配的待处理的 cf
                                CompletableFuture<Object> completableFuture = YrpcBootstrap.PENDING_REQUEST.get(1L);
                                completableFuture.complete(result);
                            }
                        });
                    }
                });
    }

    private NettyBootstrapInitializer() {
    }

    public static Bootstrap getBootstrap() {
        return bootstrap;
    }
}
