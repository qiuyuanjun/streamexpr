package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr.api.utils.StringUtils
import com.qiuyj.streamexpr.{StreamContext, jList}
import org.springframework.lang.{NonNull, Nullable}

import java.lang.reflect.Constructor
import java.util.Objects
import java.util.concurrent.ConcurrentMap
import scala.jdk.javaapi.CollectionConverters
import scala.util.Try

/**
 * stream工具类
 * @author qiuyj
 * @since 2023-08-31
 */
object StreamUtils {

  /**
   * 获取给定操作中下标为0的参数对应的值
   * @param valueContext 上下文
   * @param streamOp     操作StreamOp对象，里面可以获取所有的参数列表
   * @return 下标为0的参数对应的值
   */
  def getParameterValue(streamContext: StreamContext,
                        valueContext: Any,
                        streamOp: StreamOp): Any =
    getParameterValue(streamContext, valueContext, streamOp, 0)

  /**
   * 获取给定操作中给定下标的参数对应的值
   * @param valueContext   上下文
   * @param streamOp       操作StreamOp对象，里面可以获取所有的参数列表
   * @param parameterIndex 要获取的参数的下标
   * @return 对应下标参数对应的值
   */
  def getParameterValue(streamContext: StreamContext,
                        valueContext: Any,
                        streamOp: StreamOp,
                        parameterIndex: Int): Any = {
    safeGetParameterValueAt(streamContext, valueContext, streamOp, parameterIndex)
      .orNull
  }

  def getParameterValueAsString(streamContext: StreamContext,
                                valueContext: Any,
                                streamOp: StreamOp): String =
    getParameterValueAsString(streamContext, valueContext, streamOp, 0)

  def getParameterValueAsString(streamContext: StreamContext,
                                valueContext: Any,
                                streamOp: StreamOp,
                                parameterIndex: Int): String = {
    safeGetParameterValueAt(streamContext, valueContext, streamOp, parameterIndex)
      .map(_.toString)
      .orNull
  }

  /**
   * 获取给定操作中下标为0的参数对应的值（boolean类型，不为null）
   * @param valueContext 上下文
   * @param streamOp     操作StreamOp对象，里面可以获取所有的参数列表
   * @return 对应参数的值（boolean类型）
   */
  @NonNull
  def getParameterValueAsBooleanNonNull(streamContext: StreamContext,
                                        valueContext: Any,
                                        streamOp: StreamOp): Boolean = {
    getParameterValueAsBooleanNonNull(streamContext, valueContext, streamOp, 0)
  }

  /**
   * 获取给定操作中给定下标的参数对应的值（boolean类型，不为null）
   * @param valueContext   上下文
   * @param streamOp       操作StreamOp对象，里面可以获取所有的参数列表
   * @param parameterIndex 要获取的参数的下标
   * @return 对应参数的值（boolean类型）
   */
  @NonNull
  def getParameterValueAsBooleanNonNull(streamContext: StreamContext,
                                        valueContext: Any,
                                        streamOp: StreamOp,
                                        parameterIndex: Int): Boolean = {
    safeGetParameterValueAt(streamContext, valueContext, streamOp, parameterIndex)
      .map {
        case booleanValue: Boolean => booleanValue
        case _ => throw new IllegalStateException("Boolean result expression expect!")
      }
      .getOrElse(throw new IllegalStateException("Boolean result expression expect!"))
  }

  private[this] def safeGetParameterValueAt(streamContext: StreamContext,
                                            valueContext: Any,
                                            streamOp: StreamOp,
                                            parameterIndex: Int): Option[Any] = {
    val parameters = streamOp.getParameters
    if (parameterIndex < 0 || parameterIndex > parameters.size - 1)
      None
    else
      Some(parameters(parameterIndex)).map(_.getValue(streamContext, valueContext))
  }

  def makeStream(@Nullable source: collection.Seq[_]): Stream =
    makeStream(source, new StreamContext)

  def makeStream(@Nullable source: collection.Seq[_], @NonNull streamContext: StreamContext): Stream =
    new Head(if (Objects.isNull(source)) Seq.empty else source, streamContext)

  def makeStream(@Nullable source: jList[_]): Stream =
    makeStream(CollectionConverters.asScala(source))

  def makeStream(@Nullable source: jList[_], @NonNull streamContext: StreamContext): Stream =
    makeStream(CollectionConverters.asScala(source), streamContext)

  /**
   * 创建stream操作实例
   * @param registerStreamOps 所有注册的stream操作容器
   * @param opName            stream操作名
   * @param parameters        构造函数的参数
   * @tparam A stream操作对象
   * @return stream操作对象
   */
  private[stream] def makeRef[A](registerStreamOps: ConcurrentMap[String, Constructor[_ <: A]],
                                 opName: String,
                                 parameters: Array[Any]): A = {
    val constructor = registerStreamOps.get(opName)
    if (Objects.isNull(constructor)) {
      throw new IllegalStateException(s"Unregistered stream op: $opName")
    }
    Try(constructor.newInstance(parameters: _*)).fold(
      e => throw new IllegalStateException(s"Unable to initialize the stream op: $opName object", e),
      identity
    )
  }

  /**
   * 注册stream流操作（中间操作和终止操作）
   * @param streamOpContainer 存储stream流操作的容器
   * @param opName            操作名称
   * @param opClass           操作的Class对象
   * @param parameterTypes    操作构造函数的参数类型
   * @tparam A 操作类型
   */
  private[stream] def registerStreamOp[A](@NonNull streamOpContainer: ConcurrentMap[String, Constructor[_ <: A]],
                                          opName: String,
                                          opClass: Class[_ <: A],
                                          @NonNull parameterTypes: Array[Class[_]]): Unit = {
    Objects.requireNonNull(opClass, "stream op class is null")
    assertTrue(StringUtils.isNotEmpty(opName), "stream op name is empty")
    val constructor = Try(opClass.getDeclaredConstructor(parameterTypes: _*)).fold(
      e => throw new IllegalStateException("Unable to register stream op because get the stream op's constructor exception", e),
      identity
    )
    if (Objects.nonNull(streamOpContainer.putIfAbsent(opName, constructor))) {
      throw new IllegalStateException(s"Repeat registration stream op: $opName")
    }
    else {
      constructor.trySetAccessible
    }
  }

  implicit private[stream] class EitherThen[A](private[this] val value: Either[A, A]) {

    def mergeThen[B](fn: A => Either[B, B]): EitherThen[B] = {
      fn(get)
    }

    def `then`[B](fn: Either[A, A] => Either[B, B]): EitherThen[B] = {
      fn(value)
    }

    def get: A = value.merge
  }

  private[stream] object EitherThen {

    def apply[A](test: Boolean, right: => A, left: => A): EitherThen[A] = {
      Either.cond(test, right, left)
    }
  }

  /**
   * 断言
   * @param condition    断言条件
   * @param errorMessage 如果断言条件是false，那么抛出异常，该字段用于获取异常信息
   */
  private[stream] def assertTrue(condition: Boolean, errorMessage: => String): Unit = {
    if (!condition) {
      throw new IllegalStateException(errorMessage)
    }
  }
}
