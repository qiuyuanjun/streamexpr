package com.qiuyj.streamexpr.api.ast;

/**
 * and表达式抽象语法树节点
 * @author qiuyj
 * @since 2023-07-30
 */
public class AndExpressionASTNode extends LogicExpressionASTNode {

    public AndExpressionASTNode(ExpressionASTNode... logicPart) {
        super(LogicOperator.AND, logicPart);
    }
}
