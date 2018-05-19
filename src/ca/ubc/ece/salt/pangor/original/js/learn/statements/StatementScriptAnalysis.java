package ca.ubc.ece.salt.pangor.original.js.learn.statements;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import java.util.List;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;
import org.mozilla.javascript.ast.AstRoot;

public class StatementScriptAnalysis
  extends SourceCodeFileAnalysis
{
  public void analyze(SourceCodeFileChange sourceCodeFileChange, Map<IPredicate, IRelation> facts, ClassifiedASTNode root, List<CFG> cfgs)
    throws Exception
  {
    if (!(root instanceof AstRoot)) {
      throw new IllegalArgumentException("The AST must be parsed from Eclipse JDT.");
    }
    AstRoot script = (AstRoot)root;
    
    StatementAnalysisVisitor.getLearningFacts(facts, sourceCodeFileChange, script, null, true);
  }
}