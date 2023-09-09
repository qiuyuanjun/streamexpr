package com.qiuyj.streamexpr.stream

/**
 * 定义管道相关的操作
 * @author qiuyj
 * @since 2023-09-02
 */
trait PipelineHelper {

  /**
   * 构建stream管道
   * @param terminateSink 终止操作管道
   * @return 返回管道中的第一个Sink节点（各个管道之间通过downstream串联起来）
   */
  def buildStreamPipeline(terminateSink: TerminateSink): Sink

  /**
   * 运行给定的管道，并计算出最终的结果
   * @param streamPipeline 需要运行的管道
   * @param toBeProcessedDatasets 需要被处理的数据迭代器
   */
  def runStreamPipeline(streamPipeline: Sink, toBeProcessedDatasets: Iterator[_]): Unit
}
