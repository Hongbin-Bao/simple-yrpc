package com.simple.core;

import com.simple.NettyBootstrapInitializer;
import com.simple.YrpcBootstrap;
import com.simple.compress.CompressorFactory;
import com.simple.discovery.Registry;
import com.simple.enumeration.RequestType;
import com.simple.serialize.SerializerFactory;
import com.simple.transport.message.YrpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Hongbin BAO
 * @Date 2024/1/13 23:16
 */
@Slf4j
public class HeartbeatDetector {

    public static void detectHeartbeat(String ServiceName){
        // 1、从注册中心拉取服务列表并建立连接
        Registry registry = YrpcBootstrap.getInstance().getRegistry();
        List<InetSocketAddress> addresses = registry.lookup(ServiceName);

        // 将连接进行缓存
        for (InetSocketAddress address : addresses) {
            try {
                if(!YrpcBootstrap.CHANNEL_CACHE.containsKey(address)){
                    Channel channel = NettyBootstrapInitializer.getBootstrap().connect(address).sync().channel();
                    YrpcBootstrap.CHANNEL_CACHE.put(address,channel);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 3、任务，定期发送消息
        Thread thread = new Thread(() ->
                new Timer().scheduleAtFixedRate(new MyTimerTask(), 0, 2000)
                ,"yrpc-HeartbeatDetector-thread");
        thread.setDaemon(true);
        thread.start();

    }

    private static class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            // 将响应时长的map清空
            YrpcBootstrap.ANSWER_TIME_CHANNEL_CACHE.clear();

            // 遍历所有的channel
            Map<InetSocketAddress, Channel> cache = YrpcBootstrap.CHANNEL_CACHE;
            for (Map.Entry<InetSocketAddress, Channel> entry: cache.entrySet()){
                Channel channel = entry.getValue();

                long start = System.currentTimeMillis();
                // 构建一个心跳请求
                YrpcRequest yrpcRequest = YrpcRequest.builder()
                        .requestId(YrpcBootstrap.ID_GENERATOR.getId())
                        .compressType(CompressorFactory.getCompressor(YrpcBootstrap.COMPRESS_TYPE).getCode())
                        .requestType(RequestType.HEART_BEAT.getId())
                        .serializeType(SerializerFactory.getSerializer(YrpcBootstrap.SERIALIZE_TYPE).getCode())
                        .timeStamp(start)
                        .build();

                // 4、写出报文
                CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                // 将 completableFuture 暴露出去
                YrpcBootstrap.PENDING_REQUEST.put(yrpcRequest.getRequestId(), completableFuture);

                channel.writeAndFlush(yrpcRequest).addListener((ChannelFutureListener) promise -> {
                    if (!promise.isSuccess()) {
                        completableFuture.completeExceptionally(promise.cause());
                    }
                });

                Long endTime = 0L;
                try {
                    completableFuture.get();
                    endTime = System.currentTimeMillis();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                Long time = endTime - start;

                // 使用treemap进行缓存
                YrpcBootstrap.ANSWER_TIME_CHANNEL_CACHE.put(time,channel);
                log.debug("和[{}]服务器的响应时间是[{}].",entry.getKey(),time);

            }

            log.info("-----------------------响应时间的treemap----------------------");
            for (Map.Entry<Long,Channel> entry:YrpcBootstrap.ANSWER_TIME_CHANNEL_CACHE.entrySet() ){
                if(log.isDebugEnabled()){
                    log.debug("[{}]--->channelId:[{}]",entry.getKey(),entry.getValue().id());
                }
            }
        }
    }

}
