package com.qiuyj.streamexpr.stream

/**
 * @author qiuyj
 * @since 2023-09-02
 */
abstract class ReferencePipeline(private[this] val prevStream: AbstractStream) extends AbstractStream(prevStream) {
}
