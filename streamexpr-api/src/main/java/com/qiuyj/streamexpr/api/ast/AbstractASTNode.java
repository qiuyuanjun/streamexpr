package com.qiuyj.streamexpr.api.ast;

import java.lang.reflect.Array;
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
        children = makeArray(ASTNode.class, first, others);
    }

    @SuppressWarnings("unchecked")
    public static <T extends ASTNode> T[] makeArray(Class<T> componentType, T first, T... others) {
        int othersLength = others.length;
        T[] array = (T[]) Array.newInstance(componentType, othersLength + 1);
        array[0] = first;
        switch (othersLength) {
            case 0: break;
            case 5: array[5] = others[4];
            case 4: array[4] = others[3];
            case 3: array[3] = others[2];
            case 2: array[2] = others[1];
            case 1: array[1] = others[0]; break;
            default: System.arraycopy(others, 0, array, 1, othersLength);
        }
        return array;
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

    protected int getChildrenCount() {
        return children.length;
    }
}
