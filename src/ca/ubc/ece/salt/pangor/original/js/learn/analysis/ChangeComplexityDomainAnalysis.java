package ca.ubc.ece.salt.pangor.original.js.learn.analysis;

import ca.ubc.ece.salt.pangor.original.analysis.Commit;
import ca.ubc.ece.salt.pangor.original.analysis.DomainAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.js.cfg.JavaScriptCFGFactory;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.storage.simple.SimpleRelationFactory;

public class ChangeComplexityDomainAnalysis
  extends DomainAnalysis
{
  private ChangeComplexitySCFA srcComplexity;
  private ChangeComplexitySCFA dstComplexity;
  
  public ChangeComplexityDomainAnalysis(ChangeComplexitySCFA srcAnalysis, ChangeComplexitySCFA dstAnalysis)
  {
    super(srcAnalysis, dstAnalysis, new JavaScriptCFGFactory(), false);
    this.srcComplexity = srcAnalysis;
    this.dstComplexity = dstAnalysis;
  }
  
  public void analyze(Commit commit, Map<IPredicate, IRelation> facts)
    throws Exception
  {
    int complexity = 0;
    for (SourceCodeFileChange sourceCodeFileChange : commit.sourceCodeFileChanges)
    {
      analyzeFile(sourceCodeFileChange, facts);
      if (this.srcComplexity.getChangeComplexity() != null) {
        complexity += this.srcComplexity.getChangeComplexity().removedStatements;
      }
      if (this.dstComplexity.getChangeComplexity() != null)
      {
        complexity += this.dstComplexity.getChangeComplexity().insertedStatements;
        complexity += this.dstComplexity.getChangeComplexity().updatedStatements;
      }
      this.srcComplexity.resetComplexity();
      this.dstComplexity.resetComplexity();
    }
    addModifiedStatementCountFact(facts, complexity);
  }
  
  private void addModifiedStatementCountFact(Map<IPredicate, IRelation> facts, int complexity)
  {
    IPredicate predicate = Factory.BASIC.createPredicate("ModifiedStatementCount", 3);
    IRelation relation = (IRelation)facts.get(predicate);
    if (relation == null)
    {
      IRelationFactory relationFactory = new SimpleRelationFactory();
      relation = relationFactory.createRelation();
      facts.put(predicate, relation);
    }
    ITuple tuple = Factory.BASIC.createTuple(new ITerm[] {Factory.TERM
      .createString("ClassNA"), Factory.TERM
      .createString("MethodNA"), Factory.TERM
      .createString(String.valueOf(complexity)) });
    relation.add(tuple);
    
    facts.put(predicate, relation);
  }
  
  public static ChangeComplexityDomainAnalysis createComplexityAnalysis()
  {
    ChangeComplexitySCFA srcSCFA = new ChangeComplexitySCFA(false);
    ChangeComplexitySCFA dstSCFA = new ChangeComplexitySCFA(true);
    
    ChangeComplexityDomainAnalysis analysis = new ChangeComplexityDomainAnalysis(srcSCFA, dstSCFA);
    
    return analysis;
  }
}
