package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamContext

/**
 * stream流上下文获取接口
 * @author qiuyj
 * @since 2023-09-09
 */
trait StreamContextCapable {

  /**
   * 获取stream流上下文
   * @return stream流上下文
   */
  def getStreamContext: StreamContext
}
