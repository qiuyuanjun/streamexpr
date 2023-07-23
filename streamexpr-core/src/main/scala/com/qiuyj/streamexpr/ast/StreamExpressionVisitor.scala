package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.api.ast.ASTNode.Visitor
import com.qiuyj.streamexpr.api.ast.ExpressionASTNode

/**
 * @author qiuyj
 * @since 2023-07-23
 */
class StreamExpressionVisitor(private[this] val streamExpression: StreamExpression) extends Visitor {

  override def visitExpression(expression: ExpressionASTNode): Unit = {

  }

  def visitStreamExpression(streamExpression: StreamExpressionASTNode): Unit = {
    streamExpression.getIntermediateOps.foreach(visitStreamOp)
    visitStreamOp(streamExpression.getTerminateOp)
  }

  def visitStreamOp(streamOp: StreamOpASTNode): Unit = {

  }
}
