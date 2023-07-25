package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.api.ast.{ASTNode, ExpressionASTNode, SingleValueASTNode}

/**
 * @author qiuyj
 * @since 2023-07-25
 */
class SPELASTNode(private[this] val spelString: String) extends SingleValueASTNode with ExpressionASTNode {

  override def getValue: Any = spelString

  override def getSourceString: String = spelString

  /**
   * 采用访问者模式，访问当前节点
   */
  override def visit[T <: ASTNode.Visitor](visitor: T): Unit = {
    visitor.asInstanceOf[StreamExpressionVisitor].visitSPELExpression(this)
  }
}
