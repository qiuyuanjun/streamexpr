package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamContext
import com.qiuyj.streamexpr.StreamExpression.StreamOp

import java.util.Objects

/**
 * stream流抽象实现
 * @author qiuyj
 * @since 2023-08-23
 */
abstract class AbstractStream(private val prevStream: AbstractStream)
    extends Stream
    with PipelineHelper {

  /**
   * 用于存储头结点（头结点中存储了要处理的数据信息）
   */
  private val head: AbstractStream =
    if (Objects.isNull(prevStream)) this
    else prevStream.head

  /**
   * 从头结点中获取Stream上下文对象
   * @return 获取到的Stream上下文对象
   */
  override def getStreamContext: StreamContext = head.getStreamContext

  override def addIntermediateOp(intermediateOp: StreamOp): Stream =
    addIntermediateOps(Iterator.single(intermediateOp))

  override def addIntermediateOps(intermediateOps: collection.Seq[StreamOp]): Stream =
    addIntermediateOps(intermediateOps.iterator)

  private[this] def addIntermediateOps(intermediateOps: Iterator[StreamOp]): Stream = {
    var stream: Stream = this
    while (intermediateOps.hasNext) {
      stream = IntermediateOps.makeRef(stream, intermediateOps.next())
    }
    stream
  }

  override def evaluate(terminateOp: StreamOp): Any = {
    TerminateOps.makeRef(getStreamContext, terminateOp)
      .evaluateSequential(this, getSource.iterator)
  }

  /**
   * 从头节点中获取要处理的数据集
   * @note 该方法只能被调用一次
   * @return 获取到的要处理的数据集
   */
  def getSource: collection.Seq[_] = head.getSource

  override def buildStreamPipeline(terminateSink: TerminateSink): Sink = {
    var streamPipeline: Sink = terminateSink
    var stream: AbstractStream = this
    while (stream ne stream.head) {
      streamPipeline = stream.opWrapSink(streamPipeline)
      stream = stream.prevStream
    }
    streamPipeline
  }

  /**
   * 将当前的操作包装成Sink管道
   * @param downstream 下一个管道
   * @return 管道
   */
  protected def opWrapSink(downstream: Sink): Sink

  override def runStreamPipeline(streamContext: StreamContext,
                                 streamPipeline: Sink,
                                 toBeProcessedDatasets: Iterator[_]): Unit = {
    streamPipeline.begin(streamContext)
    while (toBeProcessedDatasets.hasNext && !streamPipeline.cancelledRequest) {
      streamPipeline.accept(toBeProcessedDatasets.next(), streamContext)
    }
    streamPipeline.end(streamContext)
  }
}
