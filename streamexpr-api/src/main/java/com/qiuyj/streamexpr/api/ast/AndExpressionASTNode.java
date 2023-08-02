package com.qiuyj.streamexpr.api.ast;

/**
 * and表达式抽象语法树节点
 * @author qiuyj
 * @since 2023-07-30
 */
public class AndExpressionASTNode extends DefaultOperatorASTNode {

    public AndExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.AND);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitAndExpression(this);
    }
}
