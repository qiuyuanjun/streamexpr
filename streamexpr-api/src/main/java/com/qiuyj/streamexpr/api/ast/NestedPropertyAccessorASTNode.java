package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-08-20
 */
public class NestedPropertyAccessorASTNode extends AbstractASTNode {

    public NestedPropertyAccessorASTNode(ASTNode[] nestedProperties) {
        super(nestedProperties);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {

    }
}
