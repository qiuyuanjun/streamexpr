package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-07-23
 */
public interface OperatorASTNode extends ASTNode {

    ASTNode getLeft();

    ASTNode getRight();

    ASTNode getOperand();
}
