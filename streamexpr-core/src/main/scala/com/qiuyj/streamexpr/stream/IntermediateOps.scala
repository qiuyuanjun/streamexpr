package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr.api.utils.StringUtils
import com.qiuyj.streamexpr.stream.StreamUtils.EitherThen

import java.lang.reflect.Constructor
import java.util
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}
import java.util.{Comparator, Objects}

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

  registerIntermediateOp("split",    classOf[Split])
  registerIntermediateOp("filter",   classOf[Filter])
  registerIntermediateOp("distinct", classOf[Distinct])
  registerIntermediateOp("sort",     classOf[Sort])

  private class Filter(private[this] val prevStream: Stream,
                       private[this] val filterOp: StreamOp) extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        override def accept(elem: Any): Unit = {
          val filterCondition = StreamUtils.getParameterValue(elem, filterOp)
          filterCondition match {
            case result: Boolean =>
              if (result) {
                downstream.accept(elem)
              }
            case _ => throw new IllegalStateException("Boolean result expression expect!")
          }
        }
      }
    }
  }

  private class Split(private[this] val prevStream: Stream,
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

  /**
   * distinct(DISTINCT_FIELD)
   * 表示按照DISTINCT_FIELD字段值进行去重
   */
  private class Distinct(private[this] val prevStream: Stream,
                         private[this] val distinctOp: StreamOp) extends ReferencePipeline(prevStream) {
    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        private[this] var set: util.Set[Any] = _

        override protected def doBegin(): Unit = set = new util.HashSet[Any]

        override def accept(elem: Any): Unit = {
          if (set.add(StreamUtils.getParameterValue(elem, distinctOp))) {
            downstream.accept(elem)
          }
        }

        override protected def doEnd(): Unit = set = null
      }
    }
  }

  /**
   * sort(SORT_FIELD, 'DESC', false)
   * 按照SORT_FIELD字段倒叙排序，并且null值放在最前面
   */
  private class Sort(private[this] val prevStream: Stream,
                     private[this] val sortOp: StreamOp) extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        private[this] var arrayList: util.List[Comparable[Any]] = _

        override def begin(): Unit = {
          arrayList = new util.ArrayList[Comparable[Any]]
        }

        override def accept(elem: Any): Unit = {
          arrayList.add(StreamUtils.getParameterValue(elem, sortOp).asInstanceOf[Comparable[Any]])
        }

        override def end(): Unit = {
          downstream.begin()
          if (arrayList.size > 0) {
            arrayList.sort(getComparator)
            val sortedArrayList = arrayList.iterator
            while (sortedArrayList.hasNext && !cancelledRequest) {
              downstream.accept(sortedArrayList.next())
            }
          }
          downstream.end()
          arrayList = null
        }

        private[this] def getComparator: Comparator[Comparable[Any]] = {
          // 顺序，ASC表示顺序，DESC表示倒序，默认是ASC
          val order = StreamUtils.getParameterValueAsString(null, sortOp, 1)
          // null值的顺序，是否是在最后面，默认是最后面，true表示最后面，false表示最前面
          val nullsOrder = StreamUtils.getParameterValue(null, sortOp, 2)
          EitherThen(StringUtils.isEmpty(order) || "ASC".equalsIgnoreCase(order), Comparator.naturalOrder[Comparable[Any]], Comparator.reverseOrder[Comparable[Any]])
            .mergeThen(prevValue => Either.cond(Objects.isNull(nullsOrder) || nullsOrder.asInstanceOf[Boolean], Comparator.nullsLast(prevValue), Comparator.nullsFirst(prevValue)))
            .get
        }
      }
    }
  }
}
