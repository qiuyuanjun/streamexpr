package com.qiuyj.streamexpr

/**
 * @author qiuyj
 * @since 2023-06-29
 */
object Main extends App {

  StreamExpression.parse("jsonToList(CONTENT|PBC_RESULT|pbc_lcard_info)|filter(#{TOTAL_NUM + 10 * PAY_ACCOUNT} gteq 100)|toArray(_)")
}
