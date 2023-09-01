package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.stream.ReferencePipeline.Head

/**
 * @author qiuyj
 * @since 2023-08-31
 */
class ReferencePipeline(private[this] val head: AbstractStream) extends AbstractStream(head) {
}

object ReferencePipeline {

  private[stream] class Head(private[this] val source: collection.Seq[_]) extends ReferencePipeline(this.asInstanceOf) {

    override def getSource: collection.Seq[_] = source
  }
}
