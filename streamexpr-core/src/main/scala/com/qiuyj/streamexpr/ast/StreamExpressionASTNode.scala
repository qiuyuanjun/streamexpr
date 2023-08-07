package com.qiuyj.streamexpr.ast

import java.util
import scala.jdk.javaapi.CollectionConverters

/**
 * @author qiuyj
 * @since 2023-07-23
 */
class StreamExpressionASTNode(private[this] val terminateOp: StreamOpASTNode,
                              private[this] val intermediateOps: StreamOpASTNode*)
   extends AbstractStreamExpressionASTNode(terminateOp, intermediateOps: _*) {

  override protected def visit(streamExpressionVisitor: StreamExpressionVisitor): Unit = {
    streamExpressionVisitor.visitStreamExpression(this)
  }

  private[ast] def getTerminateOp: StreamOpASTNode =
    fastGetChildASTNode(0).asInstanceOf[StreamOpASTNode]

  private[ast] def getIntermediateOps: Seq[StreamOpASTNode] = intermediateOps
}

object StreamExpressionASTNode {

  class Builder {

    private[this] val streamOps: util.Deque[StreamOpASTNode] = new util.ArrayDeque[StreamOpASTNode](6)

    def addStreamOp(streamOp: StreamOpASTNode): this.type = {
      streamOps add streamOp
      this
    }

    def build: StreamExpressionASTNode = {
      if (streamOps.isEmpty) {
        throw new IllegalStateException("A stream expression must have at least one stream operation")
      }
      val terminateOp = streamOps.removeLast()
      val intermediateOps = if (streamOps.isEmpty) Seq.empty else CollectionConverters.asScala(streamOps).toSeq
      new StreamExpressionASTNode(terminateOp, intermediateOps: _*)
    }
  }
}