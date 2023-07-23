package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.{Parameter, StreamOp}
import com.qiuyj.streamexpr.api._
import com.qiuyj.streamexpr.utils.ParseUtils

/**
 * @author qiuyj
 * @since 2023-06-29
 */
class StreamExpressionParser(private[this] val lexer: Lexer) extends Parser[StreamExpression] {

  override def parseExpression: StreamExpression = {
    val streamOpSeparator = TokenKinds.getInstance getTokenKindByName "|"
    val streamExpression = new StreamExpression
    lexer.nextToken
    do {
      streamExpression addStreamOp parseStreamOp
    }
    while (`match`(streamOpSeparator))
    if (lexer.nextToken.getKind.getTag != TokenKind.TAG_EOF) {
      parseError("")
    }
    streamExpression
  }

  private def parseStreamOp: StreamOp = {
    val tokenKinds = TokenKinds.getInstance
    val opName = lexer.getCurrentToken
    accept(tokenKinds getTokenKindByTag TokenKind.TAG_IDENTIFIER)
    accept(tokenKinds getTokenKindByName "(")
    // 解析参数
    val parameterSeparator = tokenKinds getTokenKindByName ","
    do {
      parseParameter
    }
    while (`match`(parameterSeparator))
    lexer.nextToken
    accept(tokenKinds getTokenKindByName ")")
    new StreamOp
  }

  private def parseParameter: Parameter = {
    new Parameter
  }

  private def accept(kind: TokenKind): Unit = {
    if (!`match`(kind)) {
      parseError(s"Unexpected token kind, expect: $kind, but find: ${lexer.getCurrentToken.getKind}")
    }
  }

  private def `match`(kind: TokenKind): Boolean = {
    if (lexer.getCurrentToken.getKind equals kind) {
      lexer.nextToken
      true
    }
    else
      false
  }

  private def parseError(errorMessage: String): Unit =
    throw new ParserException(errorMessage)
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
