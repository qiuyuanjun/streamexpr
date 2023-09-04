package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr.api.utils.StringUtils

import java.lang.reflect.Constructor
import java.util.StringJoiner
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

/**
 * 定义和存储所有的终止操作
 *
 * @author qiuyj
 * @since 2023-09-02
 */
object TerminateOps {

  private[this] val KNOWN_TERMINATE_OPS: ConcurrentMap[String, Constructor[_ <: TerminateOp]] = new ConcurrentHashMap

  private[stream] def makeRef(terminateOp: StreamOp): TerminateOp = {
    StreamUtils.makeRef(KNOWN_TERMINATE_OPS,
      terminateOp.getOpName,
      Array.apply(terminateOp))
  }

  private def registerTerminateOp(opName: String, terminateOpClass: Class[_ <: TerminateOp]): Unit = {
    StreamUtils.registerStreamOp(KNOWN_TERMINATE_OPS,
      opName,
      terminateOpClass,
      Array.apply(classOf[StreamOp]))
  }

  registerTerminateOp("concat", classOf[Concat])
  registerTerminateOp("toArray", classOf[ToArray])

  private abstract class TerminateOpSink extends TerminateSink with TerminateOp {

    override def evaluateSequential(pipelineHelper: PipelineHelper, toBeProcessedDatasets: Iterator[_]): Any = {
      // 1. 构建管道
      val streamPipeline = pipelineHelper.buildStreamPipeline(this)
      // 2. 将要处理的集合的所有数据依次流入管道处理
      pipelineHelper.runStreamPipeline(streamPipeline, toBeProcessedDatasets)
      // 3. 获取管道处理的最终结果
      get
    }
  }

  private class Concat(private[this] val concatOp: StreamOp) extends TerminateOpSink {

    private[this] var builder: StringJoiner = _

    override def begin(): Unit = {
      builder = new StringJoiner(
        StringUtils.defaultIfEmpty(StreamUtils.getParameterValueAsString(null, concatOp, 1)),
        StringUtils.defaultIfEmpty(StreamUtils.getParameterValueAsString(null, concatOp, 2)),
        StringUtils.defaultIfEmpty(StreamUtils.getParameterValueAsString(null, concatOp, 3))
      )
    }

    override def get: Any = builder.toString

    override def accept(elem: Any): Unit =
      builder.add(StreamUtils.getParameterValueAsString(elem, concatOp))

  }

  private class ToArray(private[this] val toArrayOp: StreamOp) extends TerminateOpSink {

    override def begin(): Unit = {

    }

    override def get: Any = {

    }

    override def accept(t: Any): Unit = {

    }
  }
}
