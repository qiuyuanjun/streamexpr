package com.qiuyj.streamexpr.test;

import com.qiuyj.streamexpr.StreamExpression;

/**
 * @author qiuyj
 * @since 2023-07-30
 */
public class StreamExpressionTest {

    public static void main(String[] args) {
        StreamExpression.parse("filter(#{PAY_ACCOUNT EQ '12384571'} || 100)");
    }
}
