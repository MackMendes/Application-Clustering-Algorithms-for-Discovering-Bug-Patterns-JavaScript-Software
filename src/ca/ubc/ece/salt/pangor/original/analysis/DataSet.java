package ca.ubc.ece.salt.pangor.original.analysis;

import java.util.List;
import java.util.Map;
import org.deri.iris.Configuration;
import org.deri.iris.KnowledgeBase;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.storage.IRelation;

public abstract class DataSet
{
  protected List<IRule> rules;
  protected List<IQuery> queries;
  
  public DataSet(List<IRule> rules, List<IQuery> queries)
  {
    this.rules = rules;
    this.queries = queries;
  }
  
  public void addCommitAnalysisResults(Commit commit, Map<IPredicate, IRelation> facts)
    throws Exception
  {
    IKnowledgeBase knowledgeBase = new KnowledgeBase(facts, this.rules, new Configuration());
    
    registerAlerts(commit, knowledgeBase);
  }
  
  protected abstract void registerAlerts(Commit paramCommit, IKnowledgeBase paramIKnowledgeBase)
    throws Exception;
}