package com.qiuyj.streamexpr.api;

/**
 * 词法解析器
 * @author qiuyj
 * @since 2023-06-29
 */
public interface Lexer {

    Token nextToken();
}
