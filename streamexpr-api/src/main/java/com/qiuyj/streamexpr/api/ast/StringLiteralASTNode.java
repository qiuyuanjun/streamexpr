package com.qiuyj.streamexpr.api.ast;

/**
 * 代表字符串字面量的抽象语法树节点
 * @author qiuyj
 * @since 2023-07-25
 */
public record StringLiteralASTNode(String stringLiteral) implements SingleValueASTNode {

    @Override
    public <T extends Visitor> void visit(T visitor) {
        visitor.visitStringLiteral(this);
    }

    @Override
    public Object getValue() {
        return stringLiteral;
    }

    @Override
    public String getSourceString() {
        return stringLiteral;
    }
}
