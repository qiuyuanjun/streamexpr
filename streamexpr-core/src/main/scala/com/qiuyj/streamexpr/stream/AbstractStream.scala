package com.qiuyj.streamexpr.stream
import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.StreamOp

/**
 * @author qiuyj
 * @since 2023-08-23
 */
class AbstractStream(private[this] val head: AbstractStream,
                     private[this] val prev: AbstractStream) extends Stream {

  def this(head: AbstractStream) = this(head, null)

  override def addIntermediateOps(intermediateOps: collection.Seq[StreamOp]): Stream = {
    var stream: Stream = this
    for (intermediateOp <- intermediateOps) {
      stream = StreamUtils.makeRef(stream, intermediateOp)
    }
    stream
  }

  override def evaluate(terminateOp: StreamExpression.StreamOp): Any = {

  }

  def getSource: collection.Seq[_] = head.getSource
}
