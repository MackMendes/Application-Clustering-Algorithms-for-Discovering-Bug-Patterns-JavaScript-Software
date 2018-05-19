package ca.ubc.ece.salt.pangor.original.js.cfg;

import java.util.LinkedList;
import java.util.List;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class FunctionNodeVisitor
implements NodeVisitor {
    List<FunctionNode> functionNodes = new LinkedList<FunctionNode>();

    public static List<FunctionNode> getFunctions(AstNode node) {
        FunctionNodeVisitor visitor = new FunctionNodeVisitor();
        node.visit(visitor);
        return visitor.functionNodes;
    }

    public boolean visit(AstNode node) {
        if (node instanceof FunctionNode) {
            this.functionNodes.add((FunctionNode)node);
        }
        return true;
    }
}

