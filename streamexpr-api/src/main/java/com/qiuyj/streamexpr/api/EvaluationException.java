package com.qiuyj.streamexpr.api;

/**
 * 求值异常
 * @author qiuyj
 * @since 2023-09-10
 */
public class EvaluationException extends RuntimeException {

    public EvaluationException(String errorMessage) {
        super(errorMessage);
    }
}
