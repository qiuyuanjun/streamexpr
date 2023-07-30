package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.api._
import com.qiuyj.streamexpr.api.ast._
import com.qiuyj.streamexpr.ast.{StreamExpressionASTNode, StreamExpressionVisitor, StreamOpASTNode}
import com.qiuyj.streamexpr.utils.ParseUtils

import java.util.Objects
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
    lexer.nextToken
    parseStreamExpression.visit[StreamExpressionVisitor](visitor)
    visitor.getStreamExpression
  }

  /*
   * StreamExpression: StreamOp ( BAR StreamOp )*
   * BAR: "|"
   */
  private def parseStreamExpression: StreamExpressionASTNode = {
    val streamOpSeparator = TokenKinds.getInstance getTokenKindByName "|"
    val streamExpressionBuilder = new StreamExpressionASTNode.Builder
//    streamExpressionBuilder addStreamOp parseStreamOp
//    while (`match`(streamOpSeparator)) {
//      streamExpressionBuilder addStreamOp parseStreamOp
//    }
    // 下面代码比上面代码更加简洁
    do {
      streamExpressionBuilder addStreamOp parseStreamOp
    }
    while (`match`(streamOpSeparator))
    // 最后不能再有多余的Token，必须是EOF
    accept(TokenKinds.getInstance getTokenKindByTag TokenKind.TAG_EOF)
    streamExpressionBuilder.build
  }

  /*
   * StreamOp: OpName LPARAN Parameter ( COMMA Parameter )* RPARAN
   * LPARAN: "("
   * RPARAN: ")"
   * COMMA: ","
   * OpName: Identifier
   * Identifier: IdentifierStart (IdentifierPart)*
   */
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
    accept(tokenKinds getTokenKindByName ")")
    new StreamOpASTNode(opName, parameters.toIndexedSeq: _*)
  }

  /*
   * Parameter: Expr
   */
  private def parseParameter: ASTNode = parseExpr

  /*
   * Expr: OrExpr
   */
  private def parseExpr: ExpressionASTNode = {
    val left: ASTNode = parseOrExpr
    null
  }

  /*
   * OrExpr: AndExpr ( Or AndExpr )*
   * Or: BARBAR | OR
   * BARBAR: "||"
   * OR: "or" | "OR"
   */
  private def parseOrExpr: ASTNode = {
    val tokenKinds = TokenKinds.getInstance
    val first: ASTNode = parseAndExpr
    var orPart: ArrayBuffer[ExpressionASTNode] = null
    while (`match`(tokenKinds getTokenKindByName ("||"))
      || `match`(tokenKinds getTokenKindByName "or")
      || `match`(tokenKinds getTokenKindByName "OR")) {
      if (Objects.isNull(orPart)) {
        orPart = new ArrayBuffer[ExpressionASTNode](4)
      }
      orPart += parseAndExpr.asInstanceOf[ExpressionASTNode]
    }
    if (orPart.isEmpty)
      first
    else
      new OrExpressionASTNode(AbstractASTNode.makeArray[ExpressionASTNode](classOf[ExpressionASTNode], first.asInstanceOf[ExpressionASTNode], orPart.toArray: _*): _*)
  }

  /*
   * AndExpr: RelationExpr ( And RelationExpr )*
   * And: AMPAMP | AND
   * AMPAMP: "&&"
   * AND: "and" | "AND"
   */
  private def parseAndExpr: ASTNode = {
    val tokenKinds = TokenKinds.getInstance
    val first: ASTNode = parseRelationExpr
    var andPart: ArrayBuffer[ExpressionASTNode] = null
    while (`match`(tokenKinds getTokenKindByName ("&&"))
      || `match`(tokenKinds getTokenKindByName "and")
      || `match`(tokenKinds getTokenKindByName "AND")) {
      if (Objects.isNull(andPart)) {
        andPart = new ArrayBuffer[ExpressionASTNode](4)
      }
      andPart += parseRelationExpr.asInstanceOf[ExpressionASTNode]
    }
    if (andPart.isEmpty)
      first
    else
      new AndExpressionASTNode(AbstractASTNode.makeArray[ExpressionASTNode](classOf[ExpressionASTNode], first.asInstanceOf[ExpressionASTNode], andPart.toArray: _*): _*)
  }

  private def parseRelationExpr: ASTNode = {
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
