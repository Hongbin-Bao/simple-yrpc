package com.simple;

import com.simple.discovery.Registry;
import com.simple.exceptions.NetworkException;
import com.simple.proxy.handler.RpcConsumerInvocationHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 21:31
 */
@Slf4j
public class ReferenceConfig<T> {

    private Class<T> interfaceRef;

    private Registry registry;


    public Class<T> getInterface() {
        return interfaceRef;
    }

    public void setInterface(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    /**
     * 代理设计模式 生成一个api 接口的代理对象，
     *
     * @return 代理对象
     */
    public T get() {

        // 此处一定是使用动态代理完成了一些工作

        // 此处一定是使用动态代理完成了一些工作
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<T>[] classes = new Class[]{interfaceRef};
        InvocationHandler handler = new RpcConsumerInvocationHandler(registry,interfaceRef);

        // 使用动态代理生成代理对象
        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, handler);

        return (T) helloProxy;
//        // 使用动态代理生成代理对象
//        Object helloProxy = Proxy.newProxyInstance(classLoader, classes, new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                // 我们调用 sayhi 方法 事实上会走进这个代码段中
//                // 我们已经知道 method(具体的方法) args（参数列表）
//                log.info("method-->{}", method.getName());
//                log.info("args-->{}", args);
//
//
//                // 1。发现服务 从注册中心 寻找一个可用的服务
//                //Registry registry = registryConfig.getRegistry();
//                //  传入服务的名字  返回ip+端口
//                // todo 问题  我们需要每次调用相关方法的时候都需要去注册中心拉取相关服务列表吗
//                //      我们如何合理的选择一个可用的服务 而不是只获取第一个
//                InetSocketAddress address = registry.lookup(interfaceRef.getName());
//                if (log.isDebugEnabled()) {
//                    log.debug("服务调用方 发现了服务【{}】的可用主机【{}】", interfaceRef.getName(), address);
//                }
//
//
//                // 2。使用netty连接服务器 发送调用的服务的名字 ➕方法名字 加参数列表 得到结果
//                // 定义线程池，EventLoopGroup
//                // q.整个连接过程放在这里行不行？也就意味着每次调用都会产生一个新的netty连接 如何缓存连接
//                //  也就意味着 每次在此处建立一个新的连接是不合适的
//
//
//                // 解决方案？ 缓存channel 尝试从缓存中获取channel 如果未获取则 创建新的连接并进行缓存
//                //  1, 尝试从全局的缓存中获取一个通道
//                /***
//                 * address
//                 */
//
//                Channel channel = YrpcBootstrap.CHANNEL_CACHE.get(address);
//
//                if (channel == null) {
//                    //  建立一个新的channel
//
//                    // await 方法会阻塞 会等待 连接成功再返回 netty 提供了异步处理逻辑
//                    // sync 和await 都是阻塞当前线程 获取返回值 连接的过程是异步的 发送数据的过程是异步的
//                    //  如果发生了异常 sync 会主动在主线程抛出异常 await 不会  异常在子线程中处理 需要使用future中处理
////                    channel = NettyBootstrapInitializer.getBootstrap()
////                            .connect(address).sync().await().channel();
//
//                    //  使用addListener 执行异步操作
//                    CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
//
//                    NettyBootstrapInitializer.getBootstrap().connect(address).addListener(
//                            (ChannelFutureListener) promise -> {
//                                if (promise.isDone()) {
//                                    // 异步的  我们已经完成
//                                    if (log.isDebugEnabled()) {
//                                        log.debug("已经和【{}】成功建立了连接", address);
//                                    }
//                                    channelFuture.complete(promise.channel());
//                                } else if (!promise.isSuccess()) {
//                                    channelFuture.completeExceptionally(promise.cause());
//                                }
//                            }
//                    );
//
//                    //  阻塞获取channel
//                    channel = channelFuture.get(3, TimeUnit.SECONDS);
//
//
//                    //  如何等待
//
//                    // 缓存channel
//                    YrpcBootstrap.CHANNEL_CACHE.put(address, channel);
//                }
//                if (channel == null) {
//                    log.error("获取或建立与【{}】的通道时发生了异常", address);
//                    throw new NetworkException("获取通道时发生了异常。");
//                }
//
//                /**
//                 * --------------封装报文------------
//                 */
//                /**
//                 * --------------同步策略---------------------
//                 */
////                ChannelFuture channelFuture = channel.writeAndFlush(new Object());
////                // 需要学习channelFuture的一些简单api
////                // get 阻赛获取结果 getNow 获取当前的结果 如果未处理完成 返回null
////
////                if(channelFuture.isDone()){
////                    Object object = channelFuture.getNow();
////                } else if(!channelFuture.isSuccess()){
////                    //  需要捕获异常 可以捕获异步任务中的异常
////                    Throwable cause = channelFuture.cause();
////                    throw new RuntimeException(cause);
////                }
//
//
//                /**
//                 * -------------异步策略-------------
//                 */
//
//                // todo 需要将completableFuture 暴露出去
//
//                CompletableFuture<Object> completableFuture = new CompletableFuture<>();
//                YrpcBootstrap.PENDING_REQUEST.put(1L,completableFuture);
//                channel.writeAndFlush(Unpooled.copiedBuffer("hello".getBytes(StandardCharsets.UTF_8))).addListener((ChannelFutureListener) promise -> {
//                    //  当前的promise 将来返回的结果是什么？ writAndFLush的返回结果
//                    //  一旦数据被写回去 这个promise 也就结束了
//                    //  但是我们想要的是什么？ 服务端给我们的返回值 所以这里处理completableFuture是有问题的
//                    //  是不是应该将completableFuture 挂起并且暴露 并且在得到服务提供方的响应的时候调用
//                    // complete方法
////                    if(promise.isDone()){
////                        completableFuture.complete(promise.getNow());
////                    }
//                    if (!promise.isSuccess()) {
//                        completableFuture.completeExceptionally(promise.cause());
//                    }
//                });
//
//
//                //System.out.println("hello proxy");
//                //return completableFuture.get(3, TimeUnit.SECONDS);
//                // 如果没有地方处理这个competableFuture 会阻塞等待
//                // q 我们需要在哪里调用complete方法 得到结果 很明显 pipeline中的最终的handler处理结果
//                return completableFuture.get(3,TimeUnit.SECONDS);
//            }
//        });
//        return (T) helloProxy;
    }


    public Class<T> getInterfaceRef() {
        return interfaceRef;
    }

    public void setInterfaceRef(Class<T> interfaceRef) {
        this.interfaceRef = interfaceRef;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
}
