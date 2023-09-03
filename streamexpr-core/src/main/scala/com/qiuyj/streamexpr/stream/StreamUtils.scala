package com.qiuyj.streamexpr.stream

import com.qiuyj.streamexpr.StreamContext
import com.qiuyj.streamexpr.api.utils.StringUtils
import org.springframework.lang.{NonNull, Nullable}

import java.lang.reflect.Constructor
import java.util
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

  def makeStream(@Nullable source: collection.Seq[_]): Stream =
    new Head(if (Objects.isNull(source)) Seq.empty else source, new StreamContext)

  def makeStream(@Nullable source: util.List[_]): Stream =
    makeStream(CollectionConverters.asScala(source))

  /**
   * 创建stream操作实例
   * @param registerStreamOps 所有注册的stream操作容器
   * @param opName stream操作名
   * @param parameters 构造函数的参数
   * @tparam A stream操作对象
   * @return stream操作对象
   */
  private[stream] def makeRef[A](registerStreamOps: ConcurrentMap[String, Constructor[_ <: A]],
                                 opName: String,
                                 parameters: Array[_]): A = {
    val constructor = registerStreamOps.get(opName)
    if (Objects.isNull(constructor)) {
      throw new IllegalStateException(s"Unregistered stream op: $opName")
    }
    val tryValue = Try(constructor.newInstance(parameters: _*))
    if (tryValue.isFailure) {
      throw new IllegalStateException(s"Unable to initialize the stream op: $opName object",
        tryValue.failed.get)
    }
    else {
      tryValue.get
    }
  }

  /**
   * 注册stream流操作（中间操作和终止操作）
   * @param streamOpContainer 存储stream流操作的容器
   * @param opName 操作名称
   * @param opClass 操作的Class对象
   * @param parameterTypes 操作构造函数的参数类型
   * @tparam A 操作类型
   */
  private[stream] def registerStreamOp[A](@NonNull streamOpContainer: ConcurrentMap[String, Constructor[_ <: A]],
                                          opName: String,
                                          opClass: Class[_ <: A],
                                          @NonNull parameterTypes: Array[Class[_]]): Unit = {
    Objects.requireNonNull(opClass, "stream op class is null")
    assert(StringUtils.isNotEmpty(opName), "stream op name is empty")
    val tryGetConstructor: Try[Constructor[_ <: A]] = Try(opClass.getDeclaredConstructor(parameterTypes: _*))
    if (tryGetConstructor.isFailure) {
      throw new IllegalStateException("Unable to register stream op because get the stream op's constructor exception",
        tryGetConstructor.failed.get)
    }
    val constructor = tryGetConstructor.get
    if (Objects.nonNull(streamOpContainer.putIfAbsent(opName, constructor))) {
      throw new IllegalStateException(s"Repeat registration stream op: $opName")
    }
    else {
      constructor.trySetAccessible
    }
  }
}
