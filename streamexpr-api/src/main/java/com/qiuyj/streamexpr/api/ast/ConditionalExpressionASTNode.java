package com.qiuyj.streamexpr.api.ast;

/**
 * 三元表达式抽象语法树节点
 * @author qiuyj
 * @since 2023-08-05
 */
public class ConditionalExpressionASTNode extends AbstractASTNode implements ExpressionASTNode {

    public ConditionalExpressionASTNode(ASTNode condition, ASTNode trueResult, ASTNode falseResult) {
        super(condition, trueResult, falseResult);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitConditionalExpression(this);
    }

    public ASTNode getCondition() {
        return fastGetChildASTNode(0);
    }

    public ASTNode getTrueResult() {
        return fastGetChildASTNode(1);
    }

    public ASTNode getFalseResult() {
        return fastGetChildASTNode(2);
    }
}
