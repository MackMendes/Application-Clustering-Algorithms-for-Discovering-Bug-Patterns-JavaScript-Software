/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.api;

import ca.ubc.ece.salt.pangor.original.api.AbstractAPI;
import ca.ubc.ece.salt.pangor.original.api.ClassAPI;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.PackageAPI;
import java.util.ArrayList;
import java.util.List;

public class TopLevelAPI
extends AbstractAPI
{
protected List<PackageAPI> packages;

public TopLevelAPI(List<String> keywords, List<PackageAPI> packages, List<String> methodNames, List<String> fieldNames, List<String> constantNames, List<String> eventNames, List<String> exceptionNames, List<ClassAPI> classes)
{
  super(methodNames, fieldNames, constantNames, eventNames, classes);
  for (String exception : exceptionNames) {
    this.keywords.add(new KeywordDefinition(KeywordDefinition.KeywordType.EXCEPTION, exception, this));
  }
  for (String keyword : keywords) {
    this.keywords.add(new KeywordDefinition(KeywordDefinition.KeywordType.RESERVED, keyword, this));
  }
  this.packages = packages;
}

public List<PackageAPI> getPackages()
{
  return this.packages;
}

public List<KeywordDefinition> getAllKeywords(KeywordDefinition keyword)
{
  List<KeywordDefinition> keywordsList = new ArrayList<KeywordDefinition>();
  
  recursiveKeywordSearch(this, keyword, keywordsList);
  
  return keywordsList;
}

protected void recursiveKeywordSearch(TopLevelAPI api, KeywordDefinition keyword, List<KeywordDefinition> outputList)
{
  int index = api.keywords.indexOf(keyword);
  if (index != -1) {
    outputList.add(api.keywords.get(index));
  }
  for (ClassAPI klass : api.classes) {
    recursiveKeywordSearch(klass, keyword, outputList);
  }
  for (PackageAPI pkg : api.packages) {
    recursiveKeywordSearch(pkg, keyword, outputList);
  }
}

public String getPackageName()
{
  return "global";
}

public String getName()
{
  return "global";
}
}

