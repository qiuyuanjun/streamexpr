package com.qiuyj.streamexpr.api.ast;

/**
 * 函数调用的抽象语法树节点
 * @author qiuyj
 * @since 2023-08-20
 */
public class FunctionCallASTNode extends AbstractASTNode implements SingleValueASTNode, ExpressionASTNode {

    /**
     * 函数名
     */
    private final String functionName;

    /**
     * 类名，支持静态函数和被spring容器管理的bean的成员方法
     * 如果是静态函数，那么该字段表示类名
     * 如果是spring容器管理的bean的成员方法，那么该字段表示bean的名称
     */
    private final String className;

    public FunctionCallASTNode(String functionName, ASTNode[] functionCallPart) {
        super(functionCallPart);
        int lastDot = functionName.lastIndexOf('.');
        if (lastDot > -1) {
            this.functionName = functionName.substring(lastDot + 1);
            this.className = functionName.substring(0, lastDot);
        }
        else {
            this.functionName = functionName;
            this.className = null;
        }
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getClassName() {
        return className;
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
