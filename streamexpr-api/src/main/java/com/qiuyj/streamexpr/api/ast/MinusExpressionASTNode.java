package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-02
 */
public class MinusExpressionASTNode extends DefaultOperatorASTNode {

    public MinusExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.MINUS);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitMinusExpression(this);
    }
}
