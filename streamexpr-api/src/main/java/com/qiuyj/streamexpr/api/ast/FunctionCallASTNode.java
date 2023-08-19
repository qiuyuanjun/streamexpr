package com.qiuyj.streamexpr.api.ast;

/**
 * 函数调用的抽象语法树节点
 * @author qiuyj
 * @since 2023-08-20
 */
public class FunctionCallASTNode extends AbstractASTNode {

    public FunctionCallASTNode(ASTNode[] functionCallPart) {
        super(functionCallPart);
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {

    }
}
