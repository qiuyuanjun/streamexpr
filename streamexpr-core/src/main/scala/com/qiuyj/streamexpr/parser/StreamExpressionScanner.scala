package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.api.{Lexer, Token}

import java.util.Objects

/**
 * @author qiuyj
 * @since 2023-06-29
 */
class StreamExpressionScanner(private[this] val sourceString: String) extends Lexer {

  private[this] val tokenizer = new StreamExpressionTokenizer(sourceString)

  /**
   * 用于存储上一个token
   */
  private[this] var prevToken: Token = _

  /**
   * 用于存储预读的下一个token
   */
  private[this] var lookaheadToken: Token = _

  /**
   * 用于存储当前的token
   */
  private[this] var currentToken: Token = _

  override def nextToken: Token = {
    if (Objects.nonNull(currentToken)) {
      prevToken = currentToken
    }
    if (Objects.nonNull(lookaheadToken)) {
      currentToken = lookaheadToken
      lookaheadToken = null
    }
    else {
      currentToken = tokenizer.readToken
    }
    currentToken
  }

  override def getPrevToken: Token = prevToken

  override def lookahead: Token = {
    if (Objects.nonNull(lookaheadToken)) {
      throw new IllegalStateException("After calling the 'lookahead' method, it is necessary to call the 'next' method in a timely manner")
    }
    lookaheadToken = tokenizer.readToken
    lookaheadToken
  }

}
