package com.qiuyj.streamexpr.api.ast;

/**
 * or表达式抽象语法树节点
 * @author qiuyj
 * @since 2023-07-30
 */
public class OrExpressionASTNode extends DefaultOperatorASTNode {

    public OrExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.OR);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitOrExpression(this);
    }
}
