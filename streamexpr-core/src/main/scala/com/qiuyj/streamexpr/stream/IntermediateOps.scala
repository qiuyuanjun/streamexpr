package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr.api.utils.StringUtils

import java.lang.reflect.Constructor
import java.util.Objects
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

/**
 * 定义和存储所有的中间操作
 *
 * @author qiuyj
 * @since 2023-09-02
 */
object IntermediateOps {

  /**
   * 存储所有已经注册的中间操作
   */
  private[this] val KNOWN_INTERMEDIATE_OPS: ConcurrentMap[String, Constructor[_ <: ReferencePipeline]] = new ConcurrentHashMap

  private[stream] def makeRef(prevStream: Stream, intermediateOp: StreamOp): ReferencePipeline = {
    StreamUtils.makeRef(KNOWN_INTERMEDIATE_OPS,
      intermediateOp.getOpName,
      Array.apply(prevStream, intermediateOp))
  }

  /**
   * 注册中间操作
   * @param opName 操作名称（唯一）
   * @param intermediateOpClass 中间操作的Class对象
   */
  private def registerIntermediateOp(opName: String, intermediateOpClass: Class[_ <: ReferencePipeline]): Unit = {
    StreamUtils.registerStreamOp(KNOWN_INTERMEDIATE_OPS,
      opName,
      intermediateOpClass,
      Array.apply(classOf[Stream], classOf[StreamOp]))
  }

  registerIntermediateOp("split", classOf[Split])
  registerIntermediateOp("filter", classOf[Filter])

  private[this] class Filter(private[this] val prevStream: Stream,
                             private[this] val filterOp: StreamOp) extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        override def accept(elem: Any): Unit = {

        }
      }
    }
  }

  private[this] class Split(private[this] val prevStream: Stream,
                            private[this] val splitOp: StreamOp) extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        /**
         * 分割符，如果没有填，那么默认是按照竖线分割
         */
        private[this] var separator: String = _

        override protected def doBegin(): Unit = {
          val configuredSeparator = StreamUtils.getParameterValueAsString(null, splitOp, 1)
          separator = StringUtils.defaultIfEmpty(configuredSeparator, "|")
        }

        override def accept(elem: Any): Unit = {
          val splitResult = StringUtils.split(StreamUtils.getParameterValue(elem, splitOp), separator)
          if (Objects.isNull(splitResult)) {
            downstream.accept(elem)
          }
          else {
            var i = 0
            val len = splitResult.length
            while (i < len) {
              downstream.accept(splitResult(i))
              i += 1
            }
          }
        }
      }
    }
  }
}
