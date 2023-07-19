package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.api.{Lexer, Parser, Token, TokenKind}
import com.qiuyj.streamexpr.utils.ParseUtils

/**
 * @author qiuyj
 * @since 2023-06-29
 */
class StreamExpressionParser(private[this] val lexer: Lexer) extends Parser[StreamExpression] {

  override def parseExpression: StreamExpression = {
    var token: Token = null
    do {
      token = lexer.nextToken
    }
    while (token.getKind.getTag != TokenKind.TAG_EOF)
    null
  }
}

object StreamExpressionParser {

  def parse(streamExpr: String, allowEmptyStreamExpression: Boolean = false): StreamExpression = {
    if (ParseUtils.stringIsNotBlank(streamExpr)) {
      new StreamExpressionParser(new StreamExpressionScanner(streamExpr)).parseExpression
    }
    else if (allowEmptyStreamExpression) null
    else {
      throw new IllegalArgumentException("The streamExpr can not be null or blank")
    }
  }
}
