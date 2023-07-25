package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.{Parameter, StreamOp}
import com.qiuyj.streamexpr.api.ast.ASTNode.Visitor
import com.qiuyj.streamexpr.api.ast.{ASTNode, IdentifierASTNode, StringLiteralASTNode}
import org.springframework.expression.{Expression, ParserContext}
import org.springframework.expression.spel.standard.{SpelExpression, SpelExpressionParser}

import scala.collection.mutable

/**
 * @author qiuyj
 * @since 2023-07-23
 */
class StreamExpressionVisitor extends Visitor {

  private[this] val objectStack: mutable.Stack[AnyRef] = mutable.Stack.empty[AnyRef]

  override def visitIdentifier(astNode: IdentifierASTNode): Unit = {
    executeVisit[AnyRef] {
      case streamOp: StreamOp =>
        streamOp.internalSetValue(astNode.getValue)
      case _ =>
    }
  }

  override def visitStringLiteral(astNode: StringLiteralASTNode): Unit = {

  }

  def visitStreamExpression(astNode: StreamExpressionASTNode): Unit = {
    // 对象入栈
    objectStack.push(new StreamExpression)
    astNode.getIntermediateOps.foreach(visit)
    visit(astNode.getTerminateOp)
  }

  def visitStreamOp(astNode: StreamOpASTNode): Unit = {
    executeVisit[StreamExpression](streamExpression => {
      objectStack.push(new StreamOp)
      // 解析操作名称
      visit(astNode.getOpName)
      // 解析操作所需要的各种参数
      astNode.getParameters.foreach(visit)
      streamExpression.addStreamOp(objectStack.pop().asInstanceOf[StreamOp])
    })
  }

  def visitSPELExpression(astNode: SPELASTNode): Unit = {
    executeVisit[Parameter](parameter => {
      val spelExpr: Expression = new SpelExpressionParser()
        .parseExpression(astNode.getSourceString, ParserContext.TEMPLATE_EXPRESSION)
      parameter.addParameter()
    })
  }

  private def executeVisit[T <: AnyRef](visitMethod: T => Unit): Unit = {
    val prevObj: T = objectStack.pop().asInstanceOf[T]
    visitMethod(prevObj)
    objectStack.push(prevObj)
  }

  private def visit(astNode: ASTNode): Unit = {
    astNode.visit[StreamExpressionVisitor](this)
  }

  def getStreamExpression: StreamExpression = {
    val obj = objectStack.pop()
    if (objectStack.nonEmpty) {
      throw new IllegalStateException("")
    }
    obj match {
      case streamExpression: StreamExpression => streamExpression
      case _ => throw new IllegalStateException("")
    }
  }
}
