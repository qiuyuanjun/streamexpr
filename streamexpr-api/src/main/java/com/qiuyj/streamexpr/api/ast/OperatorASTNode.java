package com.qiuyj.streamexpr.api.ast;

/**
 * 代表一个操作符的抽象语法树节点
 * @author qiuyj
 * @since 2023-07-23
 */
public interface OperatorASTNode extends ExpressionASTNode {

    /**
     * 得到左操作数
     * @return 左操作数对应的抽象语法树节点
     */
    ASTNode getLeftOperand();

    /**
     * 得到右操作数
     * @return 右操作数对应的抽象语法树节点
     */
    ASTNode getRightOperand();

    /**
     * 得到操作符
     * @return 操作符枚举
     */
    Operator getOperator();

    enum Operator {

        EQ, NEQ, GT, GTEQ, LT, LTEQ;

        public static Operator getByName(String name) {
            return null;
        }
    }
}
