package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.api._
import com.qiuyj.streamexpr.api.ast.{ASTNode, IdentifierASTNode, StringLiteralASTNode}
import com.qiuyj.streamexpr.ast.{SPELASTNode, StreamExpressionASTNode, StreamExpressionVisitor, StreamOpASTNode}
import com.qiuyj.streamexpr.utils.ParseUtils

import scala.collection.mutable.ArrayBuffer

/**
 * @author qiuyj
 * @since 2023-06-29
 */
class StreamExpressionParser(private[this] val lexer: Lexer) extends Parser[StreamExpression] {

  /**
   * 用于存储预读的token
   */
  private[this] var lookaheadToken: Token = _

  override def parseExpression: StreamExpression = {
    val visitor = new StreamExpressionVisitor
    parseStreamExpression.visit[StreamExpressionVisitor](visitor)
    visitor.getStreamExpression
  }

  private def parseStreamExpression: StreamExpressionASTNode = {
    val streamOpSeparator = TokenKinds.getInstance getTokenKindByName "|"
    val streamExpressionBuilder = new StreamExpressionASTNode.Builder
    lexer.nextToken
    do {
      streamExpressionBuilder addStreamOp parseStreamOp
    }
    while (`match`(streamOpSeparator))
    // 最后不能再有多余的Token，必须是EOF
    nextThenAccept(TokenKinds.getInstance getTokenKindByTag TokenKind.TAG_EOF)
    streamExpressionBuilder.build
  }

  private def parseStreamOp: StreamOpASTNode = {
    val tokenKinds = TokenKinds.getInstance
    accept(tokenKinds getTokenKindByTag TokenKind.TAG_IDENTIFIER)
    val opName = new IdentifierASTNode(lexer.getPrevToken.getSourceString)
    accept(tokenKinds getTokenKindByName "(")
    val parameterSeparator = tokenKinds getTokenKindByName ","
    // 解析参数
    // 大多数情况参数不会超过5个
    val parameters = new ArrayBuffer[ASTNode](5)
    do {
      parameters += parseParameter
    }
    while (`match`(parameterSeparator))
    nextThenAccept(tokenKinds getTokenKindByName ")")
    new StreamOpASTNode(opName, parameters.toSeq: _*)
  }

  private def parseParameter: ASTNode = {
    val tokenKinds = TokenKinds.getInstance
    if (`match`(tokenKinds getTokenKindByTag TokenKind.TAG_IDENTIFIER)) {
      // lookahead，看下一个token是什么类型，根据其类型解析对应的
      lookaheadToken = lexer.lookahead
      if (lookaheadMatch(tokenKinds getTokenKindByName "|")) {
        // 属性级联调用
        parseNestedProperty
      }
      else if (lookaheadMatch(tokenKinds getTokenKindByName ",")) {
        // 函数
        lexer.nextToken
        null
      }
      else {
        // identifier
        new IdentifierASTNode(lexer.getCurrentToken.getSourceString)
      }
    }
    else if (`match`(StreamExpressionTokenKind.SPEL)) {
      new SPELASTNode(lexer.getPrevToken.getSourceString)
    }
    else if (`match`(tokenKinds getTokenKindByTag TokenKind.TAG_STRING_LITERAL)) {
      new StringLiteralASTNode(lexer.getPrevToken.getSourceString)
    }
    else {
      parseError("")
      throw new IllegalStateException("Never reach here")
    }
  }

  private def parseNestedProperty: ASTNode = {
    null
  }

  private def nextThenAccept(kind: TokenKind): Unit = {
    lexer.nextToken
    accept(kind)
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

  private def lookaheadMatch(kind: TokenKind): Boolean = {
    lookaheadToken.getKind equals kind
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
