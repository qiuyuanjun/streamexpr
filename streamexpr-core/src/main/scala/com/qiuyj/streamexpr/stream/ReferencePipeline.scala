package com.qiuyj.streamexpr.stream

/**
 * @author qiuyj
 * @since 2023-08-31
 */
class ReferencePipeline(private[this] val prev: AbstractStream) extends AbstractStream(prev) {

}

object ReferencePipeline {

  private[stream] class Head(private[this] val source: collection.Seq[_]) extends ReferencePipeline(null) {

    override def getSource: collection.Seq[_] = source
  }
}
