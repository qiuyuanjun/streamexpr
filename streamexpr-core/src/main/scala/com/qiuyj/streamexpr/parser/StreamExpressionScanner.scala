package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.api.{Lexer, Token}

/**
 * @author qiuyj
 * @since 2023-06-29
 */
class StreamExpressionScanner(private[this] val sourceString: String) extends Lexer {

  private[this] val tokenizer = new StreamExpressionTokenizer(sourceString)

  override def nextToken: Token = {
    tokenizer.readToken
  }
}
