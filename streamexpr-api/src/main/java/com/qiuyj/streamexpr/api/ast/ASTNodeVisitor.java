package com.qiuyj.streamexpr.api.ast;

/**
 * 抽象语法树节点访问器，内部提供各种节点的方法方法
 * @author qiuyj
 * @since 2023-07-26
 */
public interface ASTNodeVisitor {

    /**
     * 访问标识符的抽象语法树节点
     * @param identifierASTNode 标识符抽象语法树节点，内部存储了标识符值
     */
    void visitIdentifier(IdentifierASTNode identifierASTNode);

    /**
     * 访问字符串字面量的抽象语法树节点
     * @param stringLiteralASTNode 字符串字面量抽象语法树节点，内部存储了字符串字面量
     */
    void visitStringLiteral(StringLiteralASTNode stringLiteralASTNode);

    /**
     * 访问or表达式的抽象语法树节点
     * @param orExpressionASTNode or表达式的抽象语法树节点
     */
    void visitOrExpression(OrExpressionASTNode orExpressionASTNode);

    /**
     * 访问and表达式的抽象语法树节点
     * @param andExpressionASTNode or表达式的抽象语法树节点
     */
    void visitAndExpression(AndExpressionASTNode andExpressionASTNode);

    /**
     * 访问加法表达式抽象语法树节点
     * @param plusExpressionASTNode 加法表达式抽象语法树节点
     */
    void visitPlusExpression(PlusExpressionASTNode plusExpressionASTNode);

    /**
     * 访问减法表达式抽象语法树节点
     * @param minusExpressionASTNode 减法表达式抽象语法树节点
     */
    void visitMinusExpression(MinusExpressionASTNode minusExpressionASTNode);

    /**
     * 访问三元表达式抽象语法树节点
     * @param conditionalExpressionASTNode 三元表达式抽象语法树节点
     */
    void visitConditionalExpression(ConditionalExpressionASTNode conditionalExpressionASTNode);

    /**
     * 访问数组表达式抽象语法树节点
     * @param arrayExpressionASTNode 数组表达式抽象语法树节点
     */
    void visitArrayExpression(ArrayExpressionASTNode arrayExpressionASTNode);

    /**
     * 访问函数调用的抽象语法树节点
     * @param functionCallASTNode 函数调用的抽象语法树节点
     */
    void visitFunctionCall(FunctionCallASTNode functionCallASTNode);

    /**
     * 访问索引表达式的抽象语法树节点
     * @param indexedExpressionASTNode 索引表达式的抽象语法树节点
     */
    void visitIndexedExpression(IndexedExpressionASTNode indexedExpressionASTNode);

    /**
     * 访问除法表达式的抽象语法树节点
     * @param divExpressionASTNode 除法表达式抽象语法树节点
     */
    void visitDivExpression(DivExpressionASTNode divExpressionASTNode);

    /**
     * 访问相等表达式的抽象语法树节点
     * @param eqExpressionASTNode 相等表达式抽象语法树节点
     */
    void visitEqExpression(EqExpressionASTNode eqExpressionASTNode);

    /**
     * 访问大于等于表达式抽象语法树节点
     * @param gteqExpressionASTNode 大于等于表达式抽象语法树节点
     */
    void visitGteqExpression(GteqExpressionASTNode gteqExpressionASTNode);

    /**
     * 访问大于表达式抽象语法树节点
     * @param gtExpressionASTNode 大于表达式抽象语法树节点
     */
    void visitGtExpression(GtExpressionASTNode gtExpressionASTNode);

    /**
     * 访问小于等于表达式抽象语法树节点
     * @param lteqExpressionASTNode 小于等于表达式抽象语法树节点
     */
    void visitLteqExpression(LteqExpressionASTNode lteqExpressionASTNode);

    /**
     * 访问小于表达式抽象语法树节点
     * @param ltExpressionASTNode 小于表达式抽象语法树节点
     */
    void visitLtExpression(LtExpressionASTNode ltExpressionASTNode);

    /**
     * 访问求余表达式抽象语法树节点
     * @param modExpressionASTNode 求余表达式抽象语法树节点
     */
    void visitModExpression(ModExpressionASTNode modExpressionASTNode);

    /**
     * 访问乘法表达式抽象语法树节点
     * @param multiExpressionASTNode 乘法表达式抽象语法树节点
     */
    void visitMultiExpression(MultiExpressionASTNode multiExpressionASTNode);

    /**
     * 访问不相等抽象语法树节点
     * @param neqExpressionASTNode 不相等抽象语法树节点
     */
    void visitNeqExpression(NeqExpressionASTNode neqExpressionASTNode);

    /**
     * 访问内嵌属性访问表达式抽象语法树节点
     * @param nestedPropertyAccessorASTNode 内嵌属性访问表达式抽象语法树节点
     */
    void visitNestedPropertyAccessor(NestedPropertyAccessorASTNode nestedPropertyAccessorASTNode);
}
