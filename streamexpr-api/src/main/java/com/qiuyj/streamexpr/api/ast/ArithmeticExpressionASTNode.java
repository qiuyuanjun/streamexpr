package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-01
 */
public class ArithmeticExpressionASTNode extends AbstractASTNode implements ExpressionASTNode {

    private final char arithmetic;

    public ArithmeticExpressionASTNode(ASTNode first, ASTNode second, char arithmetic) {
        super(first, second);
        this.arithmetic = arithmetic;
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {

    }
}
