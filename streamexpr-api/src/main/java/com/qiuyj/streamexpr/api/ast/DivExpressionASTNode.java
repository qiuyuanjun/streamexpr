package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-04
 */
public class DivExpressionASTNode extends DefaultOperatorASTNode {

    public DivExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.DIV);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitDivExpression(this);
    }
}
