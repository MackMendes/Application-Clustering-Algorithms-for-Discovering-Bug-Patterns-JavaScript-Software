package ca.ubc.ece.salt.pangor.original.js.learn.nodes;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.AnalysisUtilities;
import ca.ubc.ece.salt.pangor.original.pointsto.PointsToPrediction;
import java.util.Map;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.factory.Factory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.IRelationFactory;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;

public class NodeAnalysisVisitor
  implements NodeVisitor
{
  private static Integer uniqueID = Integer.valueOf(0);
  private boolean dst;
  private Map<IPredicate, IRelation> facts;
  
  public static void getLearningFacts(Map<IPredicate, IRelation> facts, SourceCodeFileChange scfc, ScriptNode function, PointsToPrediction packageModel, boolean dst)
  {
    NodeAnalysisVisitor visitor = new NodeAnalysisVisitor(facts, scfc, AnalysisUtilities.getFunctionName(function), function, dst);
    function.visit(visitor);
  }
  
  private NodeAnalysisVisitor(Map<IPredicate, IRelation> facts, SourceCodeFileChange scfc, String functionName, ScriptNode root, boolean dst)
  {
    this.facts = facts;
    this.dst = dst;
  }
  
  public boolean visit(AstNode node)
  {
    if ((node.getChangeType() != ClassifiedASTNode.ChangeType.UNCHANGED) && 
      (node.getChangeType() != ClassifiedASTNode.ChangeType.UNKNOWN) && 
      (node.getChangeType() != node.getParent().getChangeType())) {
      if (((node.getChangeType() == ClassifiedASTNode.ChangeType.REMOVED) && (!this.dst)) || (this.dst)) {
        addNodeFact(node);
      }
    }
    return true;
  }
  
  private void addNodeFact(AstNode node)
  {
    IPredicate predicate = Factory.BASIC.createPredicate("KeywordChange", 8);
    IRelation relation = (IRelation)this.facts.get(predicate);
    if (relation == null)
    {
      IRelationFactory relationFactory = new SimpleRelationFactory();
      relation = relationFactory.createRelation();
      this.facts.put(predicate, relation);
    }
    ITuple tuple = Factory.BASIC.createTuple(new ITerm[] {Factory.TERM
      .createString("ClassNA"), Factory.TERM
      .createString("MethodNA"), Factory.TERM
      .createString(KeywordDefinition.KeywordType.UNKNOWN.toString()), Factory.TERM
      .createString(KeywordUse.KeywordContext.UNKNOWN.toString()), Factory.TERM
      .createString("unknown"), Factory.TERM
      .createString(node.getChangeType().toString()), Factory.TERM
      .createString(node.getClass().getSimpleName()), Factory.TERM
      .createString(getUniqueID().toString()) });
    relation.add(tuple);
    
    this.facts.put(predicate, relation);
  }
  
  private static synchronized Integer getUniqueID()
  {
    Integer id = uniqueID;
    uniqueID = Integer.valueOf(uniqueID.intValue() + 1);
    return id;
  }
}
