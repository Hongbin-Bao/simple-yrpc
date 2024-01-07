package com.simple.exceptions;

/**
 * @author Hongbin BAO
 * @Date 2024/1/7 22:35
 */
public class NetworkException extends RuntimeException{
    public NetworkException() {
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }
}
