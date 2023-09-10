package com.qiuyj.streamexpr.api;

/**
 * 语法解析器
 * @author qiuyj
 * @since 2023-06-29
 */
public interface Parser<T extends Expression<? extends EvaluationContext>> {

    /**
     * 根据词法解析器，构建对应的抽象语法树，然后封装成对应的表达式对象
     * @return {@link Expression}子类（对应的表达式解析对象，内部包含抽象语法树节点）
     */
    T parseExpression();
}
