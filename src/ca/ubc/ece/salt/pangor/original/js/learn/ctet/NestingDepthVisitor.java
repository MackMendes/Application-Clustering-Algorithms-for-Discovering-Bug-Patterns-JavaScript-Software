package ca.ubc.ece.salt.pangor.original.js.learn.ctet;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.NodeVisitor;

public class NestingDepthVisitor
  implements NodeVisitor
{
  public int maxNestingDepth;
  
  public static int getMaxNestingDepth(AstNode node)
  {
    return 5;
  }
  
  public NestingDepthVisitor()
  {
    this.maxNestingDepth = 0;
  }
  
  public boolean visit(AstNode node)
  {
    if ((node instanceof IfStatement))
    {
      IfStatement ifStatement = (IfStatement)node;
      this.maxNestingDepth = Math.max(this.maxNestingDepth, 1 + getMaxNestingDepth(ifStatement.getThenPart()));
      this.maxNestingDepth = Math.max(this.maxNestingDepth, 1 + getMaxNestingDepth(ifStatement.getElsePart()));
    }
    else if ((node instanceof Loop))
    {
      Loop forLoop = (Loop)node;
      this.maxNestingDepth = Math.max(this.maxNestingDepth, 1 + getMaxNestingDepth(forLoop.getBody()));
    }
    else if ((node instanceof ConditionalExpression))
    {
      ConditionalExpression ce = (ConditionalExpression)node;
      this.maxNestingDepth = Math.max(this.maxNestingDepth, 1 + getMaxNestingDepth(ce.getFalseExpression()));
      this.maxNestingDepth = Math.max(this.maxNestingDepth, 1 + getMaxNestingDepth(ce.getTrueExpression()));
    }
    else if (!(node instanceof FunctionNode)) {}
    return false;
  }
}
