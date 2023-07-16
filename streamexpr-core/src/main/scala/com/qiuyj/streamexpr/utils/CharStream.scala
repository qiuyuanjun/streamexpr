package com.qiuyj.streamexpr.utils

import java.io.InputStream

/**
 * @author qiuyj
 * @since 2023-06-28
 */
trait CharStream extends InputStream {

  /**
   * 返回下一个字符，该方法会将存储字符位置的变量加一
   * @note 该方法不会控制边界，需要调用方自行控制边界
   * @return 下一个字符
   */
  def nextChar: Char

  /**
   * 返回给定位置的字符，该方法不会改变存储当前字符位置的变量
   * @param n 位置（当前位置为参照点）
   * @note 该方法不会控制边界，需要调用方自行控制边界
   * @return 对应位置的字符
   */
  def getChar(n: Int): Char

  def getPrevChar: Char = getChar(-1)

  def getNextChar: Char = getChar(1)

  def currentPos: Int

  /**
   * 回退一个字符，将指针往前移一位
   */
  def pushback(): Unit = pushback(1)

  /**
   * 回退给定位数的字符数，将指针往前移给定的位数
   * @note 该方法会控制边界，如果指针最终回退的位置小于0，那么默认是回退到0
   * @param n 要回退的字符数
   */
  def pushback(n: Int): Unit

  /**
   * 返回剩余未读取的字符数量
   * @return 未读取的字符数量
   */
  def remaining: Int

  def hasRemaining: Boolean

  def hasNext: Boolean

  def hasPrev: Boolean

  def getString(start: Int): String

  def getString(start: Int, end: Int): String
}

object CharStream {

  def wrap(source: String): CharStream = new InternalCharStream(source)

  def wrap(source: String, begin: Int): CharStream = wrap(source, begin, source.length)

  def wrap(source: String, begin: Int, end: Int): CharStream = new InternalCharStream(source.substring(begin, end))

  def wrap(source: Array[Char]): CharStream = new InternalCharStream(source)

  def wrap(source: Array[Char], begin: Int): CharStream = wrap(source, begin, source.length)

  def wrap(source: Array[Char], begin: Int, end: Int): CharStream = {
    if (begin < 0 || end > source.length - 1) {
      throw new ArrayIndexOutOfBoundsException(s"Array index out of range with begin index: $begin and end index: $end")
    }
    new InternalCharStream(new String(source, begin, end - begin))
  }
}
