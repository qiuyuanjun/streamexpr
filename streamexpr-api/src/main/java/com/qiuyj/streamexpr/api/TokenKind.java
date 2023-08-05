package com.qiuyj.streamexpr.api;

import com.qiuyj.streamexpr.api.utils.StringUtils;

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

    /**
     * 判断是否是关系运算符
     * @return 如果是关系运算符，那么返回{@code true}，否则返回{@code false}
     */
    boolean isRelationOperator();

    default boolean isNumeric() {
        return false;
    }

    default boolean isKeyword() {
        return false;
    }

    default boolean isNamed() {
        return false;
    }

    default boolean equals(TokenKind other) {
        if (this == other) {
            return true;
        }
        if (getTag() != TokenKind.TAG_NOT_SUPPORT
                && other.getTag() != TokenKind.TAG_NOT_SUPPORT) {
            return other.getTag() == getTag();
        }
        return StringUtils.isNotEmpty(getName())
                && getName().equals(other.getName());
    }
}
