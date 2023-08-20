package com.qiuyj.streamexpr.api.ast;

/**
 * 代表索引类型的抽象语法树节点
 * @author qiuyj
 * @since 2023-08-20
 */
public class IndexedExpressionASTNode extends AbstractASTNode implements SingleValueASTNode {

    public IndexedExpressionASTNode(ASTNode variable, ASTNode index) {
        super(variable, index);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getSourceString() {
        throw new UnsupportedOperationException();
    }
}
