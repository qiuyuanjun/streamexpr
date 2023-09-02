package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp

import java.util.Objects

/**
 * @author qiuyj
 * @since 2023-08-23
 */
abstract class AbstractStream(private val prevStream: AbstractStream) extends Stream with PipelineHelper {

  /**
   * 用于存储头结点（头结点中存储了要处理的数据信息）
   */
  private val head: AbstractStream =
    if (Objects.isNull(prevStream)) this
    else prevStream.head

  override def addIntermediateOps(intermediateOps: collection.Seq[StreamOp]): Stream = {
    if (intermediateOps.size == 1) {
      addIntermediateOp(intermediateOps.head)
    }
    else {
      var stream = this
      for (intermediateOp <- intermediateOps) {
        stream = IntermediateOps.makeRef(stream, intermediateOp)
      }
      stream
    }
  }

  override def evaluate(terminateOp: StreamOp): Any =
    TerminateOps.makeRef(terminateOp).evaluate(this)

  def getSource: collection.Seq[_] = head.getSource

  override def buildStreamPipeline(terminateOpSink: TerminateOpSink): Sink = {
    var streamPipeline: Sink = terminateOpSink
    var stream: AbstractStream = this
    while (stream != stream.head) {
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

  override def runStreamPipeline(streamPipeline: Sink): Unit = {
    val source: Iterator[_] = getSource.iterator
    streamPipeline.begin()
    while (source.hasNext && !streamPipeline.cancelledRequest) {
      streamPipeline.accept(source.next())
    }
    streamPipeline.end()
  }
}
