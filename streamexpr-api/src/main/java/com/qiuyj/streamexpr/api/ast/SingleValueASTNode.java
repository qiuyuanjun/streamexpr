package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-07-25
 */
public interface SingleValueASTNode extends ASTNode {

    Object getValue();

    String getSourceString();
}
