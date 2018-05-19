package ca.ubc.ece.salt.pangor.original.analysis.flow;

import java.util.LinkedList;
import java.util.List;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;

public class FunctionTreeVisitor
  implements NodeVisitor
{
  private ScriptNode root;
  private List<FunctionNode> functions;
  
  private FunctionTreeVisitor(ScriptNode root)
  {
    this.root = root;
    this.functions = new LinkedList<FunctionNode>();
  }
  
  public static List<FunctionNode> getFunctions(ScriptNode node)
  {
    FunctionTreeVisitor visitor = new FunctionTreeVisitor(node);
    node.visit(visitor);
    return visitor.functions;
  }
  
  public boolean visit(AstNode node)
  {
    if (node == this.root) {
      return true;
    }
    if ((node instanceof FunctionNode))
    {
      this.functions.add((FunctionNode)node);
      return false;
    }
    return true;
  }
}
