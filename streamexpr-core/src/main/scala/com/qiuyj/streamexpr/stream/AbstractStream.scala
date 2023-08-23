package com.qiuyj.streamexpr.stream
import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.StreamOp

import scala.collection.mutable

/**
 * @author qiuyj
 * @since 2023-08-23
 */
class AbstractStream(private[this] val prev: Stream) extends Stream {

  override def addIntermediateOps(intermediateOps: mutable.Seq[StreamOp]): Stream = {
    var stream: Stream = this
    for (streamOp <- intermediateOps) {
      stream = new AbstractStream(this)
    }
    stream
  }

  override def evaluate(terminateOp: StreamExpression.StreamOp): Any = {

  }
}
