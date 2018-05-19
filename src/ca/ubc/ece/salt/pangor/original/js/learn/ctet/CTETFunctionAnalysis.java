package ca.ubc.ece.salt.pangor.original.js.learn.ctet;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.js.analysis.FunctionAnalysis;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.original.pointsto.PointsToPrediction;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

public class CTETFunctionAnalysis
  extends FunctionAnalysis
{
  private boolean dst;
  
  public CTETFunctionAnalysis(boolean dst)
  {
    this.dst = dst;
  }
  
  public void analyze(SourceCodeFileChange sourceCodeFileChange, Map<IPredicate, IRelation> facts, CFG cfg, Scope<AstNode> scope, PointsToPrediction model)
    throws Exception
  {
    if ((((AstNode)scope.getScope()).getChangeType() != ClassifiedASTNode.ChangeType.INSERTED) && 
      (((AstNode)scope.getScope()).getChangeType() != ClassifiedASTNode.ChangeType.REMOVED)) {
      CTETSourceAnalysisVisitor.getLearningFacts(facts, sourceCodeFileChange, 
        (ScriptNode)scope.getScope(), model, this.dst);
    }
  }
}
