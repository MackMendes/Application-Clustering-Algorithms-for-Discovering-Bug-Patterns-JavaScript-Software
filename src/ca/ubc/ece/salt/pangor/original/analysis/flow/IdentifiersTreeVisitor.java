package ca.ubc.ece.salt.pangor.original.analysis.flow;

import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.AnalysisUtilities;
import java.util.LinkedList;
import java.util.List;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;

public class IdentifiersTreeVisitor
implements NodeVisitor {
    public List<String> variableIdentifiers = new LinkedList<String>();

    public boolean visit(AstNode node) {
        InfixExpression ie;
        String identifier = AnalysisUtilities.getIdentifier(node);
        if (identifier != null) {
            this.variableIdentifiers.add(identifier);
            return false;
        }
        ie = (InfixExpression)node;
        
        if (node instanceof InfixExpression && (ie.getOperator() == 104)) {
            this.visit(ie.getLeft());
            this.visit(ie.getRight());
        }
        return false;
    }
}

