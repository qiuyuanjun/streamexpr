package com.qiuyj.streamexpr.api.ast;

/**
 * 数组表达式抽象语法树节点
 * @author qiuyj
 * @since 2023-08-05
 */
public class ArrayExpression extends AbstractASTNode implements SingleValueASTNode {

    public ArrayExpression(ASTNode[] array) {
        super(array);
    }

    private ArrayExpression() {
        // 空数组
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitArrayExpression(this);
    }

    @Override
    public ASTNode[] getValue() {
        return getAllChildren();
    }

    @Override
    public String getSourceString() {
        throw new UnsupportedOperationException();
    }

    public static ArrayExpression empty() {
        return new ArrayExpression();
    }
}
