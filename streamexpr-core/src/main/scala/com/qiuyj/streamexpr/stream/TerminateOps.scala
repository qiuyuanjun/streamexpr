package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr.api.utils.StringUtils

import java.lang.reflect.Constructor
import java.util
import java.util.{Objects, StringJoiner}
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}
import scala.util.Try

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

  /**
   * 带有取消循环功能的终止操作Sink管道
   */
  private abstract class CancellableTerminateOpSink extends TerminateOpSink {

    /**
     * 是否需要取消循环标志
     */
    private[this] var cancelled: Boolean = _

    /**
     * 设置取消循环标志为true，调用该方法之后，不会进行下一次循环
     */
    protected def cancel(): Unit = cancelled = true

    override def cancelledRequest: Boolean = cancelled
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

  private class ToArray(private[this] val toArrayOp: StreamOp) extends CancellableTerminateOpSink {

    private[this] var array: util.List[Any] = _

    override def begin(): Unit = {
      val len = StreamUtils.getParameterValueAsString(null, toArrayOp, 1)
      array = if (StringUtils.isEmpty(len)) {
        new util.ArrayList[Any]
      }
      else {
        Try(Integer.parseInt(len)).fold(
          _ => new util.ArrayList[Any],
          new FixedSizeArrayList(_)
        )
      }
    }

    override def get: Any = array.toArray

    override def accept(elem: Any): Unit = {
      if (!array.add(StreamUtils.getParameterValue(elem, toArrayOp))) {
        cancel()
      }
    }
  }

  private[this] class FixedSizeArrayList(private[this] val arraySize: Int) extends util.AbstractList[Any] {

    private[this] val array: Array[Any] = new Array[Any](arraySize)

    private[this] var index: Int = _

    override def get(index: Int): Any = {
      Objects.checkIndex(index, arraySize)
      array(index)
    }

    override def size(): Int = index

    override def add(e: Any): Boolean = {
      if (index >= arraySize) {
        false
      }
      else {
        array(index) = e
        index += 1
        true
      }
    }

    override def toArray: Array[Any] = array
  }
}
