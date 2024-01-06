package com.simple.utils.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hongbin BAO
 * @Date 2024/1/6 21:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZookeeperNode {
    private String nodePath;
    private byte[] data;
}
