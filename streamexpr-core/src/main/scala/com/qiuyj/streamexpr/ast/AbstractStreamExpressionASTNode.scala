package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.api.ast.{ASTNode, ASTNodeVisitor, AbstractASTNode}

/**
 * @author qiuyj
 * @since 2023-07-23
 */
abstract class AbstractStreamExpressionASTNode(private[this] val first: ASTNode,
                                               private[this] val others: ASTNode*)
    extends AbstractASTNode(first, others: _*) {

  protected def visit(streamExpressionVisitor: StreamExpressionVisitor): Unit

  override def visit[T <: ASTNodeVisitor](visitor: T): Unit = {
    visitor match {
      case streamExpressionVisitor: StreamExpressionVisitor =>
        visit(streamExpressionVisitor)
      case _ =>
        throw new IllegalStateException("Unsupported visitor type, must as sub class of StreamExpressionVisitor")
    }
  }
}
