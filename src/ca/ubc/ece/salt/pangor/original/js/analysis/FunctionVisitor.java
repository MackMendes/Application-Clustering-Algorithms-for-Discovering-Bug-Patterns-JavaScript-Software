package ca.ubc.ece.salt.pangor.original.js.analysis;

import ca.ubc.ece.salt.pangor.original.js.analysis.scope.JavaScriptScope;
import java.util.HashMap;
import java.util.List;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.VariableInitializer;

public class FunctionVisitor
  implements NodeVisitor
{
  private JavaScriptScope scope;
  
  public static void getLocalScope(JavaScriptScope scope)
  {
    ScriptNode script = scope.scope;
    if ((script instanceof FunctionNode))
    {
      FunctionNode function = (FunctionNode)script;
      FunctionVisitor scopeVisitor = new FunctionVisitor(scope, function.getParams());
      function.getBody().visit(scopeVisitor);
    }
    else
    {
      FunctionVisitor scopeVisitor = new FunctionVisitor(scope);
      script.visit(scopeVisitor);
    }
  }
  
  public FunctionVisitor(JavaScriptScope scope)
  {
    this.scope = scope;
    this.scope.variables = new HashMap<String, AstNode>();
    this.scope.globals = new HashMap<String, AstNode>();
  }
  
  public FunctionVisitor(JavaScriptScope scope, List<AstNode> parameters)
  {
    this.scope = scope;
    this.scope.variables = new HashMap<String, AstNode>();
    this.scope.globals = new HashMap<String, AstNode>();
    for (AstNode parameter : parameters) {
      if ((parameter instanceof Name))
      {
        Name name = (Name)parameter;
        this.scope.variables.put(name.getIdentifier(), name);
      }
    }
  }
  
  public boolean visit(AstNode node)
  {
    if ((node instanceof FunctionNode))
    {
      FunctionNode fn = (FunctionNode)node;
      if (!fn.getName().equals("")) {
        this.scope.variables.put(fn.getName(), fn);
      }
      return false;
    }
    if ((node instanceof VariableInitializer))
    {
      VariableInitializer vi = (VariableInitializer)node;
      if ((vi.getTarget() instanceof Name))
      {
        Name variable = (Name)vi.getTarget();
        this.scope.variables.put(variable.getIdentifier(), variable);
      }
    }
    else if ((node instanceof Assignment))
    {
      Assignment assignment = (Assignment)node;
      if ((assignment.getLeft() instanceof Name))
      {
        Name name = (Name)assignment.getLeft();
        if ((!this.scope.isLocal(name.getIdentifier())) && (!this.scope.globals.containsKey(name.getIdentifier()))) {
          this.scope.globals.put(name.getIdentifier(), name);
        }
      }
    }
    else if ((node instanceof ForInLoop))
    {
      ForInLoop loop = (ForInLoop)node;
      if ((loop.getIterator() instanceof Name))
      {
        Name name = (Name)loop.getIterator();
        if ((!this.scope.isLocal(name.getIdentifier())) && (!this.scope.globals.containsKey(name.getIdentifier()))) {
          this.scope.globals.put(name.getIdentifier(), name);
        }
      }
    }
    else if ((node instanceof ForLoop))
    {
      ForLoop loop = (ForLoop)node;
      if ((loop.getInitializer() instanceof Name))
      {
        Name name = (Name)loop.getInitializer();
        if ((!this.scope.isLocal(name.getIdentifier())) && (!this.scope.globals.containsKey(name.getIdentifier()))) {
          this.scope.globals.put(name.getIdentifier(), name);
        }
      }
    }
    return true;
  }
}
