package com.qiuyj.streamexpr

import com.qiuyj.streamexpr.api.{EvaluationContext, EvaluationException}

import java.util.{Collections, Objects}

/**
 * stream上下文
 * @author qiuyj
 * @since 2023-09-03
 */
class StreamContext(private[this] val source: jList[_],
                    private[this] val context: jMap[String, _])
    extends EvaluationContext {

  private[this] val contextValueHolder: jMap[String, _] =
    if (Objects.isNull(context)) Collections.emptyMap
    else context

  def this() = this(null, null)

  def getSource: jList[_] = source

  @throws[EvaluationException]
  override def getValue(key: String): Any = {
    if (!contextValueHolder.containsKey(key)) {
      throw new EvaluationException(s"Can not find property '$key' in context value holder")
    }
    contextValueHolder.get(key)
  }
}
