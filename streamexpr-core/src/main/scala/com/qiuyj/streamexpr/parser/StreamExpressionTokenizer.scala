package com.qiuyj.streamexpr.parser

import com.qiuyj.streamexpr.api.Token.NamedToken
import com.qiuyj.streamexpr.api.{LexerException, Token, TokenKind, TokenKinds}
import com.qiuyj.streamexpr.utils.CharStream

import java.util.Objects

/**
 * @author qiuyj
 * @since 2023-06-29
 */
private[parser] class StreamExpressionTokenizer(private[this] val source: CharStream) {

  /**
   * 代表当前字符，readToken主要是对该字符做各种判断
   */
  private[this] var character: Char = _

  /**
   * 存储上一个字符，后续需要获取上一个字符的值，均使用该字段获取
   */
  private[this] var prevCharacter: Char = _

  /**
   * 存储预读的字符
   */
  private[this] var lookaheadCharacter: Char = _

  private[this] val stringContent = new StringBuilder

  /**
   * 存储当前解析的token的类型
   */
  private[this] var kind: TokenKind = _

  // 初始化相关TokenKind
  assert(StreamExpressionKeyword.IN.isKeyword)
  TokenKinds.getInstance.initInternal()

  /**
   * 读取下一个字符，将读取到的字符赋值给character字段，如果值是0，那么表示读取到尾了，那么需要终止相关的操作
   */
  private def next: Char = {
    if (character != 0) {
      prevCharacter = character
    }
    lookaheadCharacter = 0
    character = if (source.hasRemaining) source.nextChar else 0
    character
  }

  private def put(): Unit = {
    stringContent append character
  }

  private def putThenNext: Char = {
    put()
    next
  }

  /**
   * 回退一个字符，有些方法，比如读取标识符或者数字，需要多读一个字符来判断结束，因此在读取结束之后，需要将多读的字符回退回去
   */
  private def pushback(): Unit = {
//    character = prevCharacter
    // 由于需要获取prevCharacter的值，因此这里需要先回退2个字符
    // 等拿到了prevCharacter的值之后，再调用nextChar获取character的值
    source.pushback(2)
    // 假设有ab.c这个字符，读取完ab标识符之后，pos位置如下所示：
    // ab.c
    //    ^
    // 执行完pushback(2)函数之后，pos位置如下所示：
    // ab.c
    //  ^
    prevCharacter = if (source.hasPrev) source.getPrevChar else 0
    character = source.nextChar
    // 执行完nextChar方法之后（返回字符'b'），pos位置如下所示：
    // ab.c
    //   ^
    lookaheadCharacter = 0 // 如果回退了，那么需要清空预读的字符值
  }

  /**
   * 预读一个字符
   * @return 预读的字符
   * @note 调用该方法之后，需要及时调用next方法
   */
  private def lookahead: Char = {
    if (lookaheadCharacter != 0) {
      throw new IllegalStateException("After calling the 'lookahead' method, it is necessary to call the 'next' method in a timely manner")
    }
    if (source.hasNext) {
      // 仅仅是预读一个字符，并不移动位置指针
      lookaheadCharacter = source.getCurrentChar
    }
    lookaheadCharacter
  }

  def readToken: Token = {
    stringContent.setLength(0)
    val startPos = skipWhitespace
    var numericInfo: NumericInfo = null
    character match {
      case 0 => kind = TokenKinds.getInstance getTokenKindByTag TokenKind.TAG_EOF
      case 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' |
           'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' |
           'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' |
           'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' | 'W' | 'X' | 'Y' | 'Z' |
           '$' | '_' =>
        next
        readIdentifier(startPos)
        pushback()
      case '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' =>
        val firstIsZero = is('0')
        putThenNext
        numericInfo = readNumeric(startPos, firstIsZero)
        pushback()
      case '\'' =>
        next
        readStringLiteral(startPos)
      case '#' => // 目前仅支持#开头表示spel表达式
        next
        readSpelExpression(startPos)
      case '.' =>
        kind = TokenKinds.getInstance getTokenKindByName "."
      case '(' =>
        kind = TokenKinds.getInstance getTokenKindByName "("
      case ')' =>
        kind = TokenKinds.getInstance getTokenKindByName ")"
      case ',' =>
        kind = TokenKinds.getInstance getTokenKindByName ","
      case '-' =>
        kind = lookaheadTokenIfMatch(true,
          '-',
          '-',
          '=')
      case '+' =>
        kind = lookaheadTokenIfMatch(true,
          '+',
          '+',
          '=')
      case '|' =>
        kind = lookaheadTokenIfMatch(true,
          '|',
          '|')
      case '*' =>
        kind = lookaheadTokenIfMatch(true,
          '*',
          '=')
      case '/' =>
        kind = lookaheadTokenIfMatch(true,
          '/',
          '=')
      case '>' =>
        kind = lookaheadTokenIfMatch(true,
          '>',
          '=')
      case '<' =>
        kind = lookaheadTokenIfMatch(true,
          '<',
          '=')
      case '=' =>
        kind = lookaheadTokenIfMatch(true,
          '=',
          '=')
      case '[' =>
        kind = TokenKinds.getInstance getTokenKindByName "["
      case ']' =>
        kind = TokenKinds.getInstance getTokenKindByName "]"
      case '&' => // 目前只支持&&，不支持单个&
        kind = lookaheadTokenIfMatch(false,
          '&',
          '&')
      case '!' =>
        kind = lookaheadTokenIfMatch(true,
          '!',
          '=')
      case '@' => // 从上下文里面获取变量
        putThenNext
        readContextAttribute()
        pushback()
      case _ =>
        lexError(s"Illegal character '$character'")
        throw new IllegalStateException("Never reach here!")
    }
    val sourceString = if (stringContent.isEmpty) kind.getName else stringContent.toString
    if (kind.isNamed)
      new NamedToken(sourceString, sourceString, startPos, kind)
    else if (kind.isNumeric)
      new NumericToken(sourceString, startPos, kind, numericInfo)
    else
      new Token(sourceString, startPos, kind)
  }

  /**
   * 读取上下文属性
   */
  private def readContextAttribute(): Unit = {
    while (isInRange('a', 'z')
      || isInRange('A', 'Z')
      || isInRange('0', '9')
      || isOneOf('_', '$')) {
      putThenNext
    }
    if (stringContent.length == 1) {
      lexError("Context attribute must has content")
    }
    kind = StreamExpressionTokenKind.CTX_ATTR
  }

  /**
   * 判断当前字符的下一个字符是否是给定的字符，如果是，那么创建这两个字符组成的TokenKind，否则值创建当前字符组成的TokenKind
   * @param fallbackCurrentIfNotMatch 如果下一个预读的字符都没有匹配上，是否回退到当前字符对应的TokenKind
   * @param currentCharacter 当前字符
   * @param lookaheadCharacters 下一个字符
   * @return TokenKind
   */
  private def lookaheadTokenIfMatch(fallbackCurrentIfNotMatch: Boolean, currentCharacter: Char, lookaheadCharacters: Char*): TokenKind = {
    lookahead
    val iter = lookaheadCharacters.iterator
    val tokenKinds = TokenKinds.getInstance
    while (iter.hasNext) {
      val nextChar = iter.next()
      if (lookaheadIs(nextChar)) {
        next
        return tokenKinds getTokenKindByName Array.apply(currentCharacter, nextChar).mkString
      }
    }
    if (!fallbackCurrentIfNotMatch) {
      lexError(s"Illegal character '$currentCharacter'")
    }
    tokenKinds getTokenKindByName String.valueOf(currentCharacter)
  }

  /**
   * 解析spel表达式，以 #{ 开头，以 } 结尾
   * @param startPos spel表达式的起始位置
   */
  private def readSpelExpression(startPos: Int): Unit = {
    // spel表达式，以 #{ 开头，以 } 结尾
    if (!is('{')) {
      lexError("Illegal character '#'")
    }
    var lbraceCount = 1
    var loop = true
    do {
      next
      if (is('{')) lbraceCount += 1
      else if (is('}')) lbraceCount -= 1
      if (lbraceCount == 0) {
        loop = false
      }
      else if (is(0)) {
        lexError(s"The SPEL expression does not end with character '}' at start position: $startPos")
      }
    }
    while (loop)
    val spelExpr = source.getString(startPos)
    val length = spelExpr.length
    if (length == 3 || spelExpr.substring(2, length - 1).isBlank) {
      // spel表达式是空的(#{})，那么抛出异常
      lexError(s"The SPEL expression is empty at start position: $startPos")
    }
    stringContent ++= spelExpr
    kind = StreamExpressionTokenKind.SPEL
  }

  /**
   * 解析字符串字面量
   * @param startPos 字符串字面量的起始位置
   */
  private def readStringLiteral(startPos: Int): Unit = {
    var completeString = false
    var loop = true
    var hasEscapeChar = false
    while (loop) {
      if (!isOneOf(0, '\'')) {
        next
      }
      else if (is('\'')) {
        if (prevCharacter == '\\') {
          hasEscapeChar = true
          next
        }
        else {
          loop = false
          completeString = true
        }
      }
      else {
        loop = false
      }
    }
    if (!completeString) {
      lexError(s"The string literal does not end in character ', at start position: ${startPos - 1}")
    }
    // 需要跳过最前面的'和最后面的'
    var stringLiteral = source.getString(startPos + 1, source.currentPos - 1)
    if (hasEscapeChar) {
      stringLiteral = stringLiteral.translateEscapes
    }
    stringContent ++= stringLiteral
    kind = TokenKinds.getInstance.getTokenKindByTag(TokenKind.TAG_STRING_LITERAL)
  }

  private def readNumeric(startPos: Int, firstIsZero: Boolean): NumericInfo = {
    val radix = readNumericRadix(firstIsZero)
    val integerPart = readNumericPart(radix)._1
    val tokenKinds = TokenKinds.getInstance
    var fractionPart = 0L
    if (is('.')) {
      // 处理小数，所有的小数，只能是10进制
      if (radix != 10) {
        lexError(s"Decimal fraction can only be decimalism at start position: $startPos")
      }
      putThenNext
      val fractionPartReadResult = readNumericPart(10)
      if (!fractionPartReadResult._2) {
        lexError("Illegal decimal number format, with ending character '.'")
      }
      fractionPart = fractionPartReadResult._1
      kind = tokenKinds.getTokenKindByTag(TokenKind.TAG_DOUBLE_NUMERIC)
    }
    else if (isOneOf('L', 'l')) {
      // long类型整数
      putThenNext
      kind = tokenKinds.getTokenKindByTag(TokenKind.TAG_LONG_NUMERIC)
    }
    else {
      kind = tokenKinds.getTokenKindByTag(TokenKind.TAG_INTEGER_NUMERIC)
    }
    // 处理结尾，如果是F或者f，表示是float类型，如果是D或者d表示是double类型
    if (isOneOf('F', 'f')) {
      putThenNext
      kind = tokenKinds.getTokenKindByTag(TokenKind.TAG_FLOAT_NUMERIC)
    }
    else if (isOneOf('D', 'd')) {
      putThenNext
      kind = tokenKinds.getTokenKindByTag(TokenKind.TAG_DOUBLE_NUMERIC)
    }
    new NumericInfo(radix, integerPart, fractionPart)
  }

  /**
   * 读取数字的进制（2，8，10，16）
   * @return 当前数字的进制（2，8，10，16）
   */
  private def readNumericRadix(firstIsZero: Boolean): Int = {
    var radix = 10
    if (firstIsZero) {
      radix = if (isOneOf('x', 'X')) 16
              else if (isOneOf('b', 'B')) 2
              else if (isInRange('0', '7')) 8
              else 10
      if (radix != 10) {
        next
      }
      else if (isOneOf('8', '9')) {
        // 异常的8进制数字字符
        lexError(s"Illegal Octal digit character: $character")
      }
    }
    radix
  }

  private def readNumericPart(radix: Int): (Long, Boolean) = {
    var canContinue = true
    // 如果是10进制，那么需要判断前一个字符是否是数字，如果是数字，那么result初始化对应字符的数字值
    var result = if (radix == 10 && Character.isDigit(prevCharacter)) prevCharacter - '0' else 0
    var hasResult = false
    while (canContinue) {
      val digitValue =
        if (isInRange('0', '9')) character - '0'
        else if (isInRange('a', 'f')) character - 'a' + 10
        else if (isInRange('A', 'F')) character - 'A' + 10
        else -1
      if (digitValue >= 0 && digitValue < radix) {
        putThenNext
        result = result * radix + digitValue
        hasResult = true
      }
      else if (digitValue == -1) {
        canContinue = false
      }
      else if (digitValue >= radix) {
        // 抛异常
        lexError(s"Illegal number format, current decimal: $radix, actual value: $digitValue")
      }
    }
    (result, hasResult)
  }

  /**
   * 解析标识符
   * @param startPos 标识符的起始位置
   */
  private def readIdentifier(startPos: Int): Unit = {
    var canContinue = true
    while (canContinue) character match {
      case 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' |
           'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' |
           'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' |
           'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' | 'W' | 'X' | 'Y' | 'Z' |
           '$' | '_' |
           '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' =>
        next
      case _ => canContinue = false
    }
    val identifier = source.getString(startPos, source.currentPos - 1)
    // 判断是否是关键字
    val tokenKinds = TokenKinds.getInstance
    kind = tokenKinds.getKeyword(identifier)
    if (Objects.isNull(kind)) {
      stringContent ++= identifier
      kind = tokenKinds.getTokenKindByTag(TokenKind.TAG_IDENTIFIER)
    }
  }

  /**
   * 跳过所有的空白字符，并返回第一个非空白字符的位置信息
   * @note 该方法会把位置指针放置到第一个非空白字符后一个字符处
   */
  private def skipWhitespace: Int = {
    while (Character.isWhitespace(next)) {}
    source.currentPos - 1
  }

  /**
   * 判断当前位置的字符是否是给定的字符
   * @param c 要判断的字符
   * @return 如果当前位置的是给定的字符，那么返回true，否则返回false
   */
  private def is(c: Char): Boolean = character == c

  private def isOneOf(c1: Char, c2: Char): Boolean = is(c1) || is(c2)

  private def isInRange(lo: Int, hi: Int): Boolean = {
    val value = character.toInt
    value >= lo && value <= hi
  }

  /**
   * 判断给定的字符是否和预读的字符一致
   * @param c 要判断的字符
   * @return 判断结果，如果一致，那么返回true，否则返回false
   */
  private def lookaheadIs(c: Char): Boolean = lookaheadCharacter == c

  private def lexError(errorMessage: String): Unit = {
    throw new LexerException(errorMessage)
  }
}
