package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-03
 */
public class EqExpressionASTNode extends DefaultOperatorASTNode {

    public EqExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.EQ);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitEqExpression(this);
    }
}
