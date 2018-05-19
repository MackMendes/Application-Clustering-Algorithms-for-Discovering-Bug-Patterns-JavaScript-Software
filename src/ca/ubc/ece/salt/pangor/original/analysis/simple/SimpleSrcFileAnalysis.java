/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 *  org.deri.iris.api.basics.IPredicate
 *  org.deri.iris.api.basics.ITuple
 *  org.deri.iris.api.factory.IBasicFactory
 *  org.deri.iris.api.factory.ITermFactory
 *  org.deri.iris.api.terms.IStringTerm
 *  org.deri.iris.api.terms.ITerm
 *  org.deri.iris.factory.Factory
 *  org.deri.iris.storage.IRelation
 *  org.deri.iris.storage.simple.SimpleRelationFactory
 */
package ca.ubc.ece.salt.pangor.original.analysis.simple;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import java.util.List;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.storage.simple.SimpleRelationFactory;

public class SimpleSrcFileAnalysis
extends SourceCodeFileAnalysis
{
public void analyze(SourceCodeFileChange sourceCodeFileChange, Map<IPredicate, IRelation> facts, ClassifiedASTNode root, List<CFG> cfgs)
  throws Exception
{
  IBasicFactory basicFactory = Factory.BASIC;
  ITermFactory termFactory = Factory.TERM;
  
  IPredicate predicate = basicFactory.createPredicate("SourceRoot", 2);
  
  IRelation relation = (IRelation)facts.get(predicate);
  if (relation == null)
  {
    IRelationFactory relationFactory = new SimpleRelationFactory();
    relation = relationFactory.createRelation();
    facts.put(predicate, relation);
  }
  ITuple tuple = basicFactory.createTuple(new ITerm[] {termFactory
    .createString(sourceCodeFileChange.getFileName()), termFactory
    .createString(root.getASTNodeType()) });
  relation.add(tuple);
}
}