package com.simple.loadbalancer.imlp;

import com.simple.YrpcBootstrap;
import com.simple.loadbalancer.AbstractLoadBalancer;
import com.simple.loadbalancer.Selector;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * @author Hongbin BAO
 * @Date 2024/1/14 14:06
 */
public class MinimumResponseTimeLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Selector getSelector(List<InetSocketAddress> serviceList) {
        return new MinimumResponseTimeSelector(serviceList);
    }

    private static class MinimumResponseTimeSelector implements Selector {

        public MinimumResponseTimeSelector(List<InetSocketAddress> serviceList) {

        }

        @Override
        public InetSocketAddress getNext() {
            Map.Entry<Long, Channel> entry = YrpcBootstrap.ANSWER_TIME_CHANNEL_CACHE.firstEntry();
            if (entry != null) {
                return (InetSocketAddress) entry.getValue().remoteAddress();
            }

            // 直接从缓存中获取一个可用的就行了
            Channel channel = (Channel)YrpcBootstrap.CHANNEL_CACHE.values().toArray()[0];
            return (InetSocketAddress)channel.remoteAddress();
        }

        @Override
        public void reBalance() {

        }


    }
}

