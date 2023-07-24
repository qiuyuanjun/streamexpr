package com.qiuyj.streamexpr.ast

import com.qiuyj.streamexpr.api.ast.ASTNode

/**
 * @author qiuyj
 * @since 2023-07-23
 */
class StreamOpASTNode(private[this] val opName: ASTNode,
                      private[this] val parameters: ASTNode*)
    extends AbstractStreamExpressionASTNode(opName, parameters: _*) {

  override protected def visit(streamExpressionVisitor: StreamExpressionVisitor): Unit = {
    streamExpressionVisitor.visitStreamOp(this)
  }

  private[ast] def getOpName: ASTNode = fastGetChildASTNode(0)

  private[ast] def getParameters: Seq[ASTNode] = parameters
}
