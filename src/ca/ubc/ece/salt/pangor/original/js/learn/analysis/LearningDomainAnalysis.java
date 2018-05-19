package ca.ubc.ece.salt.pangor.original.js.learn.analysis;

import ca.ubc.ece.salt.pangor.original.analysis.DomainAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.js.analysis.FunctionAnalysis;
import ca.ubc.ece.salt.pangor.original.js.analysis.ScriptAnalysis;
import ca.ubc.ece.salt.pangor.original.js.cfg.JavaScriptCFGFactory;
import java.util.LinkedList;
import java.util.List;

public class LearningDomainAnalysis
  extends DomainAnalysis
{
  private LearningDomainAnalysis(SourceCodeFileAnalysis srcSCFA, SourceCodeFileAnalysis dstSCFA)
  {
    super(srcSCFA, dstSCFA, new JavaScriptCFGFactory(), false);
  }
  
  public static LearningDomainAnalysis createLearningAnalysis()
  {
    List<FunctionAnalysis> srcFunctionAnalyses = new LinkedList<FunctionAnalysis>();
    List<FunctionAnalysis> dstFunctionAnalyses = new LinkedList<FunctionAnalysis>();
    
    srcFunctionAnalyses.add(new LearningFunctionAnalysis(false));
    dstFunctionAnalyses.add(new LearningFunctionAnalysis(true));
    
    SourceCodeFileAnalysis srcSCFA = new ScriptAnalysis(srcFunctionAnalyses);
    SourceCodeFileAnalysis dstSCFA = new ScriptAnalysis(dstFunctionAnalyses);
    
    LearningDomainAnalysis analysis = new LearningDomainAnalysis(srcSCFA, dstSCFA);
    
    return analysis;
  }
}
