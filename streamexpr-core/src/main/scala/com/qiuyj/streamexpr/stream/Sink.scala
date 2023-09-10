package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamContext

import java.util.function.{BiConsumer, Supplier}

/**
 * Sink表示Stream管道中的一环，一个Stream管道由一个或者多个Sink组成
 * 要处理的集合数据中的每一项，都会经过Sink
 * @author qiuyj
 * @since 2023-09-02
 */
abstract class Sink
    extends BiConsumer[Any, StreamContext] {

  /**
   * 执行数据消费之前的动作
   */
  def begin(streamContext: StreamContext): Unit = {}

  /**
   * 消费完数据之后的动作
   */
  def end(streamContext: StreamContext): Unit = {}

  /**
   * 取消下一次数据加工
   * @return 如果返回true，那么取消后续数据加工，否则继续后续数据加工直到所有数据都被处理
   */
  def cancelledRequest: Boolean = false
}

abstract class ChainedSink(protected val downstream: Sink)
    extends Sink {

  override def begin(streamContext: StreamContext): Unit = {
    doBegin(streamContext)
    downstream.begin(streamContext)
  }

  /**
   * 当前管道数据消费之前的动作，子类重写
   */
  protected def doBegin(streamContext: StreamContext): Unit = {}

  override def end(streamContext: StreamContext): Unit = {
    doEnd(streamContext)
    downstream.end(streamContext)
  }

  /**
   * 当前管道消费数据之后的动作，子类重写
   */
  protected def doEnd(streamContext: StreamContext): Unit = {}

  override def cancelledRequest: Boolean = downstream.cancelledRequest

}

/**
 * 终止操作管道，相比于中间操作的管道，增加了获取结果的方法（实现了Supplier接口）
 * stream流上下文存储在终止操作的管道里面，提供获取stream流上下文的方法的实现
 */
abstract class TerminateSink(private[this] val streamContext: StreamContext)
    extends Sink
    with Supplier[Any]
    with StreamContextCapable {

  override def getStreamContext: StreamContext = streamContext
}
