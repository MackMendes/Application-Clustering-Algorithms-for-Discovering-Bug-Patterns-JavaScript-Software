package ca.ubc.ece.salt.pangor.original.js.analysis.utilities;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.flow.IdentifiersTreeVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ScriptNode;

public class AnalysisUtilities
{
  public static String getFunctionSignature(FunctionNode function)
  {
    List<AstNode> params = function.getParams();
    String signature = "(";
    for (AstNode param : params) {
      if ((param instanceof Name))
      {
        if (!signature.equals("(")) {
          signature = signature + ",";
        }
        signature = signature + ((Name)param).getIdentifier();
      }
    }
    signature = signature + ")";
    return signature;
  }
  
  public static String getFunctionName(ScriptNode function)
  {
    if ((function instanceof FunctionNode))
    {
      String name = ((FunctionNode)function).getName();
      if (name.isEmpty()) {
        return "~anonymous~";
      }
      return name;
    }
    return "~script~";
  }
  
  public static AstNode isParameter(Name name)
  {
    AstNode parent = name.getParent();
    while (!(parent instanceof AstRoot))
    {
      if ((parent instanceof FunctionNode))
      {
        List<AstNode> parameters = ((FunctionNode)parent).getParams();
        for (AstNode parameter : parameters) {
          if (((parameter instanceof Name)) && 
            (((Name)parameter).getIdentifier().equals(name.getIdentifier()))) {
            return parameter;
          }
        }
      }
      parent = parent.getParent();
    }
    return null;
  }
  
  public static AstNode getComparison(AstNode node)
  {
    AstNode parent = node.getParent();
    if ((parent instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)parent;
      if (isEquivalenceOperator(ie.getOperator()))
      {
        if (ie.getRight() == node) {
          return ie.getLeft();
        }
        return ie.getRight();
      }
    }
    return null;
  }
  
  public static AstNode getTopLevelFieldIdentifier(AstNode node)
    throws IllegalArgumentException
  {
    if ((node.getParent() instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)node.getParent();
      if ((isIdentifierOperator(ie.getOperator())) && 
        (!(ie.getLeft() instanceof FunctionCall)) && (!(ie.getRight() instanceof FunctionCall))) {
        return getTopLevelIdentifier(ie);
      }
    }
    return node;
  }
  
  public static AstNode getTopLevelIdentifier(AstNode node)
    throws IllegalArgumentException
  {
    if ((node.getParent() instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)node.getParent();
      if (isIdentifierOperator(ie.getOperator())) {
        return getTopLevelIdentifier(ie);
      }
    }
    return node;
  }
  
  public static String getIdentifier(AstNode node)
    throws IllegalArgumentException
  {
    if ((node instanceof Name)) {
      return ((Name)node).getIdentifier();
    }
    if ((node instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)node;
      if (isIdentifierOperator(ie.getOperator()))
      {
        String left = getIdentifier(ie.getLeft());
        String right = getIdentifier(ie.getRight());
        if ((left == null) || (right == null)) {
          return null;
        }
        return left + "." + right;
      }
      return null;
    }
    if ((node instanceof FunctionCall))
    {
      FunctionCall fc = (FunctionCall)node;
      String identifier = getIdentifier(fc.getTarget());
      if (identifier == null) {
        return null;
      }
      return identifier + "()";
    }
    if ((node instanceof ParenthesizedExpression))
    {
      ParenthesizedExpression pe = (ParenthesizedExpression)node;
      String identifier = getIdentifier(pe.getExpression());
      if (identifier == null) {
        return null;
      }
      return identifier;
    }
    return null;
  }
  
  public static List<String> getRHSIdentifiers(AstNode node)
  {
    IdentifiersTreeVisitor visitor = new IdentifiersTreeVisitor();
    node.visit(visitor);
    return visitor.variableIdentifiers;
  }
  
  public static boolean isIdentifierOperator(int tokenType)
  {
    if ((tokenType == 33) || (tokenType == 34)) {
      return true;
    }
    return false;
  }
  
  public static boolean isAssignmentOperator(int tokenType)
  {
    if ((tokenType == 90) || (tokenType == 103)) {
      return true;
    }
    return false;
  }
  
  public static boolean isEquivalenceOperator(int tokenType)
  {
    if ((tokenType == 46) || (tokenType == 47) || (tokenType == 12) || (tokenType == 13)) {
      return true;
    }
    return false;
  }
  
  public static Set<String> getUsedIdentifiers(AstNode node)
  {
    UseTreeVisitor useVisitor = new UseTreeVisitor();
    node.visit(useVisitor);
    return useVisitor.getUsedIdentifiers();
  }
  
  public static String getFunctionCallName(FunctionCall call)
  {
    AstNode target = call.getTarget();
    if ((target instanceof Name)) {
      return ((Name)target).getIdentifier();
    }
    if ((target instanceof PropertyGet)) {
      return ((PropertyGet)target).getRight().toSource();
    }
    if ((target instanceof FunctionNode))
    {
      Name targetFunctionName = ((FunctionNode)target).getFunctionName();
      if (targetFunctionName == null) {
        return "~anonymous~";
      }
      return targetFunctionName.toString();
    }
    return "?";
  }
  
  public static String getFunctionFullCallName(FunctionCall call)
  {
    AstNode target = call.getTarget();
    if (((target instanceof Name)) || ((target instanceof PropertyGet))) {
      return target.toSource();
    }
    if ((target instanceof FunctionNode))
    {
      Name targetFunctionName = ((FunctionNode)target).getFunctionName();
      if (targetFunctionName == null) {
        return "~anonymous~";
      }
      return targetFunctionName.toString();
    }
    return "?";
  }
  
  public static String getBoundedContextFunctionName(FunctionCall call)
  {
    PropertyGet target = (PropertyGet)call.getTarget();
    if (((target.getLeft() instanceof Name)) || ((target.getLeft() instanceof PropertyGet))) {
      return target.getLeft().toSource();
    }
    if ((target.getLeft() instanceof FunctionNode))
    {
      Name targetFunctionName = ((FunctionNode)target.getLeft()).getFunctionName();
      if (targetFunctionName == null) {
        return "~anonymous~";
      }
      return targetFunctionName.toString();
    }
    return "?";
  }
  
  public static List<AstNode> getChangedArguments(FunctionCall call)
  {
    List<AstNode> arguments = new ArrayList<AstNode>();
    
    AstNode mappedNode = (AstNode)call.getMapping();
    if ((mappedNode == null) || (!(mappedNode instanceof FunctionCall))) {
      return arguments;
    }
    FunctionCall mappedCall = (FunctionCall)mappedNode;
    int argumentIndex;
    if (call.getArguments().size() == mappedCall.getArguments().size())
    {
      for (argumentIndex = 0; argumentIndex < call.getArguments().size(); argumentIndex++)
      {
        AstNode argument = (AstNode)call.getArguments().get(argumentIndex);
        if ((!(argument instanceof FunctionCall)) && (!(argument instanceof FunctionNode))) {
          if (!argument.getASTNodeType().equals(((AstNode)mappedCall.getArguments().get(argumentIndex)).getASTNodeType())) {
            arguments.add(argument);
          }
        }
      }
    }
    else
    {
      for (AstNode argument : call.getArguments()) {
        if ((!(argument instanceof FunctionCall)) && (!(argument instanceof FunctionNode))) {
          if (argument.getChangeType() == ClassifiedASTNode.ChangeType.INSERTED) {
            arguments.add(argument);
          }
        }
      }
      for (AstNode argument : mappedCall.getArguments()) {
        if ((!(argument instanceof FunctionCall)) && (!(argument instanceof FunctionNode))) {
          if (argument.getChangeType() == ClassifiedASTNode.ChangeType.REMOVED) {
            arguments.add(argument);
          }
        }
      }
    }
    for (AstNode argument : call.getArguments()) {
      if ((argument instanceof ObjectLiteral))
      {
        ChangeTypeFilterVisitor visitor = new ChangeTypeFilterVisitor(new ClassifiedASTNode.ChangeType[] { ClassifiedASTNode.ChangeType.REMOVED, ClassifiedASTNode.ChangeType.INSERTED });
        
        argument.visit(visitor);
        if (visitor.storedNodes.size() > 0)
        {
          argument.setChangeType(ClassifiedASTNode.ChangeType.UPDATED);
          arguments.add(argument);
        }
      }
    }
    return arguments;
  }
}
