package ca.ubc.ece.salt.pangor.original.js.analysis.scope;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import java.util.List;
import java.util.Map;

public interface Scope<T> {
	  public abstract Scope<T> getParent();
	  
	  public abstract T getScope();
	  
	  public abstract Map<String, T> getVariables();
	  
	  public abstract Map<String, T> getGlobals();
	  
	  public abstract List<Scope<T>> getChildren();
	  
	  public abstract String getIdentity();
	  
	  public abstract T getVariableDeclaration(String paramString);
	  
	  public abstract Scope<T> getFunctionScope(ClassifiedASTNode paramClassifiedASTNode);
	  
	  public abstract boolean isLocal(String paramString);
	  
	  public abstract boolean isGlobal(String paramString);
}

