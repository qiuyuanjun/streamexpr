package com.qiuyj.streamexpr.api;

/**
 * 求值上下文，提供相关的字段值获取方法
 * @author qiuyj
 * @since 2023-09-10
 */
public interface EvaluationContext {

    /**
     * 根据传入的key获取对应的值
     * @param key 要获取的值的key
     * @apiNote 必须要能找到对应的key，否则抛出异常
     * @return 对应的值
     */
    Object getValue(String key) throws EvaluationException;
}
