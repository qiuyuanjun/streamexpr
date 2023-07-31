package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-07-23
 */
public class DefaultOperatorASTNode extends AbstractASTNode implements OperatorASTNode {

    public DefaultOperatorASTNode(ASTNode left, ASTNode right, ASTNode operand) {
        super(left, operand, right);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
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
