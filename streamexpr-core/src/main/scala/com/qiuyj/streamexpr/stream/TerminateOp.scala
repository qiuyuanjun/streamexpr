package com.qiuyj.streamexpr.stream

import java.util.function.Supplier

/**
 * 终止操作，内部执行所有节点的计算逻辑
 * @author qiuyj
 * @since 2023-09-02
 */
trait TerminateOp extends Supplier[Any] {

  def evaluate(pipelineHelper: PipelineHelper): Any
}
