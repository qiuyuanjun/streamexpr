package com.qiuyj.streamexpr.ast

import scala.collection.mutable.ArrayBuffer

/**
 * @author qiuyj
 * @since 2023-07-23
 */
class StreamExpressionASTNode(private[this] val first: StreamOpASTNode,
                              private[this] val others: StreamOpASTNode*)
   extends AbstractStreamExpressionASTNode(first, others: _*) {

  override protected def visit(streamExpressionVisitor: StreamExpressionVisitor): Unit = {
    streamExpressionVisitor.visitStreamExpression(this)
  }

  private[ast] def getTerminateOp: StreamOpASTNode =
    fastGetChildASTNode(0).asInstanceOf[StreamOpASTNode]

  private[ast] def getIntermediateOps: Seq[StreamOpASTNode] = others
}

object StreamExpressionASTNode {

  class Builder {

    private[this] val streamOps: ArrayBuffer[StreamOpASTNode] = new ArrayBuffer[StreamOpASTNode](6)

    def addStreamOp(streamOp: StreamOpASTNode): this.type = {
      streamOps addOne streamOp
      this
    }

    def build: StreamExpressionASTNode = {
      if (streamOps.isEmpty) {
        throw new IllegalStateException("A stream expression must have at least one stream operation")
      }
      val terminateOp = streamOps.remove(streamOps.size - 1)
      val intermediateOps = if (streamOps.isEmpty) Seq.empty else streamOps.toSeq
      new StreamExpressionASTNode(terminateOp, intermediateOps: _*)
    }
  }
}