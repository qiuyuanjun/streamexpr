package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp

import scala.collection.mutable

/**
 * @author qiuyj
 * @since 2023-06-29
 */
trait Stream {

  /**
   * 向stream流中增加中间操作
   * @param intermediateOp 中间操作
   * @return 当前stream流对象
   */
  def addIntermediateOp(intermediateOp: StreamOp): Stream =
    addIntermediateOps(mutable.Seq.apply(intermediateOp))

  def addIntermediateOps(intermediateOps: mutable.Seq[StreamOp]): Stream

  /**
   * 根据传入的终止操作计算当前流式操作的结果
   * @param terminateOp 终止操作
   * @return 最终计算得到的结果
   */
  def evaluate(terminateOp: StreamOp): Any
}
