package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr._
import com.qiuyj.streamexpr.api.utils.StringUtils

import java.lang.reflect.Constructor
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}
import java.util.{Objects, StringJoiner}
import scala.util.Try

/**
 * 定义和存储所有的终止操作
 *
 * @author qiuyj
 * @since 2023-09-02
 */
object TerminateOps {

  private[this] val KNOWN_TERMINATE_OPS: ConcurrentMap[String, Constructor[_ <: TerminateOp]] = new ConcurrentHashMap

  private[stream] def makeRef(streamContext: StreamContext, terminateOp: StreamOp): TerminateOp = {
    StreamUtils.makeRef(KNOWN_TERMINATE_OPS,
      terminateOp.getOpName,
      Array.apply(streamContext, terminateOp))
  }

  private def registerTerminateOp(opName: String, terminateOpClass: Class[_ <: TerminateOp]): Unit = {
    StreamUtils.registerStreamOp(KNOWN_TERMINATE_OPS,
      opName,
      terminateOpClass,
      Array.apply(classOf[StreamContext], classOf[StreamOp]))
  }

  registerTerminateOp("concat",    classOf[Concat])
  registerTerminateOp("toArray",   classOf[ToArray])
  registerTerminateOp("toMap",     classOf[ToMap])
  registerTerminateOp("findFirst", classOf[FindFirst])

  private abstract class TerminateOpSink(private[this] val streamContext: StreamContext)
      extends TerminateSink(streamContext)
      with TerminateOp {

    override def evaluateSequential(pipelineHelper: PipelineHelper, toBeProcessedDatasets: Iterator[_]): Any = {
      // 1. 构建管道
      val streamPipeline = pipelineHelper.buildStreamPipeline(this)
      // 2. 将要处理的集合的所有数据依次流入管道处理
      pipelineHelper.runStreamPipeline(getStreamContext, streamPipeline, toBeProcessedDatasets)
      // 3. 获取管道处理的最终结果
      get
    }
  }

  /**
   * 带有取消循环功能的终止操作Sink管道
   */
  private abstract class CancellableTerminateOpSink(private[this] val streamContext: StreamContext)
      extends TerminateOpSink(streamContext) {

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

  private class Concat(private[this] val streamContext: StreamContext,
                       private[this] val concatOp: StreamOp)
      extends TerminateOpSink(streamContext) {

    private[this] var builder: StringJoiner = _

    override def begin(streamContext: StreamContext): Unit = {
      builder = new StringJoiner(
        StringUtils.defaultIfEmpty(StreamUtils.getParameterValueAsString(streamContext, null, concatOp, 1)),
        StringUtils.defaultIfEmpty(StreamUtils.getParameterValueAsString(streamContext, null, concatOp, 2)),
        StringUtils.defaultIfEmpty(StreamUtils.getParameterValueAsString(streamContext, null, concatOp, 3))
      )
    }

    override def get: Any = builder.toString

    override def accept(elem: Any, streamContext: StreamContext): Unit =
      builder.add(StreamUtils.getParameterValueAsString(streamContext, elem, concatOp))

  }

  private class ToArray(private[this] val streamContext: StreamContext,
                        private[this] val toArrayOp: StreamOp)
      extends CancellableTerminateOpSink(streamContext) {

    private[this] var arrayList: jList[Any] = _

    override def begin(streamContext: StreamContext): Unit = {
      val len = StreamUtils.getParameterValueAsString(streamContext, null, toArrayOp, 1)
      arrayList = if (StringUtils.isEmpty(len)) {
        new jArrayList[Any]
      }
      else {
        Try(Integer.parseInt(len)).fold(
          _ => new jArrayList[Any],
          new FixedSizeArrayList(_)
        )
      }
    }

    override def get: Any = arrayList.toArray

    override def accept(elem: Any, streamContext: StreamContext): Unit = {
      arrayList.add(StreamUtils.getParameterValue(streamContext, elem, toArrayOp))
      arrayList match {
        case array: FixedSizeArrayList if array.isFull => cancel()
        case _ =>
      }
    }
  }

  private class FixedSizeArrayList(private[this] val arraySize: Int)
      extends jAbstractList[Any] {

    private[this] val array = new Array[Any](arraySize)

    /**
     * 存储下一个添加数据的下标
     */
    private[this] var index: Int = _

    override def get(index: Int): Any = {
      Objects.checkIndex(index, arraySize)
      array(index)
    }

    override def size(): Int = index

    override def add(e: Any): Boolean = {
      if (isFull) {
        false
      }
      else {
        array(index) = e
        index += 1
        true
      }
    }

    override def toArray: Array[Any] = array

    /**
     * 判断当前数组是否满了
     * @return 如果满了，返回true，否则返回false
     */
    private[TerminateOps] def isFull: Boolean = index >= arraySize
  }

  private class ToMap(private[this] val streamContext: StreamContext,
                      private[this] val toMapOp: StreamOp)
      extends TerminateOpSink(streamContext) {

    private[this] var map: jMap[Any, Any] = _

    override def begin(streamContext: StreamContext): Unit =
      map = new jHashMap[Any, Any]

    override def accept(elem: Any, streamContext: StreamContext): Unit = {
      val key = StreamUtils.getParameterValue(streamContext, elem, toMapOp)
      val value = StreamUtils.getParameterValue(streamContext, elem, toMapOp, 1)
      map.put(key, value)
    }

    override def get: Any = map
  }

  private class FindFirst(private[this] val streamContext: StreamContext,
                          private[this] val findFirstOp: StreamOp)
      extends CancellableTerminateOpSink(streamContext) {

    private[this] var firstValue: Any = _

    /**
     * 是否不为null的标识，默认允许为null
     */
    private[this] var nonNull: Boolean = _

    override def begin(streamContext: StreamContext): Unit = {
      val optionalNonNull = StreamUtils.getParameterValue(streamContext, null, findFirstOp, 1)
      nonNull = Objects.isNull(optionalNonNull) || optionalNonNull.asInstanceOf[Boolean]
    }

    override def get: Any = firstValue

    override def accept(elem: Any, streamContext: StreamContext): Unit = {
      firstValue = StreamUtils.getParameterValue(streamContext, elem, findFirstOp)
      if (!nonNull || Objects.nonNull(firstValue)) {
        cancel()
      }
    }
  }
}
