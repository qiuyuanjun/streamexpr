package com.qiuyj.cdexpr.scalaCode

/**
 * @author qiuyj
 * @since 2023-06-28
 */
private[scalaCode] class InternalCharStream(private[this] val charArray: Array[Char]) extends CharStream {

  private var pos: Int = 0
  private val maxPos: Int = charArray.length - 1

  def this(source: String) = this(source.toCharArray)

  override def read(): Int = if (pos > remaining) -1 else nextChar & 0xff

  override def nextChar: Char = {
    val c = charArray(pos)
    pos += 1
    c
  }

  override def getChar(n: Int): Char = {
    charArray(pos + n)
  }

  override def currentPos: Int = pos

  override def fallback(n: Int): Unit = {
    pos = Ordering.Int.max(pos - n, 0)
  }

  override def remaining: Int = charArray.length - pos

  override def hasRemaining: Boolean = pos <= maxPos

  override def hasNext: Boolean = pos + 1 <= maxPos

  override def hasPrev: Boolean = pos - 1 >= 0

  override def getString(position: Int): String =
    if (position == pos - 1) {
      String.valueOf(getCurrentChar)
    }
    else if (position < pos) {
      new String(charArray, position, pos - position)
    }
    else {
      new String(charArray, pos, position - pos)
    }

}
