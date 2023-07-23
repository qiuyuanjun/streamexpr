package com.qiuyj.streamexpr

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr.api.Expression
import com.qiuyj.streamexpr.parser.StreamExpressionParser

/**
 * @author qiuyj
 * @since 2023-06-29
 */
class StreamExpression extends Expression {

  override def getValue: Any = {
    null
  }

  def addStreamOp(streamOp: StreamOp): Unit = {

  }
}

object StreamExpression {

  def parse(streamExpr: String): StreamExpression =
    StreamExpressionParser.parse(streamExpr)

  // 这里之所以不使用scala的参数默认值功能，而是选择多新增一个方法，写死给定一个默认值
  // 主要是考虑到了给java调用，java是不支持的参数默认值功能的，必须得显示指定参数的值
  def parse(streamExpr: String, allowEmptyStreamExpression: Boolean): StreamExpression =
    StreamExpressionParser.parse(streamExpr, allowEmptyStreamExpression)

  class StreamOp {

  }

  class Parameter {

  }
}
