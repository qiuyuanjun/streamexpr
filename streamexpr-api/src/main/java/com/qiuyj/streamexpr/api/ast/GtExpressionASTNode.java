package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-03
 */
public class GtExpressionASTNode extends DefaultOperatorASTNode {

    public GtExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.GT);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitGtExpression(this);
    }
}
