package com.qiuyj.streamexpr.stream
import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.StreamOp

/**
 * @author qiuyj
 * @since 2023-08-23
 */
class AbstractStream(private[this] val head: Stream, private[this] val prev: Stream) extends Stream {

  override def addIntermediateOps(intermediateOps: collection.Seq[StreamOp]): Stream = {
    var stream: Stream = this
    for (intermediateOp <- intermediateOps) {
      stream = new AbstractStream(head,this)
    }
    stream
  }

  override def evaluate(terminateOp: StreamExpression.StreamOp): Any = {

  }
}
