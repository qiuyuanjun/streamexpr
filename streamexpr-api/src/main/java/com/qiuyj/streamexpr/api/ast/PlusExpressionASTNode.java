package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-02
 */
public class PlusExpressionASTNode extends DefaultOperatorASTNode {

    public PlusExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.PLUS);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitPlusExpression(this);
    }
}
