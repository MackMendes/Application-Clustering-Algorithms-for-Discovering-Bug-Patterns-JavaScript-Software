/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.deri.iris.api.basics.IPredicate
 *  org.deri.iris.api.basics.ITuple
 *  org.deri.iris.api.factory.IBasicFactory
 *  org.deri.iris.api.terms.ITerm
 *  org.deri.iris.factory.Factory
 *  org.deri.iris.storage.IRelation
 *  org.deri.iris.storage.simple.SimpleRelationFactory
 */
package ca.ubc.ece.salt.pangor.original.analysis;

import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.storage.simple.SimpleRelationFactory;

public class Utilities
{
  public static void addFact(Map<IPredicate, IRelation> facts, String predicateName, ITerm... terms)
  {
    IBasicFactory basicFactory = Factory.BASIC;
    
    IPredicate predicate = basicFactory.createPredicate(predicateName, terms.length);
    
    IRelation relation = (IRelation)facts.get(predicate);
    if (relation == null)
    {
      IRelationFactory relationFactory = new SimpleRelationFactory();
      relation = relationFactory.createRelation();
      facts.put(predicate, relation);
    }
    ITuple tuple = basicFactory.createTuple(terms);
    relation.add(tuple);
  }
}

