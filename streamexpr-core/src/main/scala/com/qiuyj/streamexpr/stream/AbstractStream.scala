package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp

import java.util.Objects

/**
 * @author qiuyj
 * @since 2023-08-23
 */
abstract class AbstractStream(private[this] val prev: AbstractStream) extends Stream {

  /**
   * 用于存储头结点（头结点中存储了要处理的数据信息）
   */
  private val head: AbstractStream =
    if (Objects.isNull(prev)) this
    else prev.head

  override def addIntermediateOps(intermediateOps: collection.Seq[StreamOp]): Stream = {
    if (intermediateOps.size == 1) {
      addIntermediateOp(intermediateOps.head)
    }
    else {
      var stream = this
      for (intermediateOp <- intermediateOps) {
        stream = StreamUtils.makeRef(stream, intermediateOp)
      }
      stream
    }
  }

  override def evaluate(terminateOp: StreamOp): Any = {
    if (this == head) {
      // 只有头节点，那么抛出异常
      throw new IllegalStateException("")
    }
  }

  def getSource: collection.Seq[_] = head.getSource
}
