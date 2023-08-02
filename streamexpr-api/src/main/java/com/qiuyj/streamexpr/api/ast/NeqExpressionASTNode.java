package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-03
 */
public class NeqExpressionASTNode extends DefaultOperatorASTNode {

    public NeqExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.NEQ);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {

    }
}
