package ca.ubc.ece.salt.pangor.original.js.learn.analysis;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;

public class ChangeComplexityVisitor
  implements NodeVisitor
{
  private ChangeComplexity changeComplexity;
  private boolean dst;
  
  public static ChangeComplexity getChangeComplexity(AstRoot root, boolean dst)
  {
    ChangeComplexityVisitor visitor = new ChangeComplexityVisitor(dst);
    root.visit(visitor);
    return visitor.changeComplexity;
  }
  
  public ChangeComplexityVisitor(boolean dst)
  {
    this.changeComplexity = new ChangeComplexity();
    this.dst = dst;
  }
  
  public boolean visit(AstNode node)
  {
    if ((((node instanceof VariableDeclaration)) && (node.isStatement() == true)) || ((node instanceof ExpressionStatement)) || ((node instanceof ReturnStatement)) || ((node instanceof BreakStatement)) || ((node instanceof ContinueStatement)) || ((node instanceof ThrowStatement)) || ((node instanceof IfStatement)) || ((node instanceof WithStatement)) || ((node instanceof TryStatement)) || ((node instanceof CatchClause)) || ((node instanceof SwitchStatement)) || ((node instanceof DoLoop)) || ((node instanceof ForInLoop)) || ((node instanceof ForLoop)) || ((node instanceof WhileLoop)) || ((node instanceof FunctionNode))) {
      changeTypeModified(node);
    }
    return true;
  }
  
  private void changeTypeModified(AstNode node)
  {
    switch (node.getChangeType())
    {
    case INSERTED: 
      this.changeComplexity.insertedStatements += 1;
      return;
    case REMOVED: 
      this.changeComplexity.removedStatements += 1;
      return;
    case UPDATED: 
      ClassifiedASTNode.ChangeType mappedChangeType = node.getMapping().getChangeType();
      if (mappedChangeType == ClassifiedASTNode.ChangeType.UPDATED) {
        this.changeComplexity.updatedStatements += 1;
      } else if (this.dst) {
        this.changeComplexity.insertedStatements += 1;
      } else {
        this.changeComplexity.removedStatements += 1;
      }
      return;
	default:
		return;
    }
  }
  
  public class ChangeComplexity
  {
    public int updatedStatements;
    public int insertedStatements;
    public int removedStatements;
    
    public ChangeComplexity()
    {
      this.updatedStatements = 0;
      this.insertedStatements = 0;
      this.removedStatements = 0;
    }
  }
}
