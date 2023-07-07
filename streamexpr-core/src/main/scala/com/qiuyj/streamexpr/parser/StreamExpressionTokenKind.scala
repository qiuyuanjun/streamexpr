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
}

object StreamExpressionTokenKind {

  val TAG_SPEL = 101

  val SPEL = new StreamExpressionTokenKind(TAG_SPEL, null)
}
