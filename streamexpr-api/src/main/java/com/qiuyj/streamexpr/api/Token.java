package com.qiuyj.streamexpr.api;

/**
 * 代表词法解析阶段解析的词法单元
 * @author qiuyj
 * @since 2023-06-29
 */
public class Token {

    /**
     * 原始字符串
     */
    private final String sourceString;

    /**
     * 起始下标
     */
    private final int startPos;

    /**
     * {@link TokenKind}子类
     */
    private final TokenKind kind;

    public Token(String sourceString, int startPos, TokenKind kind) {
        this.sourceString = sourceString;
        this.startPos = startPos;
        this.kind = kind;
    }

    public String getSourceString() {
        return sourceString;
    }

    public int getStartPos() {
        return startPos;
    }

    public TokenKind getKind() {
        return kind;
    }
}
