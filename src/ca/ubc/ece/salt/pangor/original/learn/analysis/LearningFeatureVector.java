package ca.ubc.ece.salt.pangor.original.learn.analysis;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.Commit;
import ca.ubc.ece.salt.pangor.original.analysis.FeatureVector;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class LearningFeatureVector
  extends FeatureVector
{
  public String cluster;
  public String klass;
  public String method;
  public int modifiedStatementCount;
  public Map<KeywordUse, Integer> keywordMap;
  
  public LearningFeatureVector(Commit commit, String klass, String method)
  {
    super(commit);
    this.klass = klass;
    this.method = method;
    this.modifiedStatementCount = 0;
    this.keywordMap = new HashMap<KeywordUse, Integer>();
    this.cluster = "?";
  }
  
  public LearningFeatureVector(Commit commit, String klass, String method, int modifiedStatementCount, int id)
  {
    super(commit, id);
    this.klass = klass;
    this.method = method;
    this.modifiedStatementCount = modifiedStatementCount;
    this.keywordMap = new HashMap<KeywordUse, Integer>();
    this.cluster = "?";
  }
  
  public String serialize()
  {
    String serialized = this.id + "," + this.commit.projectID + "," + this.commit.commitMessageType.toString() + "," + this.commit.url + "/commit/" + this.commit.repairedCommitID + "," + this.commit.buggyCommitID + "," + this.commit.repairedCommitID + "," + this.klass + "," + this.method + "," + this.modifiedStatementCount;
    for (Map.Entry<KeywordUse, Integer> entry : this.keywordMap.entrySet()) {
      serialized = serialized + "," + ((KeywordUse)entry.getKey()).toString() + ":" + entry.getValue();
    }
    return serialized;
  }
  
  public String serializeWithCluster()
  {
    String serialized = this.id + "," + this.commit.projectID + "," + this.commit.commitMessageType.toString() + "," + this.commit.url + "/commit/" + this.commit.repairedCommitID + "," + this.commit.buggyCommitID + "," + this.commit.repairedCommitID + "," + this.klass + "," + this.method + "," + this.modifiedStatementCount + "," + this.cluster;
    for (Map.Entry<KeywordUse, Integer> entry : this.keywordMap.entrySet()) {
      serialized = serialized + "," + ((KeywordUse)entry.getKey()).toString() + ":" + entry.getValue();
    }
    return serialized;
  }
  
  public void addKeyword(KeywordUse keyword)
  {
    Integer count = Integer.valueOf(this.keywordMap.containsKey(keyword) ? ((Integer)this.keywordMap.get(keyword)).intValue() + 1 : 1);
    this.keywordMap.put(keyword, count);
  }
  
  public void addKeyword(KeywordUse keyword, Integer count)
  {
    this.keywordMap.put(keyword, count);
  }
  
  public static LearningFeatureVector deSerialize(String serialized)
    throws Exception
  {
    String[] features = serialized.split(",");
    if (features.length < 8) {
      throw new Exception("De-serialization exception. Serial format not recognized.");
    }
    Commit commit = new Commit(features[1], features[3], features[4], features[5], Commit.Type.valueOf(features[2]));
    
    LearningFeatureVector featureVector = new LearningFeatureVector(commit, features[6], features[7], Integer.parseInt(features[8]), Integer.parseInt(features[0]));
    for (int i = 9; i < features.length; i++)
    {
      String[] feature = features[i].split(":");
      if (feature.length < 6) {
        throw new Exception("De-serialization exception. Serial format not recognized.");
      }
      KeywordUse keyword = new KeywordUse(KeywordDefinition.KeywordType.valueOf(feature[0]), KeywordUse.KeywordContext.valueOf(feature[1]), feature[4], ClassifiedASTNode.ChangeType.valueOf(feature[2]), feature[3]);
      
      featureVector.addKeyword(keyword, Integer.valueOf(Integer.parseInt(feature[5])));
    }
    return featureVector;
  }
  
  public Instance getWekaInstance(Instances dataSet, ArrayList<Attribute> attributes, Set<KeywordDefinition> keywords, double complexityWeight)
  {
    Instance instance = new DenseInstance(attributes.size());
    instance.setDataset(dataSet);
    
    instance.setValue(0, this.id);
    instance.setValue(1, this.commit.commitMessageType.toString());
    instance.setValue(2, this.commit.projectID);
    instance.setValue(3, this.commit.url);
    instance.setValue(4, this.commit.buggyCommitID);
    instance.setValue(5, this.commit.repairedCommitID);
    instance.setValue(6, this.klass);
    instance.setValue(7, this.method);
    instance.setValue(8, this.cluster);
    instance.setValue(9, this.modifiedStatementCount * complexityWeight);
    
    int i = 10;
    for (KeywordDefinition keyword : keywords)
    {
      if (this.keywordMap.containsKey(keyword)) {
        instance.setValue(i, ((Integer)this.keywordMap.get(keyword)).intValue());
      } else {
        instance.setValue(i, 0.0D);
      }
      i++;
    }
    return instance;
  }
  
  public String getFeatureVector(Set<KeywordDefinition> keywords)
  {
    String vector = this.id + "," + this.commit.projectID + "," + this.commit.url + "," + this.commit.commitMessageType + "," + this.commit.buggyCommitID + "," + this.commit.repairedCommitID + "," + this.klass + "," + this.method + this.modifiedStatementCount;
    for (KeywordDefinition keyword : keywords) {
      if (this.keywordMap.containsKey(keyword)) {
        vector = vector + "," + ((Integer)this.keywordMap.get(keyword)).toString();
      } else {
        vector = vector + ",0";
      }
    }
    return vector;
  }
  
  public String toString()
  {
    return serialize();
  }
  
  public boolean equals(Object o)
  {
    if ((o instanceof LearningFeatureVector))
    {
      LearningFeatureVector sa = (LearningFeatureVector)o;
      if ((this.commit.equals(sa.commit)) && 
        (this.klass.equals(sa.klass)) && 
        (this.method.equals(sa.method))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return (this.commit.projectID + this.commit.repairedCommitID + this.klass + this.method).hashCode();
  }
}
