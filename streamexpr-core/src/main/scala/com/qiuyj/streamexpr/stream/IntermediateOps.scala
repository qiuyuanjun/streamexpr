package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp

import java.lang.reflect.Constructor
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

/**
 * 定义和存储所有的中间操作
 *
 * @author qiuyj
 * @since 2023-09-02
 */
object IntermediateOps {

  private[this] val KNOWN_INTERMEDIATE_OPS: ConcurrentMap[String, Constructor[_ <: ReferencePipeline]] = new ConcurrentHashMap

  private[stream] def makeRef(prevStream: Stream, intermediateOp: StreamOp): ReferencePipeline = {
    null
  }
}
