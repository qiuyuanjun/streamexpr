package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import org.springframework.lang.Nullable

import java.lang.reflect.Constructor
import java.util
import java.util.Objects
import java.util.concurrent.ConcurrentMap
import scala.jdk.javaapi.CollectionConverters

/**
 * stream工具类
 * @author qiuyj
 * @since 2023-08-31
 */
object StreamUtils {

  def makeStream(@Nullable source: collection.Seq[_]): Stream =
    new Head(if (Objects.isNull(source)) Seq.empty else source)

  def makeStream(@Nullable source: util.List[_]): Stream =
    makeStream(CollectionConverters.asScala(source))

  private[stream] def makeRef(registerOps: ConcurrentMap[String, Constructor[Any]],
                              prevStream : Stream,
                              streamOp: StreamOp): ReferencePipeline = {
    Objects.requireNonNull(prevStream, "the parameter 'prevStream' is null")
    Objects.requireNonNull(streamOp, "the parameter 'streamOp' is null")
    null
  }
}
