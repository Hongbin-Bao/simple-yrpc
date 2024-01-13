package com.simple;

import com.simple.channelHandler.Handler.MethodCallHandler;
import com.simple.channelHandler.Handler.YrpcRequestDecoder;
import com.simple.channelHandler.Handler.YrpcResponseEncoder;
import com.simple.discovery.Registry;
import com.simple.discovery.RegistryConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hongbin BAO
 * @Date 2024/1/5 20:19
 */
@Slf4j
public class YrpcBootstrap {


    // YrpcBootstrap是个单例，我们希望每个应用程序只有一个实例
    private static final YrpcBootstrap yrpcBootstrap = new YrpcBootstrap();

    // 定义相关的一些基础配置
    private String appName = "default";
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private int port = 8088;

    public static final IdGenerator ID_GENERATOR = new IdGenerator(1,2);
    // 注册中心
    private Registry registry;

    // 连接的缓存,如果使用InetSocketAddress这样的类做key，一定要看他有没有重写equals方法和toString方法
    public final static Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>(16);

    // 维护已经发布且暴露的服务列表 key-> interface的全限定名  value -> ServiceConfig
    public final static Map<String,ServiceConfig<?>> SERVERS_LIST = new ConcurrentHashMap<>(16);

    // 定义全局的对外挂起的 completableFuture
    public final static Map<Long, CompletableFuture<Object>> PENDING_REQUEST = new ConcurrentHashMap<>(128);

    public static String SERIALIZE_TYPE = "jdk";

    public static  String COMPRESS_TYPE = "gzip";


    // 维护一个zookeeper实例
//    private ZooKeeper zooKeeper;

    private YrpcBootstrap() {
        // 构造启动引导程序，时需要做一些什么初始化的事
    }

    public static YrpcBootstrap getInstance() {
        return yrpcBootstrap;
    }

    /**
     * 用来定义当前应用的名字
     * @param appName 应用的名字
     * @return this当前实例
     */
    public YrpcBootstrap application(String appName) {
        this.appName = appName;
        return this;
    }

    /**
     * 用来配置一个注册中心
     * @param registryConfig 注册中心
     * @return this当前实例
     */
    public YrpcBootstrap registry(RegistryConfig registryConfig) {
        // 这里维护一个zookeeper实例，但是，如果这样写就会将zookeeper和当前工程耦合
        // 我们其实是更希望以后可以扩展更多种不同的实现

        // 尝试使用 registryConfig 获取一个注册中心，有点工厂设计模式的意思了
        this.registry = registryConfig.getRegistry();
        return this;
    }

    /**
     * 配置当前暴露的服务使用的协议
     * @param protocolConfig 协议的封装
     * @return this当前实例
     */
    public YrpcBootstrap protocol(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
        if(log.isDebugEnabled()){
            log.debug("当前工程使用了：{}协议进行序列化",protocolConfig.toString());
        }
        return this;
    }


    /**
     * ---------------------------服务提供方的相关api---------------------------------
     */

    /**
     * 发布服务，将接口-》实现，注册到服务中心
     * @param service 封装的需要发布的服务
     * @return this当前实例
     */
    public YrpcBootstrap publish(ServiceConfig<?> service) {
        // 我们抽象了注册中心的概念，使用注册中心的一个实现完成注册
        // 有人会想，此时此刻难道不是强耦合了吗？
        registry.register(service);

        // 1、当服务调用方，通过接口、方法名、具体的方法参数列表发起调用，提供怎么知道使用哪一个实现
        // (1) new 一个  （2）spring beanFactory.getBean(Class)  (3) 自己维护映射关系
        SERVERS_LIST.put(service.getInterface().getName(),service);
        return this;
    }

    /**
     * 批量发布
     * @param services 封装的需要发布的服务集合
     * @return this当前实例
     */
    public YrpcBootstrap publish(List<ServiceConfig<?>> services) {
        for (ServiceConfig<?> service : services) {
            this.publish(service);
        }
        return this;
    }

    /**
     * 启动netty服务
     */
    public void start() {
        // 1、创建eventLoop，老板只负责处理请求，之后会将请求分发至worker
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        try {

            // 2、需要一个服务器引导程序
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 3、配置服务器
            serverBootstrap = serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 是核心，我们需要添加很多入站和出站的handler
                            socketChannel.pipeline().addLast(new LoggingHandler())
                                    .addLast(new YrpcRequestDecoder())
                                    // 根据请求进行方法调用
                                    .addLast(new MethodCallHandler())
                                    .addLast(new YrpcResponseEncoder());
                        }
                    });

            // 4、绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * ---------------------------服务调用方的相关api---------------------------------
     */
    public YrpcBootstrap reference(ReferenceConfig<?> reference) {

        // 在这个方法里我们是否可以拿到相关的配置项-注册中心
        // 配置reference，将来调用get方法时，方便生成代理对象
        // 1、reference需要一个注册中心
        reference.setRegistry(registry);
        return this;
    }

    /**
     * 配置序列化的方式
     * @param serializeType 序列化的方式
     */
    public YrpcBootstrap serialize(String serializeType) {
        SERIALIZE_TYPE = serializeType;
        if(log.isDebugEnabled()){
            log.debug("我们配置了使用的序列化的方式为【{}】",serializeType);
        }
        return this;
    }


    public YrpcBootstrap compress(String compressType) {
        COMPRESS_TYPE = compressType;
        if(log.isDebugEnabled()){
            log.debug("我们配置了使用的压缩算法为【{}】.",compressType);
        }
        return this;
    }

    public Registry getRegistry() {
        return registry;
    }
}
