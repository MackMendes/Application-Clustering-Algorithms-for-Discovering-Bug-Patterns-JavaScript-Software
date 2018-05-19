package ca.ubc.ece.salt.pangor.original.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;

public class CommitAnalysis
{
  private DataSet dataSet;
  private List<DomainAnalysis> domainAnalyses;
  
  public CommitAnalysis(DataSet dataSet, List<DomainAnalysis> domainAnalyses)
  {
    this.dataSet = dataSet;
    this.domainAnalyses = domainAnalyses;
  }
  
  public void analyze(Commit commit)
    throws Exception
  {
    Map<IPredicate, IRelation> facts = new HashMap<IPredicate, IRelation>();
    for (DomainAnalysis domainAnalysis : this.domainAnalyses) {
      domainAnalysis.analyze(commit, facts);
    }
    synthesizeAlerts(commit, facts);
  }
  
  protected void synthesizeAlerts(Commit commit, Map<IPredicate, IRelation> facts)
    throws Exception
  {
    this.dataSet.addCommitAnalysisResults(commit, facts);
  }
}

