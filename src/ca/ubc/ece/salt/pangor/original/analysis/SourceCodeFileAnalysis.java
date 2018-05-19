/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 *  org.deri.iris.api.basics.IPredicate
 *  org.deri.iris.storage.IRelation
 */
package ca.ubc.ece.salt.pangor.original.analysis;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import java.util.List;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;

public abstract class SourceCodeFileAnalysis
{
  public abstract void analyze(SourceCodeFileChange paramSourceCodeFileChange, Map<IPredicate, IRelation> paramMap, ClassifiedASTNode paramClassifiedASTNode, List<CFG> paramList)
    throws Exception;
}
