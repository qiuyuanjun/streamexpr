package com.qiuyj.streamexpr.api.ast;

import java.util.Iterator;

/**
 * or表达式抽象语法树节点
 * @author qiuyj
 * @since 2023-07-30
 */
public class OrExpressionASTNode extends LogicExpressionASTNode {

    public OrExpressionASTNode(ExpressionASTNode... orPart) {
        super(LogicOperator.OR, orPart);
    }

}
