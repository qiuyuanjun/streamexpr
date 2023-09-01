package com.qiuyj.streamexpr.stream

import java.util
import scala.jdk.javaapi.CollectionConverters

/**
 * @author qiuyj
 * @since 2023-08-23
 */
object StreamSupport {

  def stream(source: collection.Seq[_]): Stream = {
    new ReferencePipeline.Head(source)
  }

  def stream(source: util.List[_]): Stream = {
    new ReferencePipeline.Head(CollectionConverters.asScala(source))
  }
}
