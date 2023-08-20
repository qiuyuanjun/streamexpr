package com.qiuyj.streamexpr.api.ast;

/**
 * 函数调用的抽象语法树节点
 * @author qiuyj
 * @since 2023-08-20
 */
public class FunctionCallASTNode extends AbstractASTNode implements SingleValueASTNode {

    private final String functionName;

    public FunctionCallASTNode(String functionName, ASTNode[] functionCallPart) {
        super(functionCallPart);
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {

    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getSourceString() {
        throw new UnsupportedOperationException();
    }
}
