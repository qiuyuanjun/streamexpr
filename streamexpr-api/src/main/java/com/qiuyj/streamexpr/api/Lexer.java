package com.qiuyj.streamexpr.api;

/**
 * 词法解析器
 * @author qiuyj
 * @since 2023-06-29
 */
public interface Lexer {

    /**
     * 得到下一个词法单元，即{@link Token}对象
     * @return {@link Token}对象
     */
    Token nextToken();

    /**
     * 返回前一个词法单元，即{@link Token}对象
     * @return {@link Token}对象
     */
    Token getPrevToken();

    /**
     * 预读一个词法单元并返回
     * @return 预读的词法单元 {@link Token}对象
     */
    Token lookahead();
}
