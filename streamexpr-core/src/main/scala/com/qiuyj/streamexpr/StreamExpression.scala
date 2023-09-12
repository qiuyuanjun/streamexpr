package com.qiuyj.streamexpr

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr.api.Expression
import com.qiuyj.streamexpr.api.ast.ASTNode
import com.qiuyj.streamexpr.parser.StreamExpressionParser
import com.qiuyj.streamexpr.stream.StreamUtils
import org.springframework.expression.spel.standard.SpelExpression
import org.springframework.lang.{NonNull, Nullable}

import java.util.Objects
import scala.collection.mutable.ArrayBuffer

/**
 * @author qiuyj
 * @since 2023-06-29
 */
class StreamExpression extends Expression[StreamContext] {

  /**
   * 中间操作，0或者n个
   */
  @Nullable
  private[this] var intermediateOps: ArrayBuffer[StreamOp] = _

  /**
   * 终止操作，必须有值
   */
  @NonNull
  private[this] var terminateOp: StreamOp = _

  override def evaluate(): Any = evaluate(new StreamContext)

  override def evaluate(context: StreamContext): Any = {
    var stream = StreamUtils.makeStream(context.getSource, context)
    if (Objects.nonNull(intermediateOps)) {
      stream = stream.addIntermediateOps(intermediateOps)
    }
    stream.evaluate(terminateOp)
  }

  /**
   * 增加中间操作或者是终止操作
   * @param streamOp 操作
   */
  private[streamexpr] def internalAddStreamOp(streamOp: StreamOp): Unit = {
    if (Objects.isNull(terminateOp)) {
      terminateOp = streamOp
    }
    else {
      if (Objects.isNull(intermediateOps)) {
        // 正常情况下，中间操作不会超过4个
        intermediateOps = new ArrayBuffer[StreamOp](4)
      }
      intermediateOps += terminateOp
      terminateOp = streamOp
    }
  }

  /**
   * 校验terminateOp字段必须不为null
   */
  private[streamexpr] def internalCheck(): Unit =
    Objects.requireNonNull(terminateOp, "Stream expressions must have at least one termination operation")

}

object StreamExpression {

  def parse(streamExpr: String): StreamExpression =
    StreamExpressionParser.parse(streamExpr)

  // 这里之所以不使用scala的参数默认值功能，而是选择多新增一个方法，写死给定一个默认值
  // 主要是考虑到了给java调用，java是不支持的参数默认值功能的，必须得显示指定参数的值
  def parse(streamExpr: String, allowEmptyStreamExpression: Boolean): StreamExpression =
    StreamExpressionParser.parse(streamExpr, allowEmptyStreamExpression)

  class StreamOp {

    /**
     * 操作名称
     */
    private[this] var opName: String = _

    /**
     * 当前操作的参数
     */
    private[this] var parameters: ArrayBuffer[Parameter] = _

    def getOpName: String = opName

    def getParameters: ArrayBuffer[Parameter] = parameters

    private[streamexpr] def internalSetValue(value: AnyRef): Unit = {
      if (Objects.isNull(opName)) {
        // opName必须是字符串
        opName = value.toString
      }
      else {
        if (Objects.isNull(parameters)) {
          // 大多数情况下，参数不会超过5个
          parameters = new ArrayBuffer[Parameter](5)
        }
        parameters += value.asInstanceOf[Parameter]
      }
    }
  }

  class Parameter {

    private[this] var kindValue: Any = _

    private[this] var kind: Kind = _

    private[streamexpr] def internalInitParameter(kindValue: Any, kind: Kind): Unit = {
      this.kindValue = kindValue
      this.kind = kind
    }

    def getValue(streamContext: StreamContext, valueContext: Any): Any = {
      if ((kind eq IDENTIFIER) && (kindValue.toString eq "_"))
        valueContext
      else
        kind.getValue(kindValue, valueContext, streamContext)
    }
  }

  sealed trait Kind {

    def getValue(kindValue: Any,
                 valueContext: Any,
                 streamContext: StreamContext): Any
  }

  object IDENTIFIER extends Kind {

    override def getValue(kindValue: Any,
                          valueContext: Any,
                          streamContext: StreamContext): Any = {

    }
  }

  object SPEL extends Kind {

    override def getValue(kindValue: Any,
                          valueContext: Any,
                          streamContext: StreamContext): Any = {
      kindValue.asInstanceOf[SpelExpression].getValue(valueContext)
    }
  }

  object STRING_LITERAL extends Kind {

    override def getValue(kindValue: Any,
                          valueContext: Any,
                          streamContext: StreamContext): Any =
      kindValue.asInstanceOf[String]
  }

  object AST extends Kind {

    override def getValue(kindValue: Any,
                          valueContext: Any,
                          streamContext: StreamContext): Any = {
      kindValue.asInstanceOf[ASTNode].evaluate
    }
  }

  object CONTEXT_ATTRIBUTE extends Kind {

    override def getValue(kindValue: Any,
                          valueContext: Any,
                          streamContext: StreamContext): Any = {
      streamContext.getValue(kindValue.toString)
    }
  }
}
