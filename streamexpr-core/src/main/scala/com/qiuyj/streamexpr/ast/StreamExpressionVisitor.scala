package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.{Parameter, StreamOp}
import com.qiuyj.streamexpr.api.ast.AbstractObjectStackBasedASTNodeVisitor.VisitAction
import com.qiuyj.streamexpr.api.ast.{ASTNode, AbstractObjectStackBasedASTNodeVisitor, IdentifierASTNode, StringLiteralASTNode}
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.{Expression, ParserContext}

/**
 * @author qiuyj
 * @since 2023-07-23
 */
class StreamExpressionVisitor extends AbstractObjectStackBasedASTNodeVisitor(new StreamExpression) {

  override def visitIdentifier(astNode: IdentifierASTNode): Unit = {
    executeVisit[AnyRef, AnyRef, IdentifierASTNode](new VisitAction[AnyRef, AnyRef, IdentifierASTNode] {

      override def createCurrentObject(astNode: IdentifierASTNode): AnyRef = null

      override def doVisit(parentObject: AnyRef, astNode: IdentifierASTNode): Unit = parentObject match {
        case streamOp: StreamOp =>
          streamOp.internalSetValue(astNode.getValue)
        case _ =>
      }
    }, astNode)
  }

  override def visitStringLiteral(astNode: StringLiteralASTNode): Unit = {
    executeVisit[Parameter, String, StringLiteralASTNode](new VisitAction[Parameter, String, StringLiteralASTNode] {

      override def createCurrentObject(astNode: StringLiteralASTNode): String = null

      override def doVisit(parentObject: Parameter, astNode: StringLiteralASTNode): Unit = {

      }
    }, astNode)
  }

  def visitStreamExpression(astNode: StreamExpressionASTNode): Unit = {
    executeVisit[StreamExpression, StreamExpression, StreamExpressionASTNode](new VisitAction[StreamExpression, StreamExpression, StreamExpressionASTNode] {

      override def createCurrentObject(astNode: StreamExpressionASTNode): StreamExpression = null

      override def doVisit(parentObject: StreamExpression, astNode: StreamExpressionASTNode): Unit = {
        astNode.getIntermediateOps.foreach(visit)
        visit(astNode.getTerminateOp)
      }
    }, astNode)
  }

  def visitStreamOp(astNode: StreamOpASTNode): Unit = {
    executeVisit[StreamExpression, StreamOp, StreamOpASTNode](new VisitAction[StreamExpression, StreamOp, StreamOpASTNode] {

      override def doVisit(parentObject: StreamExpression, astNode: StreamOpASTNode): Unit = {
        // 解析操作名称
        visit(astNode.getOpName)
        // 解析操作所需要的各种参数
        astNode.getParameters.foreach(visit)
      }

      override def postVisit(parentObject: StreamExpression, currentObject: StreamOp, astNode: StreamOpASTNode): Unit = {
        parentObject.addStreamOp(currentObject)
      }
    }, astNode)
  }

  def visitSPELExpression(astNode: SPELASTNode): Unit = {
    executeVisit[StreamOp, Parameter, SPELASTNode](new VisitAction[StreamOp, Parameter, SPELASTNode] {

      override def doVisit(parentObject: StreamOp, astNode: SPELASTNode): Unit = {
        val spelExpr: Expression = new SpelExpressionParser()
          .parseExpression(astNode.getSourceString, ParserContext.TEMPLATE_EXPRESSION)
        // todo
      }

      override def postVisit(parentObject: StreamOp, currentObject: Parameter, astNode: SPELASTNode): Unit = {
        parentObject.internalSetValue(currentObject)
      }
    }, astNode)
  }

  private def visit(astNode: ASTNode): Unit = {
    astNode.visit[StreamExpressionVisitor](this)
  }

  def getStreamExpression: StreamExpression = {
    getRootObject match {
      case expression: StreamExpression =>
        expression
      case _ =>
        throw new IllegalStateException("")
    }
  }
}
