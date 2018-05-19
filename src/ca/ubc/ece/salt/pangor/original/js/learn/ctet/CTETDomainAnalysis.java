package ca.ubc.ece.salt.pangor.original.js.learn.ctet;

import ca.ubc.ece.salt.pangor.original.analysis.DomainAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.js.analysis.FunctionAnalysis;
import ca.ubc.ece.salt.pangor.original.js.cfg.JavaScriptCFGFactory;
import java.util.LinkedList;
import java.util.List;

public class CTETDomainAnalysis
  extends DomainAnalysis
{
  private CTETDomainAnalysis(SourceCodeFileAnalysis srcSCFA, SourceCodeFileAnalysis dstSCFA)
  {
    super(srcSCFA, dstSCFA, new JavaScriptCFGFactory(), false);
  }
  
  public static CTETDomainAnalysis createLearningAnalysis()
  {
    List<FunctionAnalysis> srcFunctionAnalyses = new LinkedList<FunctionAnalysis>();
    List<FunctionAnalysis> dstFunctionAnalyses = new LinkedList<FunctionAnalysis>();
    
    srcFunctionAnalyses.add(new CTETFunctionAnalysis(false));
    dstFunctionAnalyses.add(new CTETFunctionAnalysis(true));
    
    SourceCodeFileAnalysis srcSCFA = new CTETScriptAnalysis();
    SourceCodeFileAnalysis dstSCFA = new CTETScriptAnalysis();
    
    CTETDomainAnalysis analysis = new CTETDomainAnalysis(srcSCFA, dstSCFA);
    
    return analysis;
  }
}
