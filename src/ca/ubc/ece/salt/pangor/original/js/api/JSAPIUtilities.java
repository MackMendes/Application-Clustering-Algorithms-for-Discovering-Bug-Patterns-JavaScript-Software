package ca.ubc.ece.salt.pangor.original.js.api;

import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.SpecialTypeAnalysisUtilities;
import java.util.Arrays;
import java.util.List;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

public class JSAPIUtilities
{
  public static KeywordDefinition.KeywordType getTokenType(AstNode token)
  {
    if (isJavaScriptReserved(token)) {
      return KeywordDefinition.KeywordType.RESERVED;
    }
    KeywordDefinition.KeywordType type = KeywordDefinition.KeywordType.UNKNOWN;
    type = typeSwitch(token);
    
    return type;
  }
  
  public static KeywordUse.KeywordContext getTokenContext(AstNode token)
  {
    KeywordUse.KeywordContext context = KeywordUse.KeywordContext.UNKNOWN;
    context = contextSwitch(token);
    
    return context;
  }
  
  private static boolean isJavaScriptReservedLiteral(AstNode token)
  {
    List<String> JAVASCRIPT_RESERVED_WORDS = Arrays.asList(new String[] { 
    		"abstract", "await", "boolean", "break", "byte", "case", "catch", "char", "class", "const", 
    		"continue", "debugger", "default", "delete", "do", "double", "else", "enum", "eval ", "export", 
    		"extends", "false", "final", "finally", "float", "for", "function", "goto", "if", "implements", 
    		"import", "in", "instanceof", "int", "interface", "let", "long", "module", "native", "new", 
    		"Number", "null", "package", "private", "protected", "public", "RegExp", "return", "short", 
    		"static", "String", "super", "switch", "synchronized", "this", "throw", "throws", "transient", 
    		"true", "try", "typeof", "undefined ", "var", "void", "volatile", "while", "with", "yield" });
    
    if ((token instanceof Name))
    {
      Name name = (Name)token;
      if (JAVASCRIPT_RESERVED_WORDS.contains(name.getIdentifier())) {
        return true;
      }
    }
    return false;
  }
  
  private static KeywordDefinition.KeywordType typeSwitch(AstNode token)
  {
    if ((token == null) || (token.getParent() == null)) {
      return KeywordDefinition.KeywordType.UNKNOWN;
    }
    AstNode parent = token.getParent();
    if (SpecialTypeAnalysisUtilities.isFalsey(token)) {
      return KeywordDefinition.KeywordType.RESERVED;
    }
    if (((token instanceof ReturnStatement)) || ((token instanceof BreakStatement)) || 
    		((token instanceof ContinueStatement)) || ((token instanceof VariableDeclaration)) || 
    		((token instanceof NewExpression)) || ((token instanceof TryStatement))) {
      return KeywordDefinition.KeywordType.RESERVED;
    }
    if ((token instanceof StringLiteral))
    {
      if (((StringLiteral)token).toSource().equals("\"\"")) {
        return KeywordDefinition.KeywordType.RESERVED;
      }
    }
    else if (((token instanceof NumberLiteral)) && (((NumberLiteral)token).getNumber() == 0.0D)) {
      return KeywordDefinition.KeywordType.RESERVED;
    }
    if ((parent instanceof FunctionNode))
    {
      FunctionNode function = (FunctionNode)parent;
      if ((function.getFunctionName() == token) && (Character.isUpperCase(function.getName().charAt(0)))) {
        return KeywordDefinition.KeywordType.CLASS;
      }
      if (function.getFunctionName() == token) {
        return KeywordDefinition.KeywordType.METHOD;
      }
      if (function.getParams().contains(token)) {
        return KeywordDefinition.KeywordType.PARAMETER;
      }
    }
    else if ((parent instanceof VariableInitializer))
    {
      VariableInitializer initializer = (VariableInitializer)parent;
      if (initializer.getTarget() == token) {
        return KeywordDefinition.KeywordType.VARIABLE;
      }
    }
    else if ((parent instanceof FunctionCall))
    {
      FunctionCall call = (FunctionCall)parent;
      if ((call.getTarget() instanceof Name))
      {
        Name target = (Name)call.getTarget();
        if ((target.getIdentifier().equals("require")) && (call.getArguments().size() == 1))
        {
          AstNode pack = (AstNode)call.getArguments().get(0);
          if (((pack instanceof StringLiteral)) && (pack == token)) {
            return KeywordDefinition.KeywordType.PACKAGE;
          }
        }
        else
        {
          if (target.getIdentifier().equals("~exception")) {
            return KeywordDefinition.KeywordType.EXCEPTION;
          }
          if (target == token) {
            return KeywordDefinition.KeywordType.METHOD;
          }
        }
      }
      else if ((call.getTarget() instanceof PropertyGet))
      {
        PropertyGet target = (PropertyGet)call.getTarget();
        if ((target.getProperty().getIdentifier().equals("on")) && (call.getArguments().size() == 2))
        {
          AstNode event = (AstNode)call.getArguments().get(0);
          if (((event instanceof StringLiteral)) && (event == token)) {
            return KeywordDefinition.KeywordType.EVENT;
          }
        }
        else if ((target.getProperty().getIdentifier().equals("removeListener")) && (call.getArguments().size() == 2))
        {
          AstNode event = (AstNode)call.getArguments().get(0);
          if (((event instanceof StringLiteral)) && (event == token)) {
            return KeywordDefinition.KeywordType.EVENT;
          }
        }
        else if ((target.getProperty().getIdentifier().equals("removeAllListeners")) && (call.getArguments().size() == 1))
        {
          AstNode event = (AstNode)call.getArguments().get(0);
          if (((event instanceof StringLiteral)) && (event == token)) {
            return KeywordDefinition.KeywordType.EVENT;
          }
        }
      }
    }
    else if (((parent instanceof PropertyGet)) && 
      ((parent.getParent() instanceof FunctionCall)))
    {
      PropertyGet propertyGet = (PropertyGet)token.getParent();
      if (propertyGet.getTarget() == token)
      {
        if (Character.isUpperCase(token.toSource().charAt(0))) {
          return KeywordDefinition.KeywordType.CLASS;
        }
        return KeywordDefinition.KeywordType.VARIABLE;
      }
      if (propertyGet.getProperty() == token) {
        return KeywordDefinition.KeywordType.METHOD;
      }
    }
    else if ((parent instanceof CatchClause))
    {
      CatchClause catchClause = (CatchClause)parent;
      if (catchClause.getVarName().toSource().equals(token.toSource())) {
        return KeywordDefinition.KeywordType.EXCEPTION;
      }
    }
    if ((token instanceof Name)) {
      return getVariableOrFieldType(token, (Name)token);
    }
    return KeywordDefinition.KeywordType.UNKNOWN;
  }
  
  private static KeywordUse.KeywordContext contextSwitch(AstNode token)
  {
    if ((token == null) || (token.getParent() == null)) {
      return KeywordUse.KeywordContext.UNKNOWN;
    }
    AstNode parent = token.getParent();
    if (SpecialTypeAnalysisUtilities.isFalsey(token)) {
      return KeywordUse.KeywordContext.CONDITION;
    }
    if (((token instanceof ReturnStatement)) || ((token instanceof BreakStatement)) || 
    		((token instanceof ContinueStatement)) || ((token instanceof VariableDeclaration)) || 
    		((token instanceof NewExpression)) || ((token instanceof TryStatement))) {
      return KeywordUse.KeywordContext.STATEMENT;
    }
    if ((token instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)token;
      if ((ie.getType() == 46) || (ie.getType() == 47) || 
        (ie.getType() == 12) || (ie.getType() == 13)) {
        return KeywordUse.KeywordContext.CONDITION;
      }
    }
    else if ((parent instanceof FunctionNode))
    {
      FunctionNode function = (FunctionNode)parent;
      if ((function.getFunctionName() == token) && (Character.isUpperCase(function.getName().charAt(0)))) {
        return KeywordUse.KeywordContext.CLASS_DECLARATION;
      }
      if (function.getFunctionName() == token) {
        return KeywordUse.KeywordContext.METHOD_DECLARATION;
      }
      if (function.getParams().contains(token)) {
        return KeywordUse.KeywordContext.PARAMETER_DECLARATION;
      }
    }
    else if ((parent instanceof VariableInitializer))
    {
      VariableInitializer initializer = (VariableInitializer)parent;
      if (initializer.getTarget() == token) {
        return KeywordUse.KeywordContext.VARIABLE_DECLARATION;
      }
    }
    else if ((parent instanceof FunctionCall))
    {
      FunctionCall call = (FunctionCall)parent;
      if ((call.getTarget() instanceof Name))
      {
        Name target = (Name)call.getTarget();
        if ((target.getIdentifier().equals("require")) && (call.getArguments().size() == 1))
        {
          AstNode pack = (AstNode)call.getArguments().get(0);
          if (((pack instanceof StringLiteral)) && (pack == token)) {
            return KeywordUse.KeywordContext.REQUIRE;
          }
        }
      }
      else if ((call.getTarget() instanceof PropertyGet))
      {
        PropertyGet target = (PropertyGet)call.getTarget();
        if ((target.getProperty().getIdentifier().equals("on")) && (call.getArguments().size() == 2))
        {
          AstNode event = (AstNode)call.getArguments().get(0);
          if (((event instanceof StringLiteral)) && (event == token)) {
            return KeywordUse.KeywordContext.EVENT_REGISTER;
          }
        }
        else if ((target.getProperty().getIdentifier().equals("removeListener")) && (call.getArguments().size() == 2))
        {
          AstNode event = (AstNode)call.getArguments().get(0);
          if (((event instanceof StringLiteral)) && (event == token)) {
            return KeywordUse.KeywordContext.EVENT_REMOVE;
          }
        }
        else if ((target.getProperty().getIdentifier().equals("removeAllListeners")) && (call.getArguments().size() == 1))
        {
          AstNode event = (AstNode)call.getArguments().get(0);
          if (((event instanceof StringLiteral)) && (event == token)) {
            return KeywordUse.KeywordContext.EVENT_REMOVE;
          }
        }
      }
    }
    else if ((parent instanceof CatchClause))
    {
      CatchClause catchClause = (CatchClause)parent;
      if (catchClause.getVarName() == token) {
        return KeywordUse.KeywordContext.EXCEPTION_CATCH;
      }
    }
    return getVariableOrFieldContext(token);
  }
  
  private static KeywordDefinition.KeywordType getVariableOrFieldType(AstNode vf, Name node)
  {
    if ((vf.getParent() instanceof PropertyGet)) {
      return getVariableOrFieldType(vf.getParent(), node);
    }
    if ((vf != node) && 
      ((node.getParent() instanceof PropertyGet)) && 
      ((vf.getParent() instanceof FunctionCall)))
    {
      PropertyGet property = (PropertyGet)node.getParent();
      if (property.getTarget() == node) {
        return KeywordDefinition.KeywordType.VARIABLE;
      }
    }
    if ((vf instanceof PropertyGet))
    {
      if (node.getIdentifier().equals(node.getIdentifier().toUpperCase())) {
        return KeywordDefinition.KeywordType.CONSTANT;
      }
      return KeywordDefinition.KeywordType.FIELD;
    }
    return KeywordDefinition.KeywordType.VARIABLE;
  }
  
  private static KeywordUse.KeywordContext getVariableOrFieldContext(AstNode vfr)
  {
    if ((vfr.getParent() instanceof PropertyGet)) {
      return getVariableOrFieldContext(vfr.getParent());
    }
    if ((vfr.getParent() instanceof FunctionCall))
    {
      FunctionCall call = (FunctionCall)vfr.getParent();
      if (call.getTarget() == vfr) {
        return KeywordUse.KeywordContext.METHOD_CALL;
      }
      if (((call.getTarget() instanceof Name)) && (((Name)call.getTarget()).getIdentifier().equals("~exception"))) {
        return KeywordUse.KeywordContext.EXCEPTION_CATCH;
      }
      return KeywordUse.KeywordContext.ARGUMENT;
    }
    if ((vfr.getParent() instanceof VariableInitializer))
    {
      VariableInitializer initializer = (VariableInitializer)vfr.getParent();
      if (initializer.getInitializer() == vfr) {
        return KeywordUse.KeywordContext.ASSIGNMENT_RHS;
      }
    }
    else
    {
      if (((vfr.getParent() instanceof Assignment)) || 
        ((vfr.getParent() instanceof ObjectProperty)))
      {
        InfixExpression assignment = (InfixExpression)vfr.getParent();
        if (assignment.getLeft() == vfr) {
          return KeywordUse.KeywordContext.ASSIGNMENT_LHS;
        }
        return KeywordUse.KeywordContext.ASSIGNMENT_RHS;
      }
      if ((vfr.getParent() instanceof InfixExpression))
      {
        InfixExpression ie = (InfixExpression)vfr.getParent();
        if (isConditionalComparator(ie.getOperator())) {
          return KeywordUse.KeywordContext.CONDITION;
        }
        return KeywordUse.KeywordContext.EXPRESSION;
      }
    }
    return KeywordUse.KeywordContext.UNKNOWN;
  }
  
  public static boolean isJavaScriptReserved(AstNode token)
  {
    if (isJavaScriptReservedLiteral(token)) {
      return true;
    }
    switch (token.getType())
    {
    case 12: 
    case 13: 
    case 31: 
    case 32: 
    case 42: 
    case 43: 
    case 44: 
    case 45: 
    case 46: 
    case 47: 
    case 69: 
    case 106: 
    case 107: 
      return true;
    }
    return false;
  }
  
  private static boolean isConditionalComparator(int tokenType)
  {
    switch (tokenType)
    {
    case 12: 
    case 13: 
    case 14: 
    case 16: 
    case 17: 
    case 46: 
    case 47: 
    case 104: 
    case 105: 
      return true;
    }
    return false;
  }
}
