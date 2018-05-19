package ca.ubc.ece.salt.pangor.original.js.analysis;

import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.original.pointsto.PointsToPrediction;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;
import org.mozilla.javascript.ast.AstNode;

public abstract class FunctionAnalysis {
	  public abstract void analyze(SourceCodeFileChange paramSourceCodeFileChange, Map<IPredicate, IRelation> paramMap, CFG paramCFG, Scope<AstNode> paramScope, PointsToPrediction paramPointsToPrediction)
			    throws Exception;
}

