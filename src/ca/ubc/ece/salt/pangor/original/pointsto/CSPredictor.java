package ca.ubc.ece.salt.pangor.original.pointsto;

import ca.ubc.ece.salt.pangor.original.api.AbstractAPI;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.api.TopLevelAPI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSPredictor
  extends Predictor
{
  Set<AbstractAPI> apisFound = new HashSet<AbstractAPI>();
  Map<AbstractAPI, Integer> confidenceMap = new HashMap<AbstractAPI, Integer>();
  Map<AbstractAPI, Integer> supportMap = new HashMap<AbstractAPI, Integer>();
  Map<AbstractAPI, Integer> scoreMap = new HashMap<AbstractAPI, Integer>();
  
  public CSPredictor(TopLevelAPI api, Map<KeywordUse, Integer> keywords)
  {
    super(api, keywords);
    
    calculateScore();
  }
  
  public PredictionResults predictKeyword(KeywordUse keyword)
  {
    if (!isKeywordOnInput(keyword)) {
      throw new RuntimeException("Keyword " + keyword + " was not given on input");
    }
    Set<AbstractAPI> apis = getAPIsFromKeyword(keyword);
    
    PredictionResults results = new PredictionResults();
    for (AbstractAPI api : apis) {
      results.add(new PredictionResult(api, ((Integer)this.scoreMap.get(api)).intValue()));
    }
    return results;
  }
  
  private boolean isKeywordOnInput(KeywordUse keyword)
  {
    for (KeywordUse k : this.keywords.keySet()) {
      if ((k.type == keyword.type) && (k.keyword.equals(keyword.keyword))) {
        return true;
      }
    }
    return false;
  }
  
  public Set<AbstractAPI> predictKeywords(@SuppressWarnings("unchecked") Map<KeywordUse, Integer>... keywords)
  {
    Set<AbstractAPI> allAPIs = new HashSet<AbstractAPI>();
    for (Map<KeywordUse, Integer> keywordMap : keywords) {
      for (KeywordUse keyword : keywordMap.keySet()) {
        allAPIs.addAll(getAPIsFromKeyword(keyword));
      }
    }
    return allAPIs;
  }
  
  protected void calculateScore()
  {
    for (KeywordUse keyword : this.keywords.keySet())
    {
      List<KeywordDefinition> keywordsFound = this.api.getAllKeywords(keyword);
      
      filterKeywordsByPackagesNames(keywordsFound, this.requiredPackagesNames);
      if (keywordsFound.size() == 1)
      {
        this.apisFound.add(((KeywordDefinition)keywordsFound.get(0)).api);
        addOrIncrement((KeywordDefinition)keywordsFound.get(0), this.confidenceMap);
      }
      if (keywordsFound.size() > 1) {
        for (KeywordDefinition k : keywordsFound)
        {
          this.apisFound.add(k.api);
          addOrIncrement(k, this.supportMap);
        }
      }
    }
    for (AbstractAPI api : this.apisFound)
    {
      Integer confidence = this.confidenceMap.get(api) != null ? (Integer)this.confidenceMap.get(api) : Integer.valueOf(0);
      Integer support = this.supportMap.get(api) != null ? (Integer)this.supportMap.get(api) : Integer.valueOf(0);
      
      Integer score = Integer.valueOf(scoreFormula(confidence, support));
      
      this.scoreMap.put(api, score);
    }
  }
  
  private int scoreFormula(Integer confidence, Integer support)
  {
    return confidence.intValue() * 3 + support.intValue();
  }
  
  private void addOrIncrement(KeywordDefinition keyword, Map<AbstractAPI, Integer> map)
  {
    if (map.containsKey(keyword.api)) {
      map.put(keyword.api, Integer.valueOf(((Integer)map.get(keyword.api)).intValue() + 1));
    } else {
      map.put(keyword.api, Integer.valueOf(1));
    }
  }
  
  protected Set<AbstractAPI> getAPIsFromKeyword(KeywordDefinition keyword)
  {
    List<KeywordDefinition> keywordsFound = this.api.getAllKeywords(keyword);
    Set<AbstractAPI> apis = new HashSet<AbstractAPI>();
    
    filterKeywordsByPackages(keywordsFound, this.apisFound);
    for (KeywordDefinition k : keywordsFound) {
      apis.add(k.api);
    }
    return apis;
  }
}
