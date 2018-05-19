package ca.ubc.ece.salt.pangor.original.js.analysis.utilities;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.WhileLoop;

public class SpecialTypeAnalysisUtilities
{
  public static SpecialType getSpecialType(AstNode node)
  {
    String name = AnalysisUtilities.getIdentifier(node);
    if (name != null)
    {
      if (name.equals("undefined")) {
        return SpecialType.UNDEFINED;
      }
      if (name.equals("NaN")) {
        return SpecialType.NAN;
      }
      return null;
    }
    if ((node instanceof KeywordLiteral))
    {
      if (node.getType() == 42) {
        return SpecialType.NULL;
      }
      return null;
    }
    if ((node instanceof StringLiteral))
    {
      String literal = ((StringLiteral)node).getValue();
      if (literal.isEmpty()) {
        return SpecialType.BLANK;
      }
      return null;
    }
    if ((node instanceof NumberLiteral))
    {
      double literal = ((NumberLiteral)node).getNumber();
      if (literal == 0.0D) {
        return SpecialType.ZERO;
      }
      return null;
    }
    return null;
  }
  
  public static SpecialType getSpecialTypeString(StringLiteral stringLiteral)
  {
    switch (stringLiteral.getValue())
    {
    }
    return null;
  }
  
  public static SpecialTypeCheck getSpecialTypeCheck(AstNode condition, AstNode node)
  {
    if ((node instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)node;
      if (AnalysisUtilities.isEquivalenceOperator(ie.getOperator()))
      {
        String identifier = null;
        SpecialType specialType = null;
        if (getSpecialType(ie.getLeft()) != null)
        {
          specialType = getSpecialType(ie.getLeft());
          identifier = AnalysisUtilities.getIdentifier(ie.getRight());
        }
        else if (getSpecialType(ie.getRight()) != null)
        {
          specialType = getSpecialType(ie.getRight());
          identifier = AnalysisUtilities.getIdentifier(ie.getLeft());
        }
        else
        {
          UnaryExpression ue = null;
          StringLiteral sl = null;
          if (((ie.getLeft() instanceof UnaryExpression)) && ((ie.getRight() instanceof StringLiteral)))
          {
            ue = (UnaryExpression)ie.getLeft();
            sl = (StringLiteral)ie.getRight();
          }
          else if (((ie.getRight() instanceof UnaryExpression)) && ((ie.getLeft() instanceof StringLiteral)))
          {
            ue = (UnaryExpression)ie.getRight();
            sl = (StringLiteral)ie.getLeft();
          }
          if ((ue != null) && (sl != null)) {
            if ((ue.getOperator() == 32) && (sl.getValue().equals("undefined")))
            {
              specialType = SpecialType.UNDEFINED;
              identifier = AnalysisUtilities.getIdentifier(ue.getOperand());
            }
            else if ((ue.getOperator() == 32) && (sl.getValue().equals("function")))
            {
              specialType = SpecialType.FUNCTION;
              identifier = AnalysisUtilities.getIdentifier(ue.getOperand());
            }
          }
        }
        if ((identifier != null) && (specialType != null))
        {
          BranchesOn branchesOn;
          if (ie == condition) {
            branchesOn = BranchesOn.TRUE;
          } else {
            branchesOn = evaluatesTo(condition, ie.getParent(), BranchesOn.TRUE);
          }
          if (specialType == SpecialType.FUNCTION) {
            switch (branchesOn)
            {
            case TRUE: 
              branchesOn = BranchesOn.FALSE;
              break;
            case FALSE: 
              branchesOn = BranchesOn.TRUE;
              break;
            case TRUE_AND: 
              branchesOn = BranchesOn.FALSE_OR;
              break;
            case FALSE_AND: 
              branchesOn = BranchesOn.TRUE_OR;
              break;
            case TRUE_OR: 
              branchesOn = BranchesOn.FALSE_AND;
              break;
            case FALSE_OR: 
              branchesOn = BranchesOn.TRUE_AND;
              break;
			default:
				break;
            }
          }
          boolean isSpecialType = true;
          if ((branchesOn == BranchesOn.TRUE) || (branchesOn == BranchesOn.TRUE_AND)) {
            isSpecialType = true;
          } else if ((branchesOn == BranchesOn.FALSE) || (branchesOn == BranchesOn.FALSE_AND)) {
            isSpecialType = false;
          }
          if ((ie.getOperator() == 13) || (ie.getOperator() == 47)) {
            isSpecialType = !isSpecialType;
          }
          if ((ie.getOperator() == 13) || (ie.getOperator() == 12))
          {
            switch (specialType)
            {
            case UNDEFINED: 
            case NULL: 
              return new SpecialTypeCheck(identifier, SpecialType.NO_VALUE, isSpecialType);
            case BLANK: 
            case ZERO: 
            case EMPTY_ARRAY: 
              return new SpecialTypeCheck(identifier, SpecialType.EMPTY, isSpecialType);
			default:
				return new SpecialTypeCheck(identifier, specialType, isSpecialType);
            }            
          }
          if ((ie.getOperator() == 47) || (ie.getOperator() == 46)) {
            return new SpecialTypeCheck(identifier, specialType, isSpecialType);
          }
        }
      }
    }
    else
    {
      String identifier = AnalysisUtilities.getIdentifier(node);
      if (identifier != null)
      {
        BranchesOn branchesOn;
        if (node == condition) {
          branchesOn = BranchesOn.TRUE;
        } else {
          branchesOn = evaluatesTo(condition, node.getParent(), BranchesOn.TRUE);
        }
        if ((branchesOn == BranchesOn.TRUE) || (branchesOn == BranchesOn.TRUE_AND)) {
          return new SpecialTypeCheck(identifier, SpecialType.FALSEY, false);
        }
        if ((branchesOn == BranchesOn.FALSE) || (branchesOn == BranchesOn.FALSE_AND)) {
          return new SpecialTypeCheck(identifier, SpecialType.FALSEY, true);
        }
      }
    }
    return null;
  }
  
  public static BranchesOn evaluatesTo(AstNode condition, AstNode node, BranchesOn current)
  {
    if (node == condition.getParent()) {
      return current;
    }
    if ((node instanceof UnaryExpression))
    {
      UnaryExpression ue = (UnaryExpression)node;
      if (ue.getOperator() == 26) {
        switch (current)
        {
        case TRUE: 
          return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE);
        case FALSE: 
          return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE);
        case TRUE_AND: 
          return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE_OR);
        case FALSE_AND: 
          return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE_OR);
        case TRUE_OR: 
          return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE_AND);
        case FALSE_OR: 
          return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE_AND);
        case UNKNOWN: 
          return BranchesOn.UNKNOWN;
        }
      }
    }
    else
    {
      if ((node instanceof ParenthesizedExpression)) {
        return evaluatesTo(condition, node.getParent(), current);
      }
      if ((node instanceof InfixExpression))
      {
        InfixExpression ie = (InfixExpression)node;
        if (ie.getType() == 105) {
          switch (current)
          {
          case TRUE: 
          case TRUE_AND: 
            return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE_AND);
          case FALSE: 
          case FALSE_AND: 
            return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE_AND);
          case TRUE_OR: 
          case FALSE_OR: 
            return evaluatesTo(condition, node.getParent(), BranchesOn.UNKNOWN);
          case UNKNOWN: 
            return BranchesOn.UNKNOWN;
          }
        } else if (ie.getType() == 104) {
          switch (current)
          {
          case TRUE: 
          case TRUE_OR: 
            return evaluatesTo(condition, node.getParent(), BranchesOn.TRUE_OR);
          case FALSE: 
          case FALSE_OR: 
            return evaluatesTo(condition, node.getParent(), BranchesOn.FALSE_OR);
          case TRUE_AND: 
          case FALSE_AND: 
            return evaluatesTo(condition, node.getParent(), BranchesOn.UNKNOWN);
          case UNKNOWN: 
            return BranchesOn.UNKNOWN;
          }
        }
      }
    }
    return BranchesOn.UNKNOWN;
  }
  
  public static BranchesOn neg(BranchesOn val)
  {
    if (val == BranchesOn.UNKNOWN) {
      return val;
    }
    if (val == BranchesOn.TRUE) {
      return BranchesOn.FALSE;
    }
    return BranchesOn.TRUE;
  }
  
  public static enum BranchesOn
  {
    TRUE,  FALSE,  TRUE_AND,  FALSE_AND,  TRUE_OR,  FALSE_OR,  UNKNOWN;
    
    private BranchesOn() {}
  }
  
  public static List<Pair<String, AstNode>> getIdentifierAssignments(AstNode node)
  {
    AssignmentTreeVisitor assignmentVisitor = new AssignmentTreeVisitor();
    node.visit(assignmentVisitor);
    return assignmentVisitor.getAssignedIdentifiers();
  }
  
  public static boolean isFalsey(AstNode node)
  {
    AstNode parent = node.getParent();
    String identifier = AnalysisUtilities.getIdentifier(node);
    if (identifier == null) {
      return false;
    }
    if (((parent instanceof IfStatement)) || ((parent instanceof DoLoop)) || ((parent instanceof ForLoop)) || ((parent instanceof WhileLoop)) || ((parent instanceof ConditionalExpression)) || (((parent instanceof InfixExpression)) && 
    
      (((InfixExpression)parent).getOperator() == 105)) || (((parent instanceof InfixExpression)) && 
      (((InfixExpression)parent).getOperator() == 104)) || (((parent instanceof UnaryExpression)) && 
      (((UnaryExpression)parent).getOperator() == 26))) {
      return true;
    }
    if ((parent instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)parent;
      if ((ie.getOperator() == 104) || (ie.getOperator() == 105)) {
        return true;
      }
    }
    AstNode comparedTo = AnalysisUtilities.getComparison(node);
    if (comparedTo != null)
    {
      SpecialType specialType = getSpecialType(node);
      if (specialType == null) {
        return false;
      }
    }
    return false;
  }
  
  public static boolean isStronger(SpecialType source, SpecialType destination)
  {
    switch (source)
    {
    case FALSEY: 
      if (destination != SpecialType.FALSEY) {
        return true;
      }
    case NO_VALUE: 
      if ((destination == SpecialType.UNDEFINED) || (destination == SpecialType.NULL)) {
        return true;
      }
    case EMPTY: 
      if ((destination == SpecialType.BLANK) || (destination == SpecialType.ZERO) || (destination == SpecialType.EMPTY_ARRAY)) {
        return true;
      }
	default:
		return false;
    }
  }
  
  public static boolean isWeaker(SpecialType source, SpecialType destination)
  {
    switch (destination)
    {
    case FALSEY: 
      if (source != SpecialType.FALSEY) {
        return true;
      }
    case NO_VALUE: 
      if ((source == SpecialType.UNDEFINED) || (source == SpecialType.NULL)) {
        return true;
      }
    case EMPTY: 
      if ((source == SpecialType.BLANK) || (source == SpecialType.ZERO) || (source == SpecialType.EMPTY_ARRAY)) {
        return true;
      }
	default:
		return false;
    }
    
  }
  
  public static enum SpecialType
  {
    FALSEY,  NO_VALUE,  EMPTY,  UNDEFINED,  NULL,  NAN,  BLANK,  ZERO,  EMPTY_ARRAY,  FUNCTION;
    
    private SpecialType() {}
  }
}
