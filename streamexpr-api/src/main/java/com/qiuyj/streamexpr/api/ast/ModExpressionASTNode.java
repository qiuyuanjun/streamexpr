package com.qiuyj.streamexpr.api.ast;

/**
 * 求余运算符表达式抽象语法树节点
 * @author qiuyj
 * @since 2023-08-19
 */
public class ModExpressionASTNode extends DefaultOperatorASTNode {

    public ModExpressionASTNode(ASTNode left, ASTNode right) {
        super(left, right, Operator.MOD);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitModExpression(this);
    }
}
