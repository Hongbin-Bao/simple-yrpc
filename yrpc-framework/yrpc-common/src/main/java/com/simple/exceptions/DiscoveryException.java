package com.simple.exceptions;

/**
 * @author Hongbin BAO
 * @Date 2024/1/7 23:48
 */
public class DiscoveryException extends RuntimeException{
    public DiscoveryException() {
    }
    public DiscoveryException(Throwable cause){ super(cause);}

    public DiscoveryException(String message) {
        super(message);
    }
}
