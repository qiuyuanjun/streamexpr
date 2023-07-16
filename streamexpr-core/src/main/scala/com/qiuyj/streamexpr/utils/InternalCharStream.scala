package com.qiuyj.streamexpr.utils

/**
 * @author qiuyj
 * @since 2023-06-28
 */
private[utils] class InternalCharStream(private[this] val charArray: Array[Char]) extends CharStream {

  private[this] var pos: Int = 0
  private[this] val maxPos: Int = charArray.length - 1

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

  override def pushback(n: Int): Unit = {
    pos = Ordering.Int.max(pos - n, 0)
  }

  override def remaining: Int = charArray.length - pos

  override def hasRemaining: Boolean = pos <= maxPos

  override def hasNext: Boolean = pos + 1 <= maxPos

  override def hasPrev: Boolean = pos - 1 >= 0

  override def getString(position: Int): String = {
    assert(position <= maxPos, s"Array index out of range with begin index: $position")
    if (position == pos - 1) {
      String.valueOf(getChar(0))
    }
    else if (position < pos) {
      new String(charArray, position, pos - position)
    }
    else {
      new String(charArray, pos, position - pos)
    }
  }

  override def getString(start: Int, end: Int): String = {
    assert(start <= maxPos && end <= maxPos, s"Array index out of range with begin index: $start and end index: $end")
    new String(charArray, start, Math.abs(end - start))
  }
}
