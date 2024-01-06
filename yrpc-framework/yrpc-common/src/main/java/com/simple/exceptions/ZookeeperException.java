package com.simple.exceptions;

/**
 * @author Hongbin BAO
 * @Date 2024/1/6 21:47
 */
public class ZookeeperException extends RuntimeException{
    public ZookeeperException() {
    }

    public ZookeeperException(Throwable cause) {
        super(cause);
    }
}
