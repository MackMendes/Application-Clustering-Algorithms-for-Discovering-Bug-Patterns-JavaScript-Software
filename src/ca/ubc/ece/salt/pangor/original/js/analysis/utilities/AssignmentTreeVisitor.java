package ca.ubc.ece.salt.pangor.original.js.analysis.utilities;

import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.AnalysisUtilities;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectProperty;

public class AssignmentTreeVisitor
implements NodeVisitor
{
private List<Pair<String, AstNode>> assignedIdentifiers;

public AssignmentTreeVisitor()
{
  this.assignedIdentifiers = new LinkedList<Pair<String, AstNode>>();
}

public List<Pair<String, AstNode>> getAssignedIdentifiers()
{
  return this.assignedIdentifiers;
}

public boolean visit(AstNode node)
{
  if (((node instanceof Assignment)) || ((node instanceof ObjectProperty)))
  {
    InfixExpression assignment = (InfixExpression)node;
    String identifier = AnalysisUtilities.getIdentifier(assignment.getLeft());
    if (identifier != null) {
      this.assignedIdentifiers.add(Pair.of(identifier, assignment.getRight()));
    }
  }
  return true;
}
}
