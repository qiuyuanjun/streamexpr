package com.qiuyj.streamexpr.api.ast;

/**
 * 代表一个标识符的抽象语法树节点
 * @author qiuyj
 * @since 2023-07-25
 */
public record IdentifierASTNode(String identifierName) implements SingleValueASTNode {

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        visitor.visitIdentifier(this);
    }

    @Override
    public Object getValue() {
        return identifierName;
    }

    @Override
    public String getSourceString() {
        return identifierName;
    }

    @Override
    public String toString() {
        return identifierName;
    }
}
