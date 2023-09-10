package com.qiuyj

import java.util

/**
 * 包对象，用于定义别名
 * @author qiuyj
 * @since 2023-09-10
 */
package object streamexpr {

  /**
   * java.util.List的别名jList
   * @tparam E 泛型，存储的元素的类型
   */
  type jList[E] = util.List[E]

  /**
   * java.util.ArrayList的别名jArrayList
   * @tparam E 泛型，存储的元素的类型
   */
  type jArrayList[E] = util.ArrayList[E]

  /**
   * java.util.AbstractList的别名jAbstractList
   * @tparam E 泛型，存储的元素的类型
   */
  type jAbstractList[E] = util.AbstractList[E]

  /**
   * java.util.Map的别名jMap
   * @tparam K 泛型，存储的key的类型
   * @tparam V 泛型，存储的value的类型
   */
  type jMap[K, V] = util.Map[K, V]

  /**
   * java.util.HashMap的别名jHashMap
   * @tparam K 泛型，存储的key的类型
   * @tparam V 泛型，存储的value的类型
   */
  type jHashMap[K, V] = util.HashMap[K, V]

  /**
   * java.util.Deque的别名jDeque
   * @tparam E 泛型，存储的元素的类型
   */
  type jDeque[E] = util.Deque[E]

  /**
   * java.util.ArrayDeque的别名jArrayDeque
   * @tparam E 泛型，存储的元素的类型
   */
  type jArrayDeque[E] = util.ArrayDeque[E]
}
