package ca.ubc.ece.salt.pangor.original.js.learn.nodes;

import ca.ubc.ece.salt.pangor.original.analysis.DomainAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.js.cfg.JavaScriptCFGFactory;

public class NodeDomainAnalysis
  extends DomainAnalysis
{
  private NodeDomainAnalysis(SourceCodeFileAnalysis srcSCFA, SourceCodeFileAnalysis dstSCFA)
  {
    super(srcSCFA, dstSCFA, new JavaScriptCFGFactory(), false);
  }
  
  public static NodeDomainAnalysis createLearningAnalysis()
  {
    SourceCodeFileAnalysis srcSCFA = new NodeScriptAnalysis();
    SourceCodeFileAnalysis dstSCFA = new NodeScriptAnalysis();
    
    NodeDomainAnalysis analysis = new NodeDomainAnalysis(srcSCFA, dstSCFA);
    
    return analysis;
  }
}
