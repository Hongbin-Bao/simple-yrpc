package com.simple.exceptions;

/**
 * @author Hongbin BAO
 * @Date 2024/1/13 17:13
 */
public class CompressException extends RuntimeException{

    public CompressException() {
    }

    public CompressException(String message) {
        super(message);
    }

    public CompressException(Throwable cause) {
        super(cause);
    }
}
