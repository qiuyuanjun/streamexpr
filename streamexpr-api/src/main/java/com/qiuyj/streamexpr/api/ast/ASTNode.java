package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-07-23
 */
public interface ASTNode {

    /**
     * 采用访问者模式，访问当前节点
     */
    <T extends ASTNodeVisitor> void visit(T visitor);

    /**
     * 得到当前节点对应的子节点信息
     * @param index 子节点的位置
     * @return 对应的子节点信息
     */
    ASTNode getChildASTNode(int index);

}
