package com.qiuyj.streamexpr.stream

/**
 * 终止操作，内部执行所有节点的计算逻辑
 * @author qiuyj
 * @since 2023-09-02
 */
trait TerminateOp {

  /**
   * 处理给定的数据集中的所有数据（顺序执行所有的管道）
   * @param pipelineHelper 管道辅助类
   * @param toBeProcessedDatasets 要处理的数据集
   * @return 数据处理结果
   */
  def evaluateSequential(pipelineHelper: PipelineHelper, toBeProcessedDatasets: Iterator[_]): Any
}
