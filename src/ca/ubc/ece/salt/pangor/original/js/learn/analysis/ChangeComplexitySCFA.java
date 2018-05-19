package ca.ubc.ece.salt.pangor.original.js.learn.analysis;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import java.util.List;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;
import org.mozilla.javascript.ast.AstRoot;

public class ChangeComplexitySCFA
  extends SourceCodeFileAnalysis
{
  private ChangeComplexityVisitor.ChangeComplexity complexity;
  private boolean dst;
  
  public ChangeComplexitySCFA(boolean dst)
  {
    this.complexity = null;
    this.dst = dst;
  }
  
  public ChangeComplexityVisitor.ChangeComplexity getChangeComplexity()
  {
    return this.complexity;
  }
  
  public void resetComplexity()
  {
    this.complexity = null;
  }
  
  public void analyze(SourceCodeFileChange sourceCodeFileChange, Map<IPredicate, IRelation> facts, ClassifiedASTNode root, List<CFG> cfgs)
    throws Exception
  {
    this.complexity = ChangeComplexityVisitor.getChangeComplexity((AstRoot)root, this.dst);
  }
}
