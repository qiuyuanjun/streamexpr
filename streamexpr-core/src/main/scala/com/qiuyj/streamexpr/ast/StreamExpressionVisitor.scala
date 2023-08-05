package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.{Parameter, StreamOp}
import com.qiuyj.streamexpr.api.ast.AbstractObjectStackBasedASTNodeVisitor.VisitAction
import com.qiuyj.streamexpr.api.ast._
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

      override def postVisit(parentObject: AnyRef, currentObject: AnyRef, astNode: IdentifierASTNode): Unit = parentObject match {
        case streamOp: StreamOp =>
          streamOp.internalSetValue(astNode.getValue)
        case parameter: Parameter =>
          parameter.initParameter(astNode.getValue, StreamExpression.IDENTIFIER)
        case _ =>
      }
    }, astNode)
  }

  override def visitStringLiteral(astNode: StringLiteralASTNode): Unit = {
    executeVisit[Parameter, String, StringLiteralASTNode](new VisitAction[Parameter, String, StringLiteralASTNode] {

      override def createCurrentObject(astNode: StringLiteralASTNode): String = null

      override def postVisit(parentObject: Parameter, currentObject: String, astNode: StringLiteralASTNode): Unit = {
        // 将当前字符串字面量设置到parameter参数里面
        parentObject.initParameter(astNode.getValue, StreamExpression.STRING_LITERAL)
      }
    }, astNode)
  }

  override def visitOrExpression(astNode: OrExpressionASTNode): Unit = {
    // todo
  }

  override def visitAndExpression(andExpressionASTNode: AndExpressionASTNode): Unit = {
    // todo
  }

  override def visitPlusExpression(plusExpressionASTNode: PlusExpressionASTNode): Unit = {
    // todo
  }

  override def visitMinusExpression(minusExpressionASTNode: MinusExpressionASTNode): Unit = {
    // todo
  }

  override def visitConditionalExpression(conditionalExpressionASTNode: ConditionalExpressionASTNode): Unit = {
    // todo
  }

  override def visitArrayExpression(arrayExpression: ArrayExpression): Unit = {
    // todo
  }

  def visitStreamExpression(astNode: StreamExpressionASTNode): Unit = {
    executeVisit[StreamExpression, StreamExpression, StreamExpressionASTNode](new VisitAction[StreamExpression, StreamExpression, StreamExpressionASTNode] {

      override def createCurrentObject(astNode: StreamExpressionASTNode): StreamExpression = null

      override def doVisit(parentObject: StreamExpression, currentObject: StreamExpression, astNode: StreamExpressionASTNode): Unit = {
        astNode.getIntermediateOps.foreach(visit)
        visit(astNode.getTerminateOp)
      }
    }, astNode)
  }

  def visitStreamOp(astNode: StreamOpASTNode): Unit = {
    executeVisit[StreamExpression, StreamOp, StreamOpASTNode](new VisitAction[StreamExpression, StreamOp, StreamOpASTNode] {

      override def doVisit(parentObject: StreamExpression, currentObject: StreamOp, astNode: StreamOpASTNode): Unit = {
        // 解析操作名称
        visit(astNode.getOpName)
        // 解析操作所需要的各种参数
        astNode.getParameters.foreach(visit)
      }

      override def postVisit(parentObject: StreamExpression, currentObject: StreamOp, astNode: StreamOpASTNode): Unit = {
        parentObject.internalAddStreamOp(currentObject)
      }
    }, astNode)
  }

  def visitSPELExpression(astNode: SPELASTNode): Unit = {
    executeVisit[StreamOp, Parameter, SPELASTNode](new VisitAction[StreamOp, Parameter, SPELASTNode] {

      override def doVisit(parentObject: StreamOp, currentObject: Parameter, astNode: SPELASTNode): Unit = {
        val spelExpr: Expression = new SpelExpressionParser()
          .parseExpression(astNode.getSourceString, ParserContext.TEMPLATE_EXPRESSION)
        // 初始化参数
        currentObject.initParameter(spelExpr, StreamExpression.SPEL)
      }

      override def postVisit(parentObject: StreamOp, currentObject: Parameter, astNode: SPELASTNode): Unit = {
        parentObject.internalSetValue(currentObject)
      }
    }, astNode)
  }

  def visitContextAttribute(astNode: ContextAttributeASTNode): Unit = {
    // todo
  }

  private def visit(astNode: ASTNode): Unit = {
    astNode.visit[StreamExpressionVisitor](this)
  }

  def getStreamExpression: StreamExpression = {
    getRootObject match {
      case expression: StreamExpression =>
        expression
      case _ =>
        throw new IllegalStateException("Type matching failed, actual type requires StreamExpression")
    }
  }
}
