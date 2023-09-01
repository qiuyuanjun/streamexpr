package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import org.springframework.lang.Nullable

import java.util
import java.util.Objects
import scala.jdk.javaapi.CollectionConverters

/**
 * stream工具类
 * @author qiuyj
 * @since 2023-08-31
 */
object StreamUtils {

  def makeStream(@Nullable source: collection.Seq[_]): Stream =
    new ReferencePipeline.Head(if (Objects.isNull(source)) Seq.empty else source)

  def makeStream(@Nullable source: util.List[_]): Stream =
    makeStream(CollectionConverters.asScala(source))

  private[stream] def makeRef(prev: Stream, intermediateOp: StreamOp): ReferencePipeline = {
    Objects.requireNonNull(prev, "the parameter 'prev' is null")
    Objects.requireNonNull(intermediateOp, "the parameter 'intermediateOp' is null")
    null
  }
}
