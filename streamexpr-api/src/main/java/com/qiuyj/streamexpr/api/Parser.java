package com.qiuyj.streamexpr.api;

/**
 * 语法解析器
 * @author qiuyj
 * @since 2023-06-29
 */
public interface Parser<T extends Expression> {

    T parseExpression();
}
