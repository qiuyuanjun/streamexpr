package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-03
 */
public class GteqExpressionASTNode extends DefaultOperatorASTNode {

    public GteqExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.GTEQ);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {

    }
}
