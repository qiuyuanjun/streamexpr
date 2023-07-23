package com.qiuyj.streamexpr.api;

/**
 * @author qiuyj
 * @since 2023-07-03
 */
public interface TokenKind {

    int TAG_NOT_SUPPORT = -99;
    int TAG_EOF = -1;
    int TAG_IDENTIFIER = 0;
    int TAG_STRING_LITERAL = 1;
    int TAG_DOUBLE_NUMERIC = 2;
    int TAG_FLOAT_NUMERIC = 3;
    int TAG_LONG_NUMERIC = 4;
    int TAG_INTEGER_NUMERIC = 5;

    int getTag();

    String getName();

    default boolean isNumeric() {
        return false;
    }

    default boolean isKeyword() {
        return false;
    }

    default boolean equals(TokenKind other) {
        return this == other || other.getTag() == getTag();
    }
}
