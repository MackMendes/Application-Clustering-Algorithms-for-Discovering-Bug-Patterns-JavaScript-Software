package ca.ubc.ece.salt.pangor.original.pointsto;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.api.AbstractAPI;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.api.TopLevelAPI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Predictor
{
  protected TopLevelAPI api;
  protected Set<String> requiredPackagesNames;
  protected Map<KeywordUse, Integer> keywords;
  
  @SuppressWarnings("unchecked")
public Predictor(TopLevelAPI api, Map<KeywordUse, Integer> keywords)
  {
    this.api = api;
    this.keywords = keywords;
    ClassifiedASTNode.ChangeType[] changeTypes = new ClassifiedASTNode.ChangeType[] { ClassifiedASTNode.ChangeType.INSERTED, ClassifiedASTNode.ChangeType.UNCHANGED };
    
    this.requiredPackagesNames = lookupRequiredPackages(new Map[] {
      KeywordUse.filterMapByChangeType(keywords, changeTypes) });
  }
  
  public abstract PredictionResults predictKeyword(KeywordUse paramKeywordUse);
  
  public abstract Set<AbstractAPI> predictKeywords(@SuppressWarnings("unchecked") Map<KeywordUse, Integer>... paramVarArgs);
  
  public Set<String> getRequiredPackagesNames()
  {
    return this.requiredPackagesNames;
  }
  
  protected Set<String> lookupRequiredPackages(@SuppressWarnings("unchecked") Map<KeywordUse, Integer>... keywordsMaps)
  {
    Set<String> outputSet = new HashSet<String>();
    
    outputSet.add("global");
    if (keywordsMaps.length == 0) {
      return outputSet;
    }
    for (Map<KeywordUse, Integer> keywordsMap : keywordsMaps) {
      if (keywordsMap != null) {
        for (KeywordUse keyword : keywordsMap.keySet()) {
          if (keyword.type.equals(KeywordDefinition.KeywordType.PACKAGE)) {
            outputSet.add(keyword.keyword);
          }
        }
      }
    }
    return outputSet;
  }
  
  protected void filterKeywordsByPackagesNames(List<KeywordDefinition> keywords, Set<String> packagesNames)
  {
    for (Iterator<KeywordDefinition> iterator = keywords.iterator(); iterator.hasNext();)
    {
      KeywordDefinition keyword = (KeywordDefinition)iterator.next();
      if (!packagesNames.contains(keyword.getPackageName())) {
        iterator.remove();
      }
    }
  }
  
  protected void filterKeywordsByPackages(List<KeywordDefinition> keywords, Set<AbstractAPI> apis)
  {
    Set<String> packagesNames = new HashSet<String>();
    for (AbstractAPI api : apis) {
      packagesNames.add(api.getPackageName());
    }
    filterKeywordsByPackagesNames(keywords, packagesNames);
  }
}