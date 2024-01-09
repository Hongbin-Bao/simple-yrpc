package com.simple;

import com.simple.channelHandler.ConsumerChannelInitializer;
import com.simple.channelHandler.Handler.MySimpleChannellnboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 提供bootstrap单例
 * todo  这里会有什么问题
 * @author Hongbin BAO
 * @Date 2024/1/9 00:55
 */
@Slf4j
public class NettyBootstrapInitializer {

    private static final Bootstrap bootstrap = new Bootstrap();

    static {
        // 创建channel
        NioEventLoopGroup group = new NioEventLoopGroup();

        // 启动一个客户端需要一个辅助类，bootstrap
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)

                // 选择初始化一个什么样的channel
                .channel(NioSocketChannel.class)
                .handler(new ConsumerChannelInitializer());

    }

    private static NioEventLoopGroup group = new NioEventLoopGroup();


    private NettyBootstrapInitializer() {

    }

    public static Bootstrap getBootstrap() {
        // 建立一个新的channel
        return bootstrap;
    }
}
