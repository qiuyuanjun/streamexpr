package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-07-25
 */
public interface SingleValueASTNode extends ASTNode {

    Object getValue();

    String getSourceString();

    @Override
    default ASTNode getChildASTNode(int index) {
        // 单节点的抽象语法树节点没有子节点
        throw new UnsupportedOperationException();
    }
}
