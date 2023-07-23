package com.qiuyj.streamexpr.api.ast;
/**
 * @author qiuyj
 * @since 2023-07-23
 */
public interface ASTNode {

    /**
     * 采用访问者模式，访问当前节点
     */
    <T extends Visitor> void visit(T visitor);

    /**
     * 访问器，内部提供各种节点的访问方法
     */
    interface Visitor {

        void visitExpression(ExpressionASTNode expression);
    }
}
