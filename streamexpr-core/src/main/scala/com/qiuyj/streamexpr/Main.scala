package com.qiuyj.streamexpr

import com.qiuyj.streamexpr.api.{Token, TokenKind}
import com.qiuyj.streamexpr.parser.StreamExpressionScanner

/**
 * @author qiuyj
 * @since 2023-06-29
 */
object Main extends App {

  private[this] val lexer = new StreamExpressionScanner("jsonToList(CONTENT|PBC_RESULT|pbc_lcard_info)|filter(#{TOTAL_NUM + 10 * PAY_ACCOUNT} gteq 100)|toArray(_)")
  var token: Token = _
  do {
    token = lexer.nextToken
  }
  while (token.getKind.getTag != TokenKind.TAG_EOF)
}
