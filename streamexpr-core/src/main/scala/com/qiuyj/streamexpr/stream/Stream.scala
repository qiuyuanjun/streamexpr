package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp

/**
 * stream流式数据处理顶层接口，定义了添加中间操作的api，传入终止操作计算最终结果的api
 * @author qiuyj
 * @since 2023-06-29
 */
trait Stream extends StreamContextCapable {

  /**
   * 向stream流中增加中间操作
   * @param intermediateOp 中间操作
   * @return 当前stream流对象
   */
  def addIntermediateOp(intermediateOp: StreamOp): Stream

  /**
   * 向stream流中增加中间操作（多个）
   * @param intermediateOps 中间操作（多个）
   * @return 当前stream流对象
   */
  def addIntermediateOps(intermediateOps: collection.Seq[StreamOp]): Stream

  /**
   * 根据传入的终止操作计算当前流式操作的结果
   * @param terminateOp 终止操作
   * @return 最终计算得到的结果
   */
  def evaluate(terminateOp: StreamOp): Any
}
