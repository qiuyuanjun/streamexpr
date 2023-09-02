package com.qiuyj.streamexpr.stream

import java.util.function.Consumer

/**
 * Sink表示Stream管道中的一环，一个Stream管道由一个或者多个Sink组成
 * 要处理的集合数据中的每一项，都会经过Sink
 * @author qiuyj
 * @since 2023-09-02
 */
abstract class Sink extends Consumer[Any] {

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

abstract class ChainedSink(protected val downstream: Sink) extends Sink {

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
}

trait TerminateOpSink extends Sink with TerminateOp {


}
