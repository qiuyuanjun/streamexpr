package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.api.{Token, TokenKind}

/**
 * @author qiuyj
 * @since 2023-07-06
 */
private[parser] class NumericToken(private[this] val sourceString: String,
                                   private[this] val startPos: Int,
                                   private[this] val kind: TokenKind,
                                   private[this] val numericInfo: NumericInfo)
    extends Token(sourceString, startPos, kind) {

  override def getTokenValue: Any = getNumericValue

  private def getNumericValue: AnyVal = numericInfo.integerPart

  def getRadix: Int = numericInfo.radix

  def isInteger: Boolean = getKind.getTag == TokenKind.TAG_INTEGER_NUMERIC
  def isLong: Boolean = getKind.getTag == TokenKind.TAG_LONG_NUMERIC
  def isFloat: Boolean = getKind.getTag == TokenKind.TAG_FLOAT_NUMERIC
  def isDouble: Boolean = getKind.getTag == TokenKind.TAG_DOUBLE_NUMERIC
}

class NumericInfo(private[parser] val radix: Int,
                  private[parser] val integerPart: Long,
                  private[parser] val fractionPart: Long)
