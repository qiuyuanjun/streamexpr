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
}
