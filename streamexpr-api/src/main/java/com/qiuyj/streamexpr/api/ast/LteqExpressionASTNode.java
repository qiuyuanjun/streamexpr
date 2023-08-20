package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-03
 */
public class LteqExpressionASTNode extends DefaultOperatorASTNode {

    public LteqExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.LTEQ);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitLteqExpression(this);
    }
}
