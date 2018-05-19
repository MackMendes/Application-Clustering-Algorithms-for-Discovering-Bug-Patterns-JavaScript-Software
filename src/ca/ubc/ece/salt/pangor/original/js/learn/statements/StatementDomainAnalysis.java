package ca.ubc.ece.salt.pangor.original.js.learn.statements;

import ca.ubc.ece.salt.pangor.original.analysis.DomainAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.js.cfg.JavaScriptCFGFactory;

public class StatementDomainAnalysis
  extends DomainAnalysis
{
  private StatementDomainAnalysis(SourceCodeFileAnalysis srcSCFA, SourceCodeFileAnalysis dstSCFA)
  {
    super(srcSCFA, dstSCFA, new JavaScriptCFGFactory(), false);
  }
  
  public static StatementDomainAnalysis createLearningAnalysis()
  {
    SourceCodeFileAnalysis srcSCFA = new StatementScriptAnalysis();
    SourceCodeFileAnalysis dstSCFA = new StatementScriptAnalysis();
    
    StatementDomainAnalysis analysis = new StatementDomainAnalysis(srcSCFA, dstSCFA);
    
    return analysis;
  }
}