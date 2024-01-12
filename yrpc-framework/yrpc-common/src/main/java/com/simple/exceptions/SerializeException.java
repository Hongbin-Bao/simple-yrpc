package com.simple.exceptions;

/**
 * @author Hongbin BAO
 * @Date 2024/1/11 20:30
 */
public class SerializeException extends RuntimeException{

    public SerializeException() {
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
