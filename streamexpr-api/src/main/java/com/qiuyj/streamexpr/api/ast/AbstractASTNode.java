package com.qiuyj.streamexpr.api.ast;

import java.util.Objects;

/**
 * 抽象语法树公共节点
 * @author qiuyj
 * @since 2023-07-23
 */
public abstract class AbstractASTNode implements ASTNode {

    private static final ASTNode[] EMPTY = new ASTNode[0];

    /**
     * 当前语法树的所有子节点
     */
    private final ASTNode[] children;

    protected AbstractASTNode(ASTNode first, ASTNode... others) {
        int othersLength = others.length;
        children = new ASTNode[othersLength + 1];
        children[0] = first;
        switch (othersLength) {
            case 0: break;
            case 5: children[5] = others[4];
            case 4: children[4] = others[3];
            case 3: children[3] = others[2];
            case 2: children[2] = others[1];
            case 1: children[1] = others[0]; break;
            default: System.arraycopy(others, 0, children, 1, othersLength);
        }
    }

    protected AbstractASTNode() {
        // 没有子节点
        children = EMPTY;
    }

    public ASTNode getChildASTNode(int index) {
        Objects.checkIndex(index, children.length);
        return children[index];
    }

    protected ASTNode fastGetChildASTNode(int index) {
        return children[index];
    }

    protected int getChildrenCount() {
        return children.length;
    }
}
