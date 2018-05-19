package ca.ubc.ece.salt.pangor.original.pointsto;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.api.AbstractAPI;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.api.TopLevelAPI;
import java.util.Map;
import java.util.Set;

public class PointsToPrediction
{
  protected final double LIKELIHOOD_THRESHOLD = 0.0D;
  protected Predictor predictor;
  
  public PointsToPrediction(TopLevelAPI api, Map<KeywordUse, Integer> keywords)
  {
    this.predictor = new CSPredictor(api, keywords);
  }
  
  public KeywordUse getKeyword(KeywordDefinition.KeywordType type, KeywordUse.KeywordContext context, String token, ClassifiedASTNode.ChangeType changeType)
  {
    KeywordUse keyword = new KeywordUse(type, context, token, changeType);
    
    PredictionResults results = this.predictor.predictKeyword(keyword);
    PredictionResult result = (PredictionResult)results.poll();
    if ((result != null) && (result.likelihood > 0.0D))
    {
      keyword.api = result.api;
      return keyword;
    }
    return null;
  }
  
  public KeywordUse getKeyword(KeywordDefinition.KeywordType type, String token)
  {
    return getKeyword(type, KeywordUse.KeywordContext.UNKNOWN, token, ClassifiedASTNode.ChangeType.UNKNOWN);
  }
  
  @SuppressWarnings("unchecked")
public Set<AbstractAPI> getAPIsUsed(Map<KeywordUse, Integer> insertedKeywords, Map<KeywordUse, Integer> removedKeywords, Map<KeywordUse, Integer> updatedKeywords, Map<KeywordUse, Integer> unchangedKeywords)
  {
    return this.predictor.predictKeywords(new Map[] { insertedKeywords, unchangedKeywords });
  }
  
  @SuppressWarnings("unchecked")
public Set<AbstractAPI> getAPIsUsed(Map<KeywordUse, Integer> keywords)
  {
    return this.predictor.predictKeywords(new Map[] { KeywordUse.filterMapByChangeType(keywords, new ClassifiedASTNode.ChangeType[] { ClassifiedASTNode.ChangeType.INSERTED, ClassifiedASTNode.ChangeType.UNCHANGED }) });
  }
  
  @SuppressWarnings("unchecked")
public Set<AbstractAPI> getAPIsInRepair(Map<KeywordUse, Integer> insertedKeywords, Map<KeywordUse, Integer> removedKeywords, Map<KeywordUse, Integer> updatedKeywords, Map<KeywordUse, Integer> unchangedKeywords)
  {
    return this.predictor.predictKeywords(new Map[] { updatedKeywords, removedKeywords });
  }
  
  @SuppressWarnings("unchecked")
public Set<AbstractAPI> getAPIsInRepair(Map<KeywordUse, Integer> keywords)
  {
    return this.predictor.predictKeywords(new Map[] { KeywordUse.filterMapByChangeType(keywords, new ClassifiedASTNode.ChangeType[] { ClassifiedASTNode.ChangeType.UPDATED, ClassifiedASTNode.ChangeType.REMOVED }) });
  }
}
