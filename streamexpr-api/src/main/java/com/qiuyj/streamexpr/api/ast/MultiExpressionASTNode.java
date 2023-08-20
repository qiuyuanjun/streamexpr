package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-04
 */
public class MultiExpressionASTNode extends DefaultOperatorASTNode {

    public MultiExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.MULTI);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitMultiExpression(this);
    }
}
