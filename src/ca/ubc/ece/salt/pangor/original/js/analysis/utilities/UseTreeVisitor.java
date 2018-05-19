package ca.ubc.ece.salt.pangor.original.js.analysis.utilities;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.VariableInitializer;

public class UseTreeVisitor
  implements NodeVisitor
{
  private Set<String> usedIdentifiers;
  
  public static Set<String> getSpecialTypeChecks(AstNode statement)
  {
    UseTreeVisitor visitor = new UseTreeVisitor();
    if (statement == null) {
      return visitor.usedIdentifiers;
    }
    statement.visit(visitor);
    return visitor.usedIdentifiers;
  }
  
  public UseTreeVisitor()
  {
    this.usedIdentifiers = new HashSet<String>();
  }
  
  public Set<String> getUsedIdentifiers()
  {
    return this.usedIdentifiers;
  }
  
  public boolean visit(AstNode node)
  {
    if ((node instanceof FunctionNode)) {
      return false;
    }
    AstNode left;
    if (((node instanceof Assignment)) || ((node instanceof ObjectProperty)))
    {
      AstNode right = ((InfixExpression)node).getRight();
      check(right);
    }
    else if ((node instanceof VariableInitializer))
    {
      AstNode right = ((VariableInitializer)node).getInitializer();
      check(right);
    }
    else if ((node instanceof ElementGet))
    {
      AstNode element = ((ElementGet)node).getElement();
      check(element);
    }
    else if ((node instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)node;
      if (isUseOperator(ie.getOperator()))
      {
        left = ie.getLeft();
        check(left);
        if ((ie.getOperator() != 108) && 
          (ie.getOperator() != 33) && 
          (ie.getOperator() != 34))
        {
          AstNode right = ie.getRight();
          check(right);
        }
      }
    }
    else if ((node instanceof FunctionCall))
    {
      FunctionCall call = (FunctionCall)node;
      for (AstNode argument : call.getArguments()) {
        check(argument);
      }
      check(call.getTarget());
    }
    else if ((node instanceof ConditionalExpression))
    {
      ConditionalExpression ce = (ConditionalExpression)node;
      check(ce.getTrueExpression());
      check(ce.getFalseExpression());
    }
    else if ((node instanceof ArrayLiteral))
    {
      ArrayLiteral literal = (ArrayLiteral)node;
      for (AstNode element : literal.getElements()) {
        check(element);
      }
    }
    return true;
  }
  
  private void check(AstNode node)
  {
    if (node == null) {
      return;
    }
    ClassifiedASTNode.ChangeType changeType = node.getChangeType();
    if ((changeType == ClassifiedASTNode.ChangeType.MOVED) || (changeType == ClassifiedASTNode.ChangeType.UNCHANGED))
    {
      String identifier = AnalysisUtilities.getIdentifier(node);
      if (identifier != null) {
        this.usedIdentifiers.add(identifier);
      }
    }
  }
  
  public static boolean isUseOperator(int tokenType)
  {
    int[] useOperators = { 33, 34, 9, 10, 11, 21, 22, 23, 24, 25, 36, 37, 91, 92, 93, 94, 95, 97, 98, 99, 100, 101, 108, 106, 107 };
    
    return ArrayUtils.contains(useOperators, tokenType);
  }
}
