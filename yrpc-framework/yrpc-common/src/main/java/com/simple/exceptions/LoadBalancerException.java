package com.simple.exceptions;

/**
 * @author Hongbin BAO
 * @Date 2024/1/13 19:24
 */
public class LoadBalancerException extends RuntimeException {

    public LoadBalancerException(String message) {
        super(message);
    }

    public LoadBalancerException() {
    }
}
