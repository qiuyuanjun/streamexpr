package com.qiuyj.streamexpr.api;

/**
 * @author qiuyj
 * @since 2023-07-03
 */
public enum TokenKindImpl implements TokenKind {
    /**
     * 标识符
     */
    IDENTIFIER(TokenKind.TAG_IDENTIFIER),
    /**
     * 字符串字面量
     */
    STRING_LITERAL(TokenKind.TAG_STRING_LITERAL),
    /**
     * double类型数字
     */
    DOUBLE_NUMERIC(TokenKind.TAG_DOUBLE_NUMERIC) {
        @Override
        public boolean isNumeric() {
            return true;
        }
    },
    /**
     * float类型数字
     */
    FLOAT_NUMERIC(TokenKind.TAG_FLOAT_NUMERIC) {
        @Override
        public boolean isNumeric() {
            return true;
        }
    },
    /**
     * long类型数字
     */
    LONG_NUMERIC(TokenKind.TAG_LONG_NUMERIC) {
        @Override
        public boolean isNumeric() {
            return true;
        }
    },
    /**
     * int类型数字
     */
    INTEGER_NUMERIC(TokenKind.TAG_INTEGER_NUMERIC) {
        @Override
        public boolean isNumeric() {
            return true;
        }
    },
    DOT("."),
    LPARAN("("),
    RPARAN(")"),
    COMMA(","),
    MINUS("-"),
    PLUS("+"),
    BAR("|"),
    EQEQ("=="),
    GT(">"),
    GTEQ(">="),
    LT("<"),
    LTEQ("<="),
    BARBAR("||"),
    AMPAMP("&&"),

    EOF(TokenKind.TAG_EOF);

    private final int tag;

    private final String name;

    TokenKindImpl(int tag) {
        this(tag, null);
    }

    TokenKindImpl(String name) {
        this(TokenKind.TAG_NOT_SUPPORT, name);
    }

    TokenKindImpl(int tag, String name) {
        this.tag = tag;
        this.name = name;
        TokenKinds.getInstance().registerTokenKind(this);
    }

    @Override
    public int getTag() {
        return tag;
    }

    @Override
    public String getName() {
        return name;
    }
}
