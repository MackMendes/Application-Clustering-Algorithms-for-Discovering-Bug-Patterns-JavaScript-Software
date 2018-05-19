package ca.ubc.ece.salt.pangor.original.js.analysis;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.analysis.flow.FunctionTreeVisitor;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.JavaScriptScope;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.ScopeVisitor;
import ca.ubc.ece.salt.pangor.original.js.api.APIModelVisitor;
import ca.ubc.ece.salt.pangor.original.js.api.JSAPIFactory;
import ca.ubc.ece.salt.pangor.original.pointsto.PointsToPrediction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;

public class ScriptAnalysis
  extends SourceCodeFileAnalysis
{
  protected List<FunctionAnalysis> functionAnalyses;
  private static int anonymousIDGen = 0;
  
  public ScriptAnalysis(List<FunctionAnalysis> functionAnalyses)
  {
    this.functionAnalyses = functionAnalyses;
  }
  
  public void analyze(SourceCodeFileChange sourceCodeFileChange, Map<IPredicate, IRelation> facts, ClassifiedASTNode root, List<CFG> cfgs)
    throws Exception
  {
    if (!(root instanceof AstRoot)) {
      throw new IllegalArgumentException("The AST must be parsed from Eclipse JDT.");
    }
    AstRoot script = (AstRoot)root;
    
    Map<ScriptNode, JavaScriptScope> scopeMap = new HashMap<ScriptNode, JavaScriptScope>();
    JavaScriptScope scope = buildScopeTree(script, null, scopeMap, null);
    
    PointsToPrediction model = new PointsToPrediction(JSAPIFactory.buildTopLevelAPI(), APIModelVisitor.getScriptFeatureVector(script));
    
    Stack<JavaScriptScope> stack = new Stack<JavaScriptScope>();
    stack.push(scope);
    while (!stack.isEmpty())
    {
      JavaScriptScope currentScope = (JavaScriptScope)stack.pop();
      ScriptNode functionDeclaration = currentScope.scope;
      for (FunctionAnalysis functionAnalysis : this.functionAnalyses) {
        functionAnalysis.analyze(sourceCodeFileChange, facts, 
          getFunctionCFG(functionDeclaration, cfgs), currentScope, model);
      }
      for (Scope<AstNode> child : currentScope.children) {
        stack.push((JavaScriptScope)child);
      }
    }
  }
  
  private JavaScriptScope buildScopeTree(ScriptNode function, JavaScriptScope parent, Map<ScriptNode, JavaScriptScope> scopeMap, String parentIdentity)
    throws Exception
  {
    String identity = function.getIdentity();
    if (identity == null)
    {
      identity = String.valueOf(getAnonymousFunctionID());
      function.setIdentity(identity);
      if (function.getMapping() != null) {
        ((ScriptNode)function.getMapping()).setIdentity(identity);
      }
    }
    String name = "Script";
    if (parentIdentity != null)
    {
      assert ((function instanceof FunctionNode));
      String functionName = ((FunctionNode)function).getName();
      name = parentIdentity + "." + functionName;
    }
    JavaScriptScope scope = new JavaScriptScope(parent, function, name, identity);
    if (parent != null) {
      parent.children.add(scope);
    }
    ScopeVisitor.getLocalScope(scope);
    
    scopeMap.put(function, scope);
    
    List<FunctionNode> methods = FunctionTreeVisitor.getFunctions(function);
    for (FunctionNode method : methods) {
      buildScopeTree(method, scope, scopeMap, identity);
    }
    return scope;
  }
  
  private CFG getFunctionCFG(ClassifiedASTNode node, List<CFG> cfgs)
  {
    for (CFG cfg : cfgs) {
      if (cfg.getEntryNode().getStatement() == node) {
        return cfg;
      }
    }
    return null;
  }
  
  private static synchronized int getAnonymousFunctionID()
  {
    int id = anonymousIDGen;
    anonymousIDGen += 1;
    return id;
  }
}
