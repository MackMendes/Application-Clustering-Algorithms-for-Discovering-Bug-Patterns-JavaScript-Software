package ca.ubc.ece.salt.pangor.original.learn.analysis;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.Commit;
import ca.ubc.ece.salt.pangor.original.analysis.DataSet;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics;
import ca.ubc.ece.salt.pangor.original.learn.EvaluationResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBase;
import org.deri.iris.ProgramNotStratifiedException;
import org.deri.iris.RuleUnsafeException;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.ILiteral;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import weka.clusterers.DBSCAN;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveByName;

public class LearningDataSet
  extends DataSet
{
  public static int ctr = 0;
  private List<KeywordUse> columnFilters;
  private String dataSetPath;
  private String oraclePath;
  private Set<KeywordDefinition> keywords;
  private List<LearningFeatureVector> featureVectors;
  private Map<Integer, String> oracle;
  private Instances wekaData;
  private Double epsilon;
  private Double complexityWeight;
  private Integer minClusterSize;
  
  private LearningDataSet(String dataSetPath, String oraclePath, List<KeywordUse> columnFilters, double epsilon, double complexityWeight, int minClusterSize)
    throws Exception
  {
    super(null, null);
    this.columnFilters = columnFilters;
    this.keywords = new HashSet<KeywordDefinition>();
    this.featureVectors = new LinkedList<LearningFeatureVector>();
    this.dataSetPath = dataSetPath;
    this.oraclePath = oraclePath;
    this.oracle = null;
    this.wekaData = null;
    this.epsilon = Double.valueOf(epsilon);
    this.complexityWeight = Double.valueOf(complexityWeight);
    this.minClusterSize = Integer.valueOf(minClusterSize);
    
    importDataSet(dataSetPath);
    if (this.oraclePath != null)
    {
      this.oracle = new HashMap<Integer, String>();
      importOracle(oraclePath);
    }
  }
  
  private LearningDataSet(String dataSetPath, List<IRule> rules, List<IQuery> queries)
  {
    super(rules, queries);
    this.keywords = new HashSet<KeywordDefinition>();
    this.featureVectors = new LinkedList<LearningFeatureVector>();
    this.dataSetPath = dataSetPath;
    this.oraclePath = null;
    this.oracle = null;
    this.wekaData = null;
    this.epsilon = null;
    this.complexityWeight = null;
    this.minClusterSize = null;
  }
  
  private LearningDataSet(List<IRule> rules, List<IQuery> queries)
  {
    super(rules, queries);
    this.keywords = new HashSet<KeywordDefinition>();
    this.featureVectors = new LinkedList<LearningFeatureVector>();
    this.dataSetPath = null;
    this.oraclePath = null;
    this.oracle = null;
    this.wekaData = null;
    this.epsilon = null;
    this.complexityWeight = null;
    this.minClusterSize = null;
  }
  
  public static LearningDataSet createLearningDataSet(String dataSetPath, String oraclePath, List<KeywordUse> columnFilters, double epsilon, double complexityWeight, int minClusterSize)
    throws Exception
  {
    return new LearningDataSet(dataSetPath, oraclePath, columnFilters, epsilon, complexityWeight, minClusterSize);
  }
  
  public static LearningDataSet createLearningDataSet(String dataSetPath)
  {
    return new LearningDataSet(dataSetPath, new LinkedList<IRule>(), getQueries());
  }
  
  public static LearningDataSet createLearningDataSet()
  {
    return new LearningDataSet(new LinkedList<IRule>(), getQueries());
  }
  
  public int getSize()
  {
    return this.featureVectors.size();
  }
  
  private static List<IQuery> getQueries()
  {
    List<IQuery> queries = new LinkedList<IQuery>();
    queries.add(Factory.BASIC
      .createQuery(new ILiteral[] {Factory.BASIC
      .createLiteral(true, Factory.BASIC
      .createPredicate("KeywordChange", 8), Factory.BASIC
      .createTuple(new ITerm[] {Factory.TERM
      .createVariable("Class"), Factory.TERM
      .createVariable("Method"), Factory.TERM
      .createVariable("KeywordType"), Factory.TERM
      .createVariable("KeywordContext"), Factory.TERM
      .createVariable("Package"), Factory.TERM
      .createVariable("ChangeType"), Factory.TERM
      .createVariable("Keyword"), Factory.TERM
      .createVariable("ID") })) }));
    
    queries.add(Factory.BASIC
      .createQuery(new ILiteral[] {Factory.BASIC
      .createLiteral(true, Factory.BASIC
      .createPredicate("ModifiedStatementCount", 3), Factory.BASIC
      .createTuple(new ITerm[] {Factory.TERM
      .createVariable("Class"), Factory.TERM
      .createVariable("Method"), Factory.TERM
      .createVariable("Count") })) }));
    
    return queries;
  }
  
  protected void registerAlerts(Commit commit, IKnowledgeBase knowledgeBase)
    throws ProgramNotStratifiedException, RuleUnsafeException, EvaluationException
  {
    Map<String, LearningFeatureVector> featureVectors = new HashMap<String, LearningFeatureVector>();
    for (IQuery query : this.queries)
    {
      IRelation results = knowledgeBase.execute(query);
      for (int i = 0; i < results.size(); i++)
      {
        ITuple tuple = results.get(i);
        
        String key = commit.projectID + "_" + commit.repairedCommitID + "_" + tuple.get(0) + "_" + tuple.get(1);
        LearningFeatureVector featureVector = (LearningFeatureVector)featureVectors.get(key);
        if (featureVector == null)
        {
          featureVector = new LearningFeatureVector(commit, ((ITerm)tuple.get(0)).toString(), ((ITerm)tuple.get(1)).toString());
          featureVectors.put(key, featureVector);
        }
        if (query.toString().contains("KeywordChange"))
        {
          KeywordUse ku = new KeywordUse(KeywordDefinition.KeywordType.valueOf(((ITerm)tuple.get(2)).getValue().toString()), KeywordUse.KeywordContext.valueOf(((ITerm)tuple.get(3)).getValue().toString()), ((ITerm)tuple.get(6)).getValue().toString(), 
        		  ClassifiedASTNode.ChangeType.valueOf(((ITerm)tuple.get(5)).getValue().toString()), ((ITerm)tuple.get(4)).getValue().toString());
          Integer count = (Integer)featureVector.keywordMap.get(ku);
          count = Integer.valueOf(count == null ? 1 : count.intValue() + 1);
          featureVector.keywordMap.put(ku, count);
        }
        if (query.toString().contains("ModifiedStatementCount")) {
          featureVector.modifiedStatementCount += Integer.parseInt(((ITerm)tuple.get(2)).getValue().toString());
        }
      }
    }
    for (LearningFeatureVector featureVector : featureVectors.values()) {
      if (this.dataSetPath != null) {
        try
        {
          storeLearningFeatureVector(featureVector);
        }
        catch (Exception e)
        {
          System.err.println("Error while writing feature vector: " + e.getMessage());
        }
      } else {
        this.featureVectors.add(featureVector);
      }
    }
  }
  
  public void importDataSet(String dataSetPath)
    throws Exception
  {
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(dataSetPath));Throwable localThrowable3 = null;
      try
      {
        for (String serialLearningFeatureVector = reader.readLine(); serialLearningFeatureVector != null; serialLearningFeatureVector = reader.readLine())
        {
          LearningFeatureVector featureVector = LearningFeatureVector.deSerialize(serialLearningFeatureVector);
          
          this.featureVectors.add(featureVector);
        }
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      }
      finally
      {
        if (reader != null) {
          if (localThrowable3 != null) {
            try
            {
              reader.close();
            }
            catch (Throwable localThrowable2)
            {
              localThrowable3.addSuppressed(localThrowable2);
            }
          } else {
            reader.close();
          }
        }
      }
    }
    catch (Exception e)
    {
      throw e;
    }
  }
  
  public void importOracle(String oraclePath)
    throws Exception
  {
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(oraclePath));Throwable localThrowable3 = null;
      try
      {
        for (String line = reader.readLine(); line != null; line = reader.readLine())
        {
          String[] values = line.split(",");
          if (values.length != 2) {
            throw new Exception("Incorrect oracle format.");
          }
          Integer id = Integer.valueOf(Integer.parseInt(values[1]));
          String expected = values[0];
          
          this.oracle.put(id, expected);
        }
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      }
      finally
      {
        if (reader != null) {
          if (localThrowable3 != null) {
            try
            {
              reader.close();
            }
            catch (Throwable localThrowable2)
            {
              localThrowable3.addSuppressed(localThrowable2);
            }
          } else {
            reader.close();
          }
        }
      }
    }
    catch (Exception e)
    {
      throw e;
    }
  }
  
  private synchronized void storeLearningFeatureVector(LearningFeatureVector featureVector)
    throws Exception
  {
    File path = new File(this.dataSetPath);
    path.getParentFile().mkdirs();
    path.createNewFile();
    
    PrintStream stream = new PrintStream(new FileOutputStream(path, true));
    
    stream.println(featureVector.serialize());
    
    stream.close();
  }
  
  public ArrayList<Attribute> getWekaAttributes()
  {
    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    
    attributes.add(new Attribute("ID", 0));
    attributes.add(new Attribute("ProjectID", (List<String>)null, 1));
    attributes.add(new Attribute("BugFixingCommit", (List<String>)null, 2));
    attributes.add(new Attribute("CommitURL", (List<String>)null, 3));
    attributes.add(new Attribute("BuggyCommitID", (List<String>)null, 4));
    attributes.add(new Attribute("RepairedCommitID", (List<String>)null, 5));
    attributes.add(new Attribute("Class", (List<String>)null, 6));
    attributes.add(new Attribute("Method", (List<String>)null, 7));
    attributes.add(new Attribute("Cluster", (List<String>)null, 8));
    attributes.add(new Attribute("ModifiedStatementCount", 9));
    
    int i = 9;
    for (KeywordDefinition keyword : this.keywords)
    {
      attributes.add(new Attribute(keyword.toString(), i));
      i++;
    }
    return attributes;
  }
  
  public String getLearningFeatureVectorHeader()
  {
    String header = String.join(",", new CharSequence[] { "ID", "ProjectID", "CommitURL", "BuggyCommitID", "RepairedCommitID", "Class", "Method", "ModifiedStatements" });
    for (KeywordDefinition keyword : this.keywords) {
      header = header + "," + keyword.toString();
    }
    return header;
  }
  
  public String getLearningFeatureVector()
  {
    String dataSet = "";
    for (LearningFeatureVector featureVector : this.featureVectors) {
      dataSet = dataSet + featureVector.getFeatureVector(this.keywords) + "\n";
    }
    return dataSet;
  }
  
  public List<LearningFeatureVector> getLearningFeatureVectors()
  {
    return this.featureVectors;
  }
  
  public LearningMetrics getMetrics()
  {
    LearningMetrics metrics = new LearningMetrics();
    
    Map<KeywordUse, Integer> counts = new HashMap<KeywordUse, Integer>();
    for (LearningFeatureVector featureVector : this.featureVectors) {
      for (KeywordUse keyword : featureVector.keywordMap.keySet())
      {
        Integer count = (Integer)counts.get(keyword);
        count = Integer.valueOf(count == null ? 1 : count.intValue() + 1);
        counts.put(keyword, count);
      }
    }
    for (KeywordUse keyword : counts.keySet()) {
      metrics.addKeywordFrequency(keyword, ((Integer)counts.get(keyword)).intValue());
    }
    return metrics;
  }
  
  public void preProcess(IQuery query)
    throws EvaluationException
  {
    Set<Integer> toInclude = new HashSet<Integer>();
    
    Map<IPredicate, IRelation> facts = getDataSetAsFactDatabase();
    IKnowledgeBase knowledgeBase = new KnowledgeBase(facts, this.rules, new Configuration());
    IRelation relation = knowledgeBase.execute(query);
    ITuple tuple;
    for (int i = 0; i < relation.size(); i++)
    {
      tuple = relation.get(i);
      if (tuple.isEmpty()) {
        throw new EvaluationException("No id was found.");
      }
      Integer id = Integer.valueOf(Integer.parseInt(((ITerm)tuple.get(0)).getValue().toString()));
      toInclude.add(id);
    }
    List<LearningFeatureVector> newFeatureVectorList = new LinkedList<LearningFeatureVector>();
    for (LearningFeatureVector featureVector : this.featureVectors) {
      if (toInclude.contains(Integer.valueOf(featureVector.id))) {
        newFeatureVectorList.add(featureVector);
      }
    }
    this.featureVectors = newFeatureVectorList;
    for (LearningFeatureVector featureVector : this.featureVectors)
    {
      KeywordDefinition keyword;
      for (Iterator<KeywordUse> localIterator = featureVector.keywordMap.keySet().iterator(); localIterator.hasNext(); this.keywords.add(keyword)) {
        keyword = (KeywordDefinition)localIterator.next();
      }
    }
  }
  
  public Instances getWekaDataSet()
  {
    ArrayList<Attribute> attributes = getWekaAttributes();
    
    Instances dataSet = new Instances("DataSet", attributes, 0);
    dataSet.setClassIndex(-1);
    for (LearningFeatureVector featureVector : this.featureVectors) {
      dataSet.add(featureVector.getWekaInstance(dataSet, attributes, this.keywords, this.complexityWeight.doubleValue()));
    }
    return dataSet;
  }
  
  public void getWekaClusters(ClusterMetrics clusterMetrics)
    throws Exception
  {
    clusterMetrics.setEpsilon(this.epsilon.doubleValue());
    
    this.wekaData = getWekaDataSet();
    
    String[] removeByNameOptions = new String[2];
    removeByNameOptions[0] = "-E";
    removeByNameOptions[1] = ".*UNCHANGED.*";
    RemoveByName removeByName = new RemoveByName();
    removeByName.setOptions(removeByNameOptions);
    removeByName.setInputFormat(this.wekaData);
    this.wekaData = Filter.useFilter(this.wekaData, removeByName);
    
    String filter = "(.*:global:test)";
    for (KeywordUse keywordUse : this.columnFilters)
    {
      filter = filter + "|(" + keywordUse.type.toString();
      filter = filter + ":" + keywordUse.context.toString();
      filter = filter + ":" + keywordUse.getPackageName();
      filter = filter + ":" + keywordUse.keyword + ")";
    }
    String[] removeKeywordOptions = new String[2];
    removeKeywordOptions[0] = "-E";
    removeKeywordOptions[1] = filter;
    RemoveByName removeKeyword = new RemoveByName();
    
    removeKeyword.setOptions(removeKeywordOptions);
    removeKeyword.setInputFormat(this.wekaData);
    this.wekaData = Filter.useFilter(this.wekaData, removeKeyword);
    for (int i = 0; i < this.wekaData.size(); i++)
    {
      int total = 0;
      Instance instance = this.wekaData.get(i);
      for (int j = 10; j < instance.numAttributes(); j++) {
        total += (int)instance.value(j);
      }
      if (total == 0) {
        this.wekaData.remove(instance);
      }
    }
    String[] removeOptions = new String[2];
    removeOptions[0] = "-R";
    removeOptions[1] = "2-9";
    Remove remove = new Remove();
    remove.setOptions(removeOptions);
    remove.setInputFormat(this.wekaData);
    Instances filteredData = Filter.useFilter(this.wekaData, remove);
    
    ManhattanDistance distanceFunction = new ManhattanDistance();
    
    String[] distanceFunctionOptions = "-R 2-last -D".split("\\s");
    distanceFunction.setOptions(distanceFunctionOptions);
    
    DBSCAN dbScan = new DBSCAN();
    String[] dbScanClustererOptions = ("-E " + String.valueOf(this.epsilon) + " -M " + this.minClusterSize).split("\\s");
    dbScan.setOptions(dbScanClustererOptions);
    dbScan.setDistanceFunction(distanceFunction);
    dbScan.buildClusterer(filteredData);
    for (Instance instance : this.wekaData) {
      try
      {
        Integer cluster = Integer.valueOf(dbScan.clusterInstance(instance));
        instance.setValue(8, "cluster" + cluster.toString());
        
        List<String> keywords = new LinkedList<String>();
        for (int i = 10; i < instance.numAttributes(); i++) {
          if (instance.value(i) > 0.0D) {
            keywords.add(instance.attribute(i).name() + ":" + instance
              .value(i));
          }
        }
        String expected = this.oracle != null ? (String)this.oracle.get(Integer.valueOf((int)instance.value(0))) : "?";
        if (expected == null) {
          expected = "?";
        }
        clusterMetrics.addInstance(cluster.intValue(), (int)instance.value(0), instance.stringValue(2), expected, 
          (int)(instance.value(9) / this.complexityWeight.doubleValue()), keywords);
      }
      catch (Exception localException) {}
    }
  }
  
  public void writeFilteredDataSet(String outFile)
  {
    PrintStream stream = System.out;
    if (outFile != null) {
      try
      {
        File path = new File(outFile);
        path.getParentFile().mkdirs();
        
        stream = new PrintStream(new FileOutputStream(outFile));
      }
      catch (IOException e)
      {
        System.err.println(e.getMessage());
      }
    }
    stream.println(getLearningFeatureVectorHeader());
    
    stream.println(getLearningFeatureVector());
  }
  
  public void writeArffFile(String outputFolder, String filename)
  {
    File path = new File(outputFolder, filename);
    path.mkdirs();
    
    ArffSaver saver = new ArffSaver();
    saver.setInstances(this.wekaData);
    try
    {
      saver.setFile(path);
      saver.writeBatch();
    }
    catch (IOException e)
    {
      System.err.println("Not possible to write Arff file.");
      
      e.printStackTrace();
    }
  }
  
  public EvaluationResult evaluate(ClusterMetrics clusterMetrics)
  {
    if (this.oracle == null) {
      return null;
    }
    return clusterMetrics.evaluate(this.oracle);
  }
  
  public Map<IPredicate, IRelation> getDataSetAsFactDatabase()
  {
    IRelationFactory relationFactory = new SimpleRelationFactory();
    
    Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
    
    IPredicate fvPredicate = Factory.BASIC.createPredicate("FeatureVector", 8);
    IRelation fvRelation = relationFactory.createRelation();
    facts.put(fvPredicate, fvRelation);
    
    IPredicate kcPredicate = Factory.BASIC.createPredicate("KeywordChange", 7);
    IRelation kcRelation = relationFactory.createRelation();
    facts.put(kcPredicate, kcRelation);
    LearningFeatureVector featureVector;
    for (Iterator<LearningFeatureVector> localIterator1 = this.featureVectors.iterator(); localIterator1.hasNext();)
    {
      featureVector = null;
      featureVector = (LearningFeatureVector)localIterator1.next();
      
      fvRelation.add(Factory.BASIC.createTuple(new ITerm[] {Factory.TERM
        .createString(String.valueOf(featureVector.id)), Factory.TERM
        .createString(featureVector.commit.commitMessageType.toString()), Factory.TERM
        .createString(featureVector.commit.url), Factory.TERM
        .createString(featureVector.commit.buggyCommitID), Factory.TERM
        .createString(featureVector.commit.repairedCommitID), Factory.TERM
        .createString(featureVector.klass), Factory.TERM
        .createString(featureVector.method), Factory.CONCRETE
        .createInt(featureVector.modifiedStatementCount) }));
      for (Map.Entry<KeywordUse, Integer> entry : featureVector.keywordMap.entrySet())
      {
        KeywordUse keyword = (KeywordUse)entry.getKey();
        Integer count = (Integer)entry.getValue();
        kcRelation.add(Factory.BASIC.createTuple(new ITerm[] {Factory.TERM
          .createString(String.valueOf(featureVector.id)), Factory.TERM
          .createString(keyword.type.toString()), Factory.TERM
          .createString(keyword.context.toString()), Factory.TERM
          .createString(keyword.changeType.toString()), Factory.TERM
          .createString(keyword.getPackageName()), Factory.TERM
          .createString(keyword.keyword), Factory.TERM
          .createString(count.toString()) }));
      }
    }
   
    return facts;
  }
  
  public boolean contains(String function, List<Pair<KeywordUse, Integer>> keywords) {
      block0 : for (LearningFeatureVector featureVector : this.featureVectors) {
          for (Pair<KeywordUse, Integer> keyword : keywords) {
              if (((Integer)keyword.getRight() <= 0 || featureVector.keywordMap.containsKey(keyword.getLeft())) && 
            		  ((Integer)keyword.getRight() <= 0 || featureVector.keywordMap.get(keyword.getLeft()).equals(keyword.getRight()))) 
            	  continue;
              
              continue block0;
          }
          return true;
      }
      return false;
  }
}
