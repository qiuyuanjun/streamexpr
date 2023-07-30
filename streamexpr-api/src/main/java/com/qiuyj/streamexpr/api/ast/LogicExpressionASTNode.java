package com.qiuyj.streamexpr.api.ast;

/**
 * @author qiuyj
 * @since 2023-07-30
 */
public abstract class LogicExpressionASTNode extends AbstractASTNode implements ExpressionASTNode {
    
    private final LogicOperator logicOperator;
    
    protected LogicExpressionASTNode(LogicOperator logicOperator, ExpressionASTNode... logicPart) {
        super(logicPart);
        this.logicOperator = logicOperator;
    }
    
    @Override
    public <T extends ASTNodeVisitor> void visit(T visitor) {
        if (logicOperator == LogicOperator.OR) {
            visitor.visitOrExpression((OrExpressionASTNode) this);
        }
        else {
            visitor.visitAndExpression((AndExpressionASTNode) this);
        }
    }
    
    public enum LogicOperator {
        OR, AND
    }
}
