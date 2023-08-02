package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-03
 */
public class LtExpressionASTNode extends DefaultOperatorASTNode {

    public LtExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.LT);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {

    }
}
