/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.api;

import ca.ubc.ece.salt.pangor.original.api.ClassAPI;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAPI
{
  protected List<KeywordDefinition> keywords;
  protected List<ClassAPI> classes;
  protected AbstractAPI parent;
  
  public AbstractAPI(List<String> methodNames, List<String> fieldNames, List<String> constantNames, List<String> eventNames, List<ClassAPI> classes)
  {
    this.keywords = new ArrayList<KeywordDefinition>();
    this.classes = new ArrayList<ClassAPI>();
    for (String methodName : methodNames) {
      this.keywords.add(new KeywordDefinition(KeywordDefinition.KeywordType.METHOD, methodName, this));
    }
    for (String fieldName : fieldNames) {
      this.keywords.add(new KeywordDefinition(KeywordDefinition.KeywordType.FIELD, fieldName, this));
    }
    for (String constantName : constantNames) {
      this.keywords.add(new KeywordDefinition(KeywordDefinition.KeywordType.CONSTANT, constantName, this));
    }
    for (String eventName : eventNames) {
      this.keywords.add(new KeywordDefinition(KeywordDefinition.KeywordType.EVENT, eventName, this));
    }
    this.classes = classes;
    addParentToChildren(this.classes);
  }
  
  public KeywordDefinition getFirstKeyword(KeywordDefinition.KeywordType type, String keyword)
  {
    return getFirstKeyword(new KeywordDefinition(type, keyword));
  }
  
  public KeywordDefinition getFirstKeyword(KeywordDefinition keyword)
  {
    List<KeywordDefinition> keywordsList = getAllKeywords(keyword);
    if (keywordsList.size() > 0) {
      return (KeywordDefinition)keywordsList.get(0);
    }
    return null;
  }
  
  public List<KeywordDefinition> getAllKeywords(KeywordDefinition keyword)
  {
    List<KeywordDefinition> keywordsList = new ArrayList<KeywordDefinition>();
    
    recursiveKeywordSearch(this, keyword, keywordsList);
    
    return keywordsList;
  }
  
  public double getChangeLikelihood(Map<KeywordDefinition, Integer> insertedKeywords, Map<KeywordDefinition, Integer> removedKeywords, Map<KeywordDefinition, Integer> updatedKeywords, Map<KeywordDefinition, Integer> unchangedKeywords)
  {
    return getUseLikelihood(insertedKeywords, removedKeywords, updatedKeywords, unchangedKeywords);
  }
  
  public double getUseLikelihood(Map<KeywordDefinition, Integer> insertedKeywords, Map<KeywordDefinition, Integer> removedKeywords, Map<KeywordDefinition, Integer> updatedKeywords, Map<KeywordDefinition, Integer> unchangedKeywords)
  {
    Map<KeywordDefinition, Integer> mergedMap = new HashMap<KeywordDefinition, Integer>();
    if (insertedKeywords != null) {
      mergedMap.putAll(insertedKeywords);
    }
    if (removedKeywords != null) {
      mergedMap.putAll(removedKeywords);
    }
    if (updatedKeywords != null) {
      mergedMap.putAll(updatedKeywords);
    }
    if (unchangedKeywords != null) {
      mergedMap.putAll(unchangedKeywords);
    }
    return 1.0D;
  }
  
  public abstract String getName();
  
  public String getPackageName()
  {
    AbstractAPI lastParent = this.parent;
    while (lastParent.parent != null) {
      lastParent = lastParent.parent;
    }
    return lastParent.getPackageName();
  }
  
  protected void recursiveKeywordSearch(AbstractAPI api, KeywordDefinition keyword, List<KeywordDefinition> outputList)
  {
    int index = api.keywords.indexOf(keyword);
    if (index != -1) {
      outputList.add(api.keywords.get(index));
    }
    for (ClassAPI klass : api.classes) {
      recursiveKeywordSearch(klass, keyword, outputList);
    }
  }
  
  protected void addParentToChildren(List<ClassAPI> children)
  {
    for (ClassAPI child : children) {
      child.parent = this;
    }
  }
}

