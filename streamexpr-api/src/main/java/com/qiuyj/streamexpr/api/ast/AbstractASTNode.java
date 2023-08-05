package com.qiuyj.streamexpr.api.ast;

import com.qiuyj.streamexpr.api.utils.ArrayUtils;

import java.util.Objects;

/**
 * 抽象语法树公共节点
 * @author qiuyj
 * @since 2023-07-23
 */
public abstract class AbstractASTNode implements ASTNode {

    public static final ASTNode[] EMPTY = new ASTNode[0];

    /**
     * 当前语法树的所有子节点
     */
    private final ASTNode[] children;

    protected AbstractASTNode(ASTNode first, ASTNode... others) {
        children = ArrayUtils.makeArray(ASTNode.class, first, others);
    }

    protected AbstractASTNode(ASTNode[] children) {
        this.children = children;
    }

    protected AbstractASTNode() {
        // 没有子节点
        this(EMPTY);
    }

    @Override
    public ASTNode getChildASTNode(int index) {
        Objects.checkIndex(index, children.length);
        return children[index];
    }

    protected ASTNode fastGetChildASTNode(int index) {
        return children[index];
    }

    protected ASTNode[] getAllChildren() {
        return children;
    }

    protected int getChildrenCount() {
        return children.length;
    }
}
