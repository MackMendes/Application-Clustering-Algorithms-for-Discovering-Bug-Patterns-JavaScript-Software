package ca.ubc.ece.salt.pangor.original.api;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KeywordUse
  extends KeywordDefinition
{
  public KeywordContext context;
  public ClassifiedASTNode.ChangeType changeType;
  public String apiPackage;
  
  public KeywordUse(KeywordDefinition.KeywordType type, KeywordContext context, String keyword, ClassifiedASTNode.ChangeType changeType, String api)
  {
    super(type, keyword);
    
    this.context = context;
    this.changeType = changeType;
    this.apiPackage = api;
  }
  
  public KeywordUse(KeywordDefinition.KeywordType type, KeywordContext context, String keyword, ClassifiedASTNode.ChangeType changeType)
  {
    super(type, keyword);
    
    this.context = context;
    this.changeType = changeType;
    this.apiPackage = "_unknownapi_";
  }
  
  public KeywordUse(KeywordDefinition.KeywordType type, String keyword)
  {
    this(type, KeywordContext.UNKNOWN, keyword, ClassifiedASTNode.ChangeType.UNKNOWN);
  }
  
  public KeywordUse(KeywordDefinition.KeywordType type, KeywordContext context, String keyword, ClassifiedASTNode.ChangeType changeType, AbstractAPI path)
  {
    this(type, context, keyword, changeType);
    setAPI(path);
  }
  
  public String getPackageName()
  {
    if (this.api != null) {
      return this.api.getPackageName();
    }
    return this.apiPackage;
  }
  
  public boolean equals(Object obj)
  {
    if ((obj instanceof KeywordUse))
    {
      KeywordUse that = (KeywordUse)obj;
      if ((this.type == that.type) && (this.context == that.context) && 
        (this.keyword.equals(that.keyword)) && (this.changeType == that.changeType)) {
        return true;
      }
    }
    else if ((obj instanceof KeywordDefinition))
    {
      KeywordDefinition that = (KeywordDefinition)obj;
      if ((this.type == that.type) && (this.keyword.equals(that.keyword))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return (this.type.toString() + "_" + this.keyword).hashCode();
  }
  
  public String toString()
  {
    if (this.type == KeywordDefinition.KeywordType.PACKAGE) {
      return this.type.toString() + ":" + this.context.toString() + ":" + this.changeType.toString() + ":" + this.keyword;
    }
    if (this.api != null) {
      return this.type.toString() + ":" + this.context.toString() + ":" + this.changeType.toString() + ":" + this.api.getName() + ":" + this.keyword;
    }
    return this.type.toString() + ":" + this.context.toString() + ":" + this.changeType.toString() + ":" + this.apiPackage + ":" + this.keyword;
  }
  
  public static enum KeywordContext
  {
    UNKNOWN,  CONDITION,  EXPRESSION,  ASSIGNMENT_LHS,  ASSIGNMENT_RHS,  REQUIRE,  CLASS_DECLARATION,  METHOD_DECLARATION,  PARAMETER_DECLARATION,  VARIABLE_DECLARATION,  METHOD_CALL,  ARGUMENT,  EXCEPTION_CATCH,  EVENT_REGISTER,  EVENT_REMOVE,  STATEMENT;
    
    private KeywordContext() {}
  }
  
  public static Map<KeywordUse, Integer> filterMapByChangeType(Map<KeywordUse, Integer> keywordsMap, ClassifiedASTNode.ChangeType... changeTypes)
  {
    Map<KeywordUse, Integer> newList = new HashMap<KeywordUse, Integer>();
    for (Map.Entry<KeywordUse, Integer> keywordEntry : keywordsMap.entrySet()) {
      if (Arrays.asList(changeTypes).contains(((KeywordUse)keywordEntry.getKey()).changeType)) {
        newList.put(keywordEntry.getKey(), keywordEntry.getValue());
      }
    }
    return newList;
  }
}
