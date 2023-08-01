package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-07-23
 */
public class DefaultOperatorASTNode extends AbstractASTNode implements OperatorASTNode {

    private final Operator operator;

    public DefaultOperatorASTNode(ASTNode left, ASTNode right, String operator) {
        super(left, right);
        this.operator = Operator.getByName(operator);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitOperator(this);
    }

    @Override
    public ASTNode getLeftOperand() {
        return fastGetChildASTNode(0);
    }

    @Override
    public ASTNode getRightOperand() {
        return fastGetChildASTNode(1);
    }

    @Override
    public Operator getOperator() {
        return operator;
    }
}
