package com.simple;

import lombok.extern.slf4j.Slf4j;
import main.java.com.simple.Constant;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 注册中心的管理页面
 *
 * @author Hongbin BAO
 * @Date 2024/1/6 20:28
 */
@Slf4j
public class Application {

    public static void main(String[] args) {
        // 帮我们创建基础目录
        // yrpc-metadata   (持久节点)
        //  └─ providers （持久节点）
        //  		└─ service1  （持久节点，接口的全限定名）
        //  		    ├─ node1 [data]     /ip:port
        //  		    ├─ node2 [data]
        //            └─ node3 [data]
        //  └─ consumers
        //        └─ service1
        //             ├─ node1 [data]
        //             ├─ node2 [data]
        //             └─ node3 [data]
        //  └─ config


        // 创建一个zookeeper实例

        ZooKeeper zooKeeper;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        // 定义连接参数
        String connectString = Constant.DEFAULT_ZK_CONNECT;
        //  定义超时时间
        int timeout = Constant.TIME_OUT;


        try {
            // 创建zookeeper实例 建立连接
            zooKeeper = new ZooKeeper(connectString, timeout, event -> {
                // 只有连接成功才放行
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    System.out.println("客户端已经连接成功。");
                    countDownLatch.countDown();
                }
            });


            countDownLatch.await();
            // 定义节点和数据

            String basePath = "/yrpc-metadate";

            String providePath = basePath + "/providers";

            String consumersPath = basePath + "/consumers";

             if(zooKeeper.exists(basePath,null) == null){
                 String result = zooKeeper.create(basePath, null,
                         ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                 log.info("根结点【{}】成功创建",result);
             }

            if(zooKeeper.exists(providePath,null) == null){
                String result = zooKeeper.create(providePath, null,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                log.info("pro结点【{}】成功创建",result);
            }

            if(zooKeeper.exists(consumersPath,null) == null){
                String result = zooKeeper.create(consumersPath, null,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

                log.info("com结点【{}】成功创建",result);
            }



        } catch (IOException | InterruptedException e) {
            log.error("创建基础目录时候产生异常zookeeper");
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        }


//        ZooKeeper zooKeeper = ZookeeperUtils.createZookeeper();
//
//        // 定义节点和数据
//        String basePath = "/yrpc-metadata";
//        String providerPath = basePath + "/providers";
//        String consumersPath = basePath + "/consumers";
//        ZookeeperNode baseNode = new ZookeeperNode(basePath, null);
//        ZookeeperNode providersNode = new ZookeeperNode(providerPath, null);
//        ZookeeperNode consumersNode = new ZookeeperNode(consumersPath, null);
//
//        // 创建节点
//        List.of(baseNode, providersNode, consumersNode).forEach(node -> {
//            ZookeeperUtils.createNode(zooKeeper,node,null, CreateMode.PERSISTENT);
//        });
//
//        // 关闭连接
//        ZookeeperUtils.close(zooKeeper);
    }
}
