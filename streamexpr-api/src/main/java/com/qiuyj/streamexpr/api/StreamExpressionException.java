package com.qiuyj.streamexpr.api;

/**
 * @author qiuyj
 * @since 2023-07-07
 */
public class StreamExpressionException extends RuntimeException {

    public StreamExpressionException(String message) {
        super(message);
    }

    public StreamExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
