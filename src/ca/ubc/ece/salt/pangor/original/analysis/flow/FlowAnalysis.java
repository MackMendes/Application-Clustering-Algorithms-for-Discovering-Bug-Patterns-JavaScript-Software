package ca.ubc.ece.salt.pangor.original.analysis.flow;

import ca.ubc.ece.salt.pangor.original.analysis.flow.AbstractLatticeElement;
import ca.ubc.ece.salt.pangor.original.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.original.cfg.CFGNode;
import ca.ubc.ece.salt.pangor.original.js.analysis.FunctionAnalysis;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.Scope;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

public abstract class FlowAnalysis<LE extends AbstractLatticeElement>
extends FunctionAnalysis {
	 public abstract LE entryValue(ScriptNode paramScriptNode);
	  
	  public abstract void transfer(CFGEdge paramCFGEdge, LE paramLE, Scope<AstNode> paramScope);
	  
	  public abstract void transfer(CFGNode paramCFGNode, LE paramLE, Scope<AstNode> paramScope);
	  
	  public abstract LE copy(LE paramLE);
}

