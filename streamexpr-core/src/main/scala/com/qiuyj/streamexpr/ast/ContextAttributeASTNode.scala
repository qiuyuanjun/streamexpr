package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.api.ast.{ASTNodeVisitor, SingleValueASTNode}

/**
 * @author qiuyj
 * @since 2023-08-05
 */
class ContextAttributeASTNode(private[this] val contextAttr: String) extends SingleValueASTNode {

  override def getValue: Any = {
    null
  }

  override def getSourceString: String = contextAttr

  override def visit[T <: ASTNodeVisitor](visitor: T): Unit = {
    visitor.asInstanceOf[StreamExpressionVisitor].visitContextAttribute(this)
  }
}
