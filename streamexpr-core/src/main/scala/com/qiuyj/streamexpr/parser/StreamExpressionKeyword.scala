package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.api.TokenKind
import com.qiuyj.streamexpr.parser.StreamExpressionKeyword._

/**
 * 关键字
 * @author qiuyj
 * @since 2023-07-06
 */
private[parser] class StreamExpressionKeyword(private[this] val keyword: String) extends StreamExpressionTokenKind(TokenKind.TAG_NOT_SUPPORT, keyword) {

  override def isKeyword = true

  override def isRelationOperator: Boolean = this match {
    case EQ
         | com.qiuyj.streamexpr.parser.StreamExpressionKeyword.eq
         | NEQ
         | com.qiuyj.streamexpr.parser.StreamExpressionKeyword.neq
         | GT
         | com.qiuyj.streamexpr.parser.StreamExpressionKeyword.gt
         | LT
         | com.qiuyj.streamexpr.parser.StreamExpressionKeyword.lt
         | GTEQ
         | com.qiuyj.streamexpr.parser.StreamExpressionKeyword.gteq
         | LTQE
         | com.qiuyj.streamexpr.parser.StreamExpressionKeyword.lteq
      => true
    case _ => false
  }
}

private[parser] object StreamExpressionKeyword {

  private def apply(keyword: String) = new StreamExpressionKeyword(keyword)

  val IN = StreamExpressionKeyword("IN")
  val in = StreamExpressionKeyword("in")
  val NOT_IN = StreamExpressionKeyword("NOT_IN")
  val not_in = StreamExpressionKeyword("not_in")
  val EQ = StreamExpressionKeyword("EQ")
  val eq = StreamExpressionKeyword("eq")
  val NEQ = StreamExpressionKeyword("NEQ")
  val neq = StreamExpressionKeyword("neq")
  val GT = StreamExpressionKeyword("GT")
  val gt = StreamExpressionKeyword("gt")
  val LT = StreamExpressionKeyword("LT")
  val lt = StreamExpressionKeyword("lt")
  val GTEQ = StreamExpressionKeyword("GTEQ")
  val gteq = StreamExpressionKeyword("gteq")
  val LTQE = StreamExpressionKeyword("LTEQ")
  val lteq = StreamExpressionKeyword("lteq")
  val IS_NULL = StreamExpressionKeyword("IS_NULL")
  val is_null = StreamExpressionKeyword("is_null")
  val NON_NULL = StreamExpressionKeyword("NON_NULL")
  val non_null = StreamExpressionKeyword("non_null")
  val OR = StreamExpressionKeyword("OR")
  val or = StreamExpressionKeyword("or")
  val AND = StreamExpressionKeyword("AND")
  val and = StreamExpressionKeyword("and")
}
