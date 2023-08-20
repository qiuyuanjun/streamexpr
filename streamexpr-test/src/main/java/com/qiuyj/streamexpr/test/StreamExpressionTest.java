package com.qiuyj.streamexpr.test;

import com.qiuyj.streamexpr.StreamExpression;

/**
 * @author qiuyj
 * @since 2023-07-30
 */
public class StreamExpressionTest {

    public static void main(String[] args) {
        StreamExpression.parse("filter   ( @_CTX_PARAM_1== #{CONSTANT_FIVE_00} || #{ T(Function5).hasRetry() } )|filter(@CTX_PARAM_FIRST, #{ T(Function2).test() }, [ @CTX_PARAM_1, @CTX_PARAM_2 ])|filter(com.qiuyj.stream.expr.api.ArrayUtils.conditionIsTrue(param1, param2))|toArray(#{ collectFunction() })");
    }
}
