package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamContext
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

  registerIntermediateOp("split",     classOf[Split])
  registerIntermediateOp("filter",    classOf[Filter])
  registerIntermediateOp("distinct",  classOf[Distinct])
  registerIntermediateOp("sort",      classOf[Sort])
  registerIntermediateOp("skip",      classOf[Skip])
  registerIntermediateOp("dropWhile", classOf[DropWhile])
  registerIntermediateOp("takeWhile", classOf[TakeWhile])

  private class Filter(private[this] val prevStream: Stream,
                       private[this] val filterOp: StreamOp)
      extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        override def accept(elem: Any, streamContext: StreamContext): Unit = {
          if (StreamUtils.getParameterValueAsBooleanNonNull(streamContext, elem, filterOp)) {
            downstream.accept(elem, streamContext)
          }
        }
      }
    }
  }

  private class Split(private[this] val prevStream: Stream,
                      private[this] val splitOp: StreamOp)
      extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        /**
         * 分割符，如果没有填，那么默认是按照竖线分割
         */
        private[this] var separator: String = _

        override protected def doBegin(streamContext: StreamContext): Unit = {
          val configuredSeparator = StreamUtils.getParameterValueAsString(streamContext, null, splitOp, 1)
          separator = StringUtils.defaultIfEmpty(configuredSeparator, "|")
        }

        override def accept(elem: Any, streamContext: StreamContext): Unit = {
          val splitResult = StringUtils.split(StreamUtils.getParameterValue(streamContext, elem, splitOp), separator)
          if (Objects.isNull(splitResult)) {
            downstream.accept(elem, streamContext)
          }
          else {
            var i = 0
            val len = splitResult.length
            while (i < len) {
              downstream.accept(splitResult(i), streamContext)
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
                         private[this] val distinctOp: StreamOp)
      extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        private[this] var set: util.Set[Any] = _

        override protected def doBegin(streamContext: StreamContext): Unit =
          set = new util.HashSet[Any]

        override def accept(elem: Any, streamContext: StreamContext): Unit = {
          if (set.add(StreamUtils.getParameterValue(streamContext, elem, distinctOp))) {
            downstream.accept(elem, streamContext)
          }
        }

        override protected def doEnd(streamContext: StreamContext): Unit =
          set = null
      }
    }
  }

  /**
   * sort(SORT_FIELD, 'DESC', false)
   * 按照SORT_FIELD字段倒叙排序，并且null值放在最前面
   */
  private class Sort(private[this] val prevStream: Stream,
                     private[this] val sortOp: StreamOp)
      extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        private[this] var arrayList: util.List[Comparable[Any]] = _

        override def begin(streamContext: StreamContext): Unit =
          arrayList = new util.ArrayList[Comparable[Any]]

        override def accept(elem: Any, streamContext: StreamContext): Unit = {
          arrayList.add(StreamUtils.getParameterValue(streamContext, elem, sortOp)
            .asInstanceOf[Comparable[Any]])
        }

        override def end(streamContext: StreamContext): Unit = {
          downstream.begin(streamContext)
          if (arrayList.size > 0) {
            arrayList.sort(getComparator(streamContext))
            val sortedArrayList = arrayList.iterator
            while (sortedArrayList.hasNext && !cancelledRequest) {
              downstream.accept(sortedArrayList.next(), streamContext)
            }
          }
          downstream.end(streamContext)
          arrayList = null
        }

        private[this] def getComparator(streamContext: StreamContext): Comparator[Comparable[Any]] = {
          // 顺序，ASC表示顺序，DESC表示倒序，默认是ASC
          val order = StreamUtils.getParameterValueAsString(streamContext, null, sortOp, 1)
          // null值的顺序，是否是在最后面，默认是最后面，true表示最后面，false表示最前面
          val nullsOrder = StreamUtils.getParameterValue(streamContext, null, sortOp, 2)
          EitherThen(StringUtils.isEmpty(order) || "ASC".equalsIgnoreCase(order), Comparator.naturalOrder[Comparable[Any]], Comparator.reverseOrder[Comparable[Any]])
            .mergeThen(prevValue => Either.cond(Objects.isNull(nullsOrder) || nullsOrder.asInstanceOf[Boolean], Comparator.nullsLast(prevValue), Comparator.nullsFirst(prevValue)))
            .get
        }
      }
    }
  }

  /**
   * skip(10)，表示跳过最开始的10个元素
   */
  private class Skip(private[this] val prevStream: Stream,
                     private[this] val skipOp: StreamOp)
      extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {

      new ChainedSink(downstream) {

        private[this] var skipN: Int = _

        override protected def doBegin(streamContext: StreamContext): Unit = {
          // 要求参数必须是数字字面量
          skipN = Objects.requireNonNull(
              StreamUtils.getParameterValue(streamContext, null, skipOp),
              "The first parameter of skip op cannot be null"
          ).asInstanceOf[Int]
          StreamUtils.assert(skipN >= 0, "The first parameter of skip op cannot be less than 0")
        }

        override def accept(elem: Any, streamContext: StreamContext): Unit = {
          if (skipN < 0) {
            downstream.accept(elem, streamContext)
          }
          else {
            skipN -= 1
          }
        }
      }
    }
  }

  private class DropWhile(private[this] val prevStream: Stream,
                          private[this] val dropWhileOp: StreamOp)
      extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        private[this] var takeElement: Boolean = _

        override def accept(elem: Any, streamContext: StreamContext): Unit = {
          if (takeElement || StreamUtils.getParameterValueAsBooleanNonNull(streamContext, elem, dropWhileOp)) {
            takeElement = true
            downstream.accept(elem, streamContext)
          }
        }
      }
    }
  }

  private class TakeWhile(private[this] val prevStream: Stream,
                          private[this] val takeWhileOp: StreamOp)
      extends ReferencePipeline(prevStream) {

    override protected def opWrapSink(downstream: Sink): Sink = {
      new ChainedSink(downstream) {

        private[this] var takeElement: Boolean = true

        override def accept(elem: Any, streamContext: StreamContext): Unit = {
//           if (takeElement
//               && (takeElement = StreamUtils.getParameterValueAsBooleanNonNull(elem, takeWhileOp))) {
//             downstream.accept(elem)
//           }
          // scala不支持上面这种写法
          if (takeElement && StreamUtils.getParameterValueAsBooleanNonNull(streamContext, elem, takeWhileOp)) {
            downstream.accept(elem, streamContext)
          }
          else {
            takeElement = false
          }
        }

        override def cancelledRequest: Boolean =
          !takeElement || super.cancelledRequest
      }
    }
  }
}
