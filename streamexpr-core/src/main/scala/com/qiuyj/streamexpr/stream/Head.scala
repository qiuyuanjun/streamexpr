package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamContext

import java.util.Objects

/**
 * 流头节点，该节点用于存储要处理的数据，但是该节点不能处理数据
 * @author qiuyj
 * @since 2023-08-31
 */
private[stream] class Head(private[this] var source: collection.Seq[_],
                           private[this] val streamContext: StreamContext) extends ReferencePipeline(null) {

  /**
   * 获取要处理的集合数据
   * @note 该方法只能被调用一次，多次调用将会抛出异常
   * @return 要处理的集合数据
   */
  override def getSource: collection.Seq[_] = {
    val toBeProcessedDatasets =
      if (Objects.isNull(source))
        throw new IllegalStateException("Streaming datasets cannot be processed multiple times")
      else
        source
    source = null
    toBeProcessedDatasets
  }

  override def getStreamContext = streamContext

  override def opWrapSink(downstream: Sink): Sink = {
    throw new IllegalStateException("The head node does not support the opWrapSink operation")
  }
}
