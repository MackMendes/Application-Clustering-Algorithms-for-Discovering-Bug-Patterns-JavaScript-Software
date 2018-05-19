package ca.ubc.ece.salt.pangor.original.js.analysis.utilities;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class ChangeTypeFilterVisitor
implements NodeVisitor
{
public Set<AstNode> storedNodes = new HashSet<AstNode>();
public Set<ClassifiedASTNode.ChangeType> changeTypes;

public ChangeTypeFilterVisitor(ClassifiedASTNode.ChangeType... changeTypes)
{
  this.changeTypes = new HashSet<ChangeType>(Arrays.asList(changeTypes));
}

public boolean visit(AstNode node)
{
  if (this.changeTypes.contains(node.getChangeType())) {
    this.storedNodes.add(node);
  }
  return true;
}
}
