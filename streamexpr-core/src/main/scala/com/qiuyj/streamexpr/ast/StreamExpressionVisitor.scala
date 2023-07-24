package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.StreamOp
import com.qiuyj.streamexpr.api.ast.ASTNode.Visitor
import com.qiuyj.streamexpr.api.ast.{ASTNode, ExpressionASTNode}

import scala.collection.mutable

/**
 * @author qiuyj
 * @since 2023-07-23
 */
class StreamExpressionVisitor extends Visitor {

  private[this] val stack: mutable.Stack[Any] = mutable.Stack.empty

  override def visitExpression(astNode: ExpressionASTNode): Unit = {

  }

  def visitStreamExpression(astNode: StreamExpressionASTNode): Unit = {
    // 对象入栈
    stack.push(new StreamExpression)
    astNode.getIntermediateOps.foreach(visit)
    visit(astNode.getTerminateOp)
  }

  def visitStreamOp(astNode: StreamOpASTNode): Unit = {
    val streamExpression: StreamExpression = stack.pop().asInstanceOf[StreamExpression]
    stack.push(new StreamOp)
    // 解析操作名称
    visit(astNode.getOpName)
    // 解析操作所需要的各种参数
    astNode.getParameters.foreach(visit)
    streamExpression.addStreamOp(stack.pop().asInstanceOf[StreamOp])
    stack.push(streamExpression)
  }

  private def visit(astNode: ASTNode): Unit = {
    astNode.visit[StreamExpressionVisitor](this)
  }

  def getStreamExpression: StreamExpression = {
    val obj = stack.pop()
    if (stack.nonEmpty) {
      throw new IllegalStateException("")
    }
    obj match {
      case streamExpression: StreamExpression => streamExpression
      case _ => throw new IllegalStateException("")
    }
  }
}
