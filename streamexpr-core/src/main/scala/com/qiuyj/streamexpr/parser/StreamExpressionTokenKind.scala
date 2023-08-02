package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.api.{TokenKind, TokenKinds}

/**
 * @author qiuyj
 * @since 2023-07-04
 */
class StreamExpressionTokenKind(private[this] val tag: Int, private[this] val name: String) extends TokenKind {

  TokenKinds.getInstance.registerTokenKind(this)

  override def getTag: Int = tag

  override def getName: String = name

  override def toString: String = name

  override def isRelationOperator: Boolean = false
}

object StreamExpressionTokenKind {

  val TAG_SPEL = 101
  val TAG_CTX_ATTR = 102

  val SPEL = new StreamExpressionTokenKind(TAG_SPEL, null) {

    override def toString: String = "SPEL"
  }

  val CTX_ATTR = new StreamExpressionTokenKind(TAG_CTX_ATTR, null) {

    override def toString: String = "CTX_ATTR"
  }
}
