package com.qiuyj.streamexpr.api;

/**
 * @author qiuyj
 * @since 2023-06-29
 */
public interface Expression<T extends EvaluationContext> {

    Object evaluate();

    Object evaluate(T context);
}
