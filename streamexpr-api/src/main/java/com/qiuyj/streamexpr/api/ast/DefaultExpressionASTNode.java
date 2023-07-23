package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-07-23
 */
public class DefaultExpressionASTNode extends AbstractASTNode implements ExpressionASTNode {

    public DefaultExpressionASTNode(AbstractASTNode left, AbstractASTNode right, AbstractASTNode operand) {
        super(left, operand, right);
    }

    @Override
    public <T extends Visitor> void visit(T visitor) {
        visitor.visitExpression(this);
    }

    @Override
    public ASTNode getLeft() {
        return fastGetChildASTNode(0);
    }

    @Override
    public ASTNode getRight() {
        return fastGetChildASTNode(2);
    }

    @Override
    public ASTNode getOperand() {
        return fastGetChildASTNode(1);
    }
}
