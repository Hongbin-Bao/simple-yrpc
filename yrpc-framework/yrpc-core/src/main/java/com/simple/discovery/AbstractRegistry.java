package com.simple.discovery;

import java.net.InetSocketAddress;
import java.util.List;

/**
 *
 * 提炼共享内容 还可以做模版方法
 * @author Hongbin BAO
 * @Date 2024/1/7 23:15
 */
public abstract class AbstractRegistry implements Registry{
    public abstract List<InetSocketAddress> lookup(String name, String group);
}
