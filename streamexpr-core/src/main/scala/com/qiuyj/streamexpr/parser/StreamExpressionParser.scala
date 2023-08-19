package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.api._
import com.qiuyj.streamexpr.api.ast.OperatorASTNode.Operator
import com.qiuyj.streamexpr.api.ast._
import com.qiuyj.streamexpr.api.utils.ArrayUtils
import com.qiuyj.streamexpr.ast._
import com.qiuyj.streamexpr.parser.StreamExpressionParser.ConstructNodeHelper
import com.qiuyj.streamexpr.utils.ParseUtils

import java.util
import scala.collection.immutable.ArraySeq

/**
 * @author qiuyj
 * @since 2023-06-29
 */
class StreamExpressionParser(private[this] val lexer: Lexer) extends Parser[StreamExpression] {

  /**
   * 用于存储抽象语法树中间构造节点
   */
  private[this] val constructNodeHelper: ConstructNodeHelper = new ConstructNodeHelper

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
    accept(TokenKinds.getInstance getTokenKindByTag TokenKind.TAG_IDENTIFIER)
    val opName = new IdentifierASTNode(lexer.getPrevToken.getSourceString)
    accept("(")
    // 解析参数
    constructNodeHelper.start()
    do {
      constructNodeHelper.enqueue(parseParameter)
    }
    while (`match`(","))
    accept(")")
    new StreamOpASTNode(opName, constructNodeHelper.makeSeq: _*)
  }

  /*
   * Parameter: Expr
   */
  private def parseParameter: ASTNode = parseExpr

  /*
   * Expr: OrExpr
   */
  private def parseExpr: ASTNode = {
    var expr = parseOrExpr
    if (`match`("?")) {
      // 3元运算符
      val trueResult = parseExpr
      accept(":")
      val falseResult = parseExpr
      expr = new ConditionalExpressionASTNode(expr, trueResult, falseResult)
    }
    expr
  }

  /*
   * OrExpr: AndExpr ( Or AndExpr )*
   * Or: BARBAR | OR
   * BARBAR: "||"
   * OR: "or" | "OR"
   */
  private def parseOrExpr: ASTNode = {
    val tokenKinds = TokenKinds.getInstance
    var left = parseAndExpr
    while (`match`(tokenKinds getTokenKindByName "||")
      || `match`(tokenKinds getTokenKindByName "or")
      || `match`(tokenKinds getTokenKindByName "OR")) {
      left = new OrExpressionASTNode(left, parseAndExpr)
    }
    left
  }

  /*
   * AndExpr: RelationExpr ( And RelationExpr )*
   * And: AMPAMP | AND
   * AMPAMP: "&&"
   * AND: "and" | "AND"
   */
  private def parseAndExpr: ASTNode = {
    val tokenKinds = TokenKinds.getInstance
    var left = parseRelationExpr
    while (`match`(tokenKinds getTokenKindByName "&&")
      || `match`(tokenKinds getTokenKindByName "and")
      || `match`(tokenKinds getTokenKindByName "AND")) {
      left = new AndExpressionASTNode(left, parseRelationExpr)
    }
    left
  }

  /*
   * RelationExpr: AddSubExpr RelOp AddSubExpr
   * RelOp: EQEQ | NEQ | GT | GTEQ | LT | LTEQ | "EQ" | "eq" | "NEQ" | "neq" | "GTEQ" | "gteq" | "GT" | "gt" | "LT" | "lt" | "LTEQ" | "lteq"
   * EQEQ: "=="
   * NEQ: "!="
   * GT: ">"
   * GTEQ: ">="
   * LT: "<"
   * LTEQ: "<="
   */
  private def parseRelationExpr: ASTNode = {
    val left = parseAddSubExpr
    // 判断是否是RelOp
    if (isRelOp) {
      lexer.nextToken
      Operator.getByName(lexer.getPrevToken.getKind.getName) match {
        case op: Operator => op.createOperatorASTNode(left, parseAddSubExpr)
        case _ =>
          parseError(s"Unsupported relational operator ${lexer.getPrevToken.getKind.getName}")
          throw new IllegalStateException("Never reach here!")
      }
    }
    else
      left
  }

  /*
   * AddSubExpr: MultiDivExpr ( PLUS | MINUS ) MultiDivExpr
   * PLUS: "+"
   * MINUS: "-"
   */
  private def parseAddSubExpr: ASTNode = {
    val left = parseMultiDivExpr
    val tokenKinds = TokenKinds.getInstance
    if (`match`(tokenKinds getTokenKindByName "+"))
      new PlusExpressionASTNode(left, parseMultiDivExpr)
    else if (`match`(tokenKinds getTokenKindByName "-"))
      new MinusExpressionASTNode(left, parseMultiDivExpr)
    else
      left
  }

  /*
   * MultiDivExpr: PrimaryExpr ( MULTI | DIV | MOD ) PrimaryExpr
   * MULTI: "*"
   * DIV: "/"
   * MOD: "%"
   */
  private def parseMultiDivExpr: ASTNode = {
    val left = parsePrimaryExpr
    if (`match`("*"))
      new MultiExpressionASTNode(left, parsePrimaryExpr)
    else if (`match`("/"))
      new DivExpressionASTNode(left, parsePrimaryExpr)
    else if (`match`("%"))
      new ModExpressionASTNode(left, parsePrimaryExpr)
    else
      left
  }

  /*
   * PrimaryExpr: ParanExpr
   *   | ContextAttribute
   *   | SpelExpr
   *   | ArrayExpr
   *   | PrefixExpr
   *   | PostfixExpr
   *   | NestedPropertyAccessor
   *   | FunctionCall
   *   | Identifier
   *   | Numeric
   */
  private def parsePrimaryExpr: ASTNode = {
    if (maybeParanExpr
      || maybeContextAttribute
      || maybeSpelExpression
      || maybePrefixExpr
      || maybeIdentifierRelated)
      constructNodeHelper.pop
    else if (maybeArrayExpr)
      new ArrayExpression(constructNodeHelper.makeArray)
    else
      null
  }

  /*
   * NestedPropertyAccessor: Identifier ( BAR Identifier )+
   */
  private def maybeNestedPropertyAccessor: Boolean = {
    val prev = lexer.getPrevToken
    if (`match`("|")) {
      constructNodeHelper.start()
      constructNodeHelper.enqueue(new IdentifierASTNode(prev.getSourceString))
      do {
        constructNodeHelper.enqueue(parseIdentifier)
      }
      while (`match`("|"))
      constructNodeHelper.push(new NestedPropertyAccessorASTNode(constructNodeHelper.makeArray))
      true
    }
    else
      false
  }

  private def maybePostfixExpr: Boolean = {
    val prev = lexer.getPrevToken
    if (`match`("++") || `match`("--")) {
      Operator.getByName(lexer.getPrevToken.getKind.getName) match {
        case operator: Operator =>
          constructNodeHelper.push(operator.createOperatorASTNode(new IdentifierASTNode(prev.getSourceString), null, false))
        case _ =>
          parseError(s"Unsupported postfix operator ${lexer.getPrevToken.getKind.getName}")
      }
      true
    }
    else
      false
  }

  /*
   * FunctionCall: Identifier ( DOT Identifier )+
   * DOT: "."
   */
  private def maybeFunctionCall: Boolean = {
    val prev = lexer.getPrevToken
    if (`match`(".")) {
      constructNodeHelper.start()
      constructNodeHelper.enqueue(new IdentifierASTNode(prev.getSourceString))
      do {
        constructNodeHelper.enqueue(parseIdentifier)
      }
      while (`match`("."))
      constructNodeHelper.push(new FunctionCallASTNode(constructNodeHelper.makeArray))
      true
    }
    else
      false
  }

  private def maybeIdentifierRelated: Boolean = {
    if (`match`(TokenKinds.getInstance getTokenKindByTag TokenKind.TAG_IDENTIFIER)) {
      if (!maybeNestedPropertyAccessor
        && !maybePostfixExpr
        && !maybeFunctionCall)
        constructNodeHelper.push(new IdentifierASTNode(lexer.getPrevToken.getSourceString))
      true
    }
    else
      false
  }

  /*
   * PrefixExpr: ( INC | DEC | BANG | PLUS | MINUS ) Expr
   */
  private def maybePrefixExpr: Boolean = {
    if (`match`("++")
      || `match`("--")
      || `match`("!")
      || `match`("+")
      || `match`("-")) {
      Operator.getByName(lexer.getPrevToken.getKind.getName) match {
        case operator: Operator =>
          constructNodeHelper.push(operator.createOperatorASTNode(parseExpr, null, true))
        case _ =>
          parseError(s"Unsupported prefix operator ${lexer.getPrevToken.getKind.getName}")
      }
      true
    }
    else
      false
  }

  /*
   * ArrayExpr: LBRACKET RBRACKET
   *   | LBRACKET Expr ( COMMA Expr )* RBRACKET
   * LBRACKET: "["
   * RBRACKET: "]"
   */
  private def maybeArrayExpr: Boolean = {
    if (`match`("[")) {
      constructNodeHelper.start()
      if (!`match`("]")) {
        do {
          constructNodeHelper.enqueue(parseExpr)
        }
        while (`match`(","))
        accept("]")
      }
      true
    }
    else
      false
  }

  private def maybeSpelExpression: Boolean = {
    if (`match`(StreamExpressionTokenKind.SPEL)) {
      constructNodeHelper.push(new SPELASTNode(lexer.getPrevToken.getSourceString))
      true
    }
    else
      false
  }

  private def maybeContextAttribute: Boolean = {
    if (`match`(StreamExpressionTokenKind.CTX_ATTR)) {
      constructNodeHelper.push(new ContextAttributeASTNode(lexer.getPrevToken.getTokenValue.asInstanceOf[String]))
      true
    }
    else
      false
  }

  /**
   * ParanExpr: LPARAN Expr RPARAN
   * 判断是否可能是括号表达式（括号优先级最高）
   * @return 如果是，那么返回true，否则返回false
   */
  private def maybeParanExpr: Boolean = {
    if (`match`("(")) {
      constructNodeHelper.push(parseExpr)
      accept(")")
      true
    }
    else
      false
  }

  private def parseIdentifier: IdentifierASTNode = {
    if (!`match`(TokenKinds.getInstance getTokenKindByTag TokenKind.TAG_IDENTIFIER)) {
      parseError(s"Expect identifier, but find: ${lexer.getCurrentToken.getKind}")
    }
    new IdentifierASTNode(lexer.getPrevToken.getSourceString)
  }

  private def accept(kindName: String): Unit = {
    if (!`match`(kindName)) {
      parseError(s"Unexpected token kind, expect: $kindName, but find: ${lexer.getCurrentToken.getKind}")
    }
  }

  private def accept(kind: TokenKind): Unit = {
    if (!`match`(kind)) {
      parseError(s"Unexpected token kind, expect: $kind, but find: ${lexer.getCurrentToken.getKind}")
    }
  }

  /**
   * 判断当前的token是否是关系运算符
   * @return 如果是，那么返回<code>true</code>，否则返回<code>false</code>
   */
  private def isRelOp: Boolean = lexer.getCurrentToken.getKind.isRelationOperator

  private def `match`(kindName: String): Boolean = `match`(TokenKinds.getInstance getTokenKindByName kindName)

  /**
   * 判断传入的TokenKind是否和当前解析的TokenKind一致，如果一致，那么执行nextToken操作并返回true，否则直接返回false
   * @param kind 要判断的TokenKind实例
   * @return 一致就返回true，否则就返回false
   */
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

  private[StreamExpressionParser] class ConstructNodeHelper {

    /**
     * 用于存储抽象语法树节点
     */
    private[this] val constructNodes: util.Deque[ASTNode] = new util.ArrayDeque[ASTNode]

    /**
     * 用于存储当前队列中的插入位置，一般是创建数组之前会使用该字段
     * lazy：延迟加载，第一次使用的时候才new
     */
    private[this] lazy val startIndexes: util.Deque[Int] = new util.ArrayDeque[Int](5)

    // ---------------------------- stack op begin ----------------------------
    def push(astNode: ASTNode): Unit = constructNodes.push(astNode)

    def pop: ASTNode = constructNodes.pop
    // ---------------------------- stack op end ----------------------------

    /**
     * 记录当前队列中的插入位置，一般是创建数组之前调用该方法
     */
    def start(): Unit = startIndexes.push(constructNodes.size)

    def makeArray: Array[ASTNode] = {
      val startIndex = startIndexes.pop
      val len = constructNodes.size - startIndex
      if (len == 0)
        AbstractASTNode.EMPTY
      else
        ArrayUtils.transferToArray(classOf[ASTNode], constructNodes, startIndex, len)
    }

    def makeSeq: Seq[ASTNode] = {
      val startIndex = startIndexes.pop
      val len = constructNodes.size - startIndex
      if (len == 0)
        Seq.empty
      else
        new ArraySeq.ofRef[ASTNode](ArrayUtils.transferToArray(classOf[ASTNode], constructNodes, startIndex, len))
    }

    def enqueue(astNode: ASTNode): Unit = constructNodes.offer(astNode)

    def dequeue: ASTNode = constructNodes.poll
  }
}
