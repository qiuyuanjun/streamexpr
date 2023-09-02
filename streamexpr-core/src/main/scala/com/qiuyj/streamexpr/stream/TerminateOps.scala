package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp

import java.lang.reflect.Constructor
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

/**
 * 定义和存储所有的终止操作
 *
 * @author qiuyj
 * @since 2023-09-02
 */
object TerminateOps {

  private val KNOWN_TERMINATE_OPS: ConcurrentMap[String, Constructor[_ <: TerminateOp]] = new ConcurrentHashMap

  private[stream] def makeRef(terminateOp: StreamOp): TerminateOp = {
    null
  }
}
