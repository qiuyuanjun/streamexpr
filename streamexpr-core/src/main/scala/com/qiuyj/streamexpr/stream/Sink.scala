package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamContext

import java.util.function.{Consumer, Supplier}

/**
 * Sink表示Stream管道中的一环，一个Stream管道由一个或者多个Sink组成
 * 要处理的集合数据中的每一项，都会经过Sink
 * @author qiuyj
 * @since 2023-09-02
 */
abstract class Sink
    extends Consumer[Any]
    with StreamContextCapable {

  /**
   * 执行数据消费之前的动作
   */
  def begin(): Unit = {}

  /**
   * 消费完数据之后的动作
   */
  def end(): Unit = {}

  def cancelledRequest: Boolean = false
}

abstract class ChainedSink(protected val downstream: Sink)
    extends Sink {

  override def begin(): Unit = {
    doBegin()
    downstream.begin()
  }

  /**
   * 当前管道数据消费之前的动作，子类重写
   */
  protected def doBegin(): Unit = {}

  override def end(): Unit = {
    doEnd()
    downstream.end()
  }

  /**
   * 当前管道消费数据之后的动作，子类重写
   */
  protected def doEnd(): Unit = {}

  override def cancelledRequest: Boolean = downstream.cancelledRequest

  /**
   * 中间操作中获取stream流上下文的方法实现
   * 所有中间操作获取stream流上下文的默认实现均是委托给downstream获取，最终交给终止操作实现
   * @see com.qiuyj.streamexpr.stream.TerminateSink#getStreamContext
   * @return stream流上下文
   */
  override def getStreamContext: StreamContext = downstream.getStreamContext
}

/**
 * 终止操作管道，相比于中间操作的管道，增加了获取结果的方法（实现了Supplier接口）
 * stream流上下文存储在终止操作的管道里面，提供获取stream流上下文的方法的实现
 */
abstract class TerminateSink(private[this] val streamContext: StreamContext)
    extends Sink
    with Supplier[Any] {

  override def getStreamContext: StreamContext = streamContext
}
