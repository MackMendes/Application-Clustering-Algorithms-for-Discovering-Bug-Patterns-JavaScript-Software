package ca.ubc.ece.salt.pangor.original.js.analysis.scope;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.Scope;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;

public final class JavaScriptScope
implements Scope<AstNode> {
    public JavaScriptScope parent;
    public ScriptNode scope;
    public Map<String, AstNode> variables;
    public Map<String, AstNode> globals;
    public List<Scope<AstNode>> children;
    public String identity;
    public String name;

    public JavaScriptScope(JavaScriptScope parent, ScriptNode scope, String name, String identity) {
        this.parent = parent;
        this.scope = scope;
        this.variables = new HashMap<String, AstNode>();
        this.globals = new HashMap<String, AstNode>();
        this.children = new LinkedList<Scope<AstNode>>();
        this.name = name;
        this.identity = identity;
    }

    @Override
    public Scope<AstNode> getParent() {
        return this.parent;
    }

    @Override
    public AstNode getScope() {
        return this.scope;
    }

    @Override
    public Map<String, AstNode> getVariables() {
        return this.variables;
    }

    @Override
    public Map<String, AstNode> getGlobals() {
        return this.globals;
    }

    @Override
    public List<Scope<AstNode>> getChildren() {
        return this.children;
    }

    @Override
    public String getIdentity() {
        return this.identity;
    }

    @Override
    public AstNode getVariableDeclaration(String variable) {
        if (this.variables.containsKey(variable)) {
            return this.variables.get(variable);
        }
        if (this.parent == null) {
            return null;
        }
        return this.parent.getVariableDeclaration(variable);
    }

    @Override
    public Scope<AstNode> getFunctionScope(ClassifiedASTNode function) {
        if (!(function instanceof FunctionNode)) {
            throw new IllegalArgumentException("The AST must be parsed from Apache Rhino.");
        }
        if (this.scope == function) {
            return this;
        }
        for (Scope<AstNode> child : this.children)
        {
          Scope<AstNode> functionScope = child.getFunctionScope(function);
          if (functionScope != null) {
            return functionScope;
          }
        }
        return null;
    }

    @Override
    public boolean isLocal(String variable) {
        if (this.parent == null) {
            return false;
        }
        if (this.variables.containsKey(variable)) {
            return true;
        }
        return this.parent.isLocal(variable);
    }

    @Override
    public boolean isGlobal(String variable) {
        if (this.globals.containsKey(variable)) {
            return true;
        }
        if (this.parent == null) {
            return this.variables.containsKey(variable);
        }
        return this.parent.isGlobal(variable);
    }
}

