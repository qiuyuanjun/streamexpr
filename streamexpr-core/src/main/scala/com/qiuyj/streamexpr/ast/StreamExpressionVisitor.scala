package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.StreamExpression
import com.qiuyj.streamexpr.StreamExpression.{Parameter, StreamOp}
import com.qiuyj.streamexpr.api.ast.AbstractObjectStackBasedASTNodeVisitor.VisitAction
import com.qiuyj.streamexpr.api.ast._
import com.qiuyj.streamexpr.ast.StreamExpressionVisitor.ParameterASTNodeVisitorAction
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
          parameter.internalInitParameter(astNode.getValue, StreamExpression.IDENTIFIER)
        case _ =>
          throw new UnsupportedOperationException
      }
    }, astNode)
  }

  override def visitStringLiteral(astNode: StringLiteralASTNode): Unit = {
    executeVisit[StreamOp, Parameter, StringLiteralASTNode](new VisitAction[StreamOp, Parameter, StringLiteralASTNode] {

      override def doVisit(parentObject: StreamOp, currentObject: Parameter, astNode: StringLiteralASTNode): Unit = {
        currentObject.internalInitParameter(astNode.getValue, StreamExpression.STRING_LITERAL)
      }

      override def postVisit(parentObject: StreamOp, currentObject: Parameter, astNode: StringLiteralASTNode): Unit = {
        parentObject.internalSetValue(currentObject)
      }
    }, astNode)
  }

  override def visitOrExpression(astNode: OrExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, astNode)
  }

  override def visitAndExpression(andExpressionASTNode: AndExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, andExpressionASTNode)
  }

  override def visitPlusExpression(plusExpressionASTNode: PlusExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, plusExpressionASTNode)
  }

  override def visitMinusExpression(minusExpressionASTNode: MinusExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, minusExpressionASTNode)
  }

  override def visitConditionalExpression(conditionalExpressionASTNode: ConditionalExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, conditionalExpressionASTNode)
  }

  override def visitArrayExpression(arrayExpressionASTNode: ArrayExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, arrayExpressionASTNode)
  }

  override def visitFunctionCall(functionCallASTNode: FunctionCallASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, functionCallASTNode)
  }

  override def visitIndexedExpression(indexedExpressionASTNode: IndexedExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, indexedExpressionASTNode)
  }

  override def visitDivExpression(divExpressionASTNode: DivExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, divExpressionASTNode)
  }

  override def visitEqExpression(eqExpressionASTNode: EqExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, eqExpressionASTNode)
  }

  override def visitGteqExpression(gteqExpressionASTNode: GteqExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, gteqExpressionASTNode)
  }

  override def visitGtExpression(gtExpressionASTNode: GtExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, gtExpressionASTNode)
  }

  override def visitLteqExpression(lteqExpressionASTNode: LteqExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, lteqExpressionASTNode)
  }

  override def visitLtExpression(ltExpressionASTNode: LtExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, ltExpressionASTNode)
  }

  override def visitModExpression(modExpressionASTNode: ModExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, modExpressionASTNode)
  }

  override def visitMultiExpression(multiExpressionASTNode: MultiExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, multiExpressionASTNode)
  }

  override def visitNeqExpression(neqExpressionASTNode: NeqExpressionASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, neqExpressionASTNode)
  }

  override def visitNestedPropertyAccessor(nestedPropertyAccessorASTNode: NestedPropertyAccessorASTNode): Unit = {
    executeVisit(new ParameterASTNodeVisitorAction, nestedPropertyAccessorASTNode)
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
        currentObject.internalInitParameter(spelExpr, StreamExpression.SPEL)
      }

      override def postVisit(parentObject: StreamOp, currentObject: Parameter, astNode: SPELASTNode): Unit = {
        parentObject.internalSetValue(currentObject)
      }
    }, astNode)
  }

  def visitContextAttribute(astNode: ContextAttributeASTNode): Unit = {
    executeVisit[StreamOp, Parameter, ContextAttributeASTNode](new VisitAction[StreamOp, Parameter, ContextAttributeASTNode]() {

      override def doVisit(parentObject: StreamOp, currentObject: Parameter, astNode: ContextAttributeASTNode): Unit = {
        currentObject.internalInitParameter(astNode.getSourceString, StreamExpression.CONTEXT_ATTRIBUTE)
      }

      override def postVisit(parentObject: StreamOp, currentObject: Parameter, astNode: ContextAttributeASTNode): Unit = {
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
        throw new IllegalStateException("Type matching failed, actual type requires StreamExpression")
    }
  }
}

object StreamExpressionVisitor {

  private[StreamExpressionVisitor] class ParameterASTNodeVisitorAction extends VisitAction[StreamOp, Parameter, ASTNode] {

    override def doVisit(parentObject: StreamOp, currentObject: Parameter, astNode: ASTNode): Unit = {
      currentObject.internalInitParameter(astNode, StreamExpression.AST)
    }

    override def postVisit(parentObject: StreamOp, currentObject: Parameter, astNode: ASTNode): Unit = {
      parentObject.internalSetValue(currentObject)
    }
  }
}
