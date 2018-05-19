package ca.ubc.ece.salt.pangor.original.js.learn.statements;

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
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.EmptyStatement;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.LabeledStatement;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;

public class StatementAnalysisVisitor
  implements NodeVisitor
{
  private static Integer uniqueID = Integer.valueOf(0);
  private boolean dst;
  private Map<IPredicate, IRelation> facts;
  
  public static void getLearningFacts(Map<IPredicate, IRelation> facts, SourceCodeFileChange scfc, ScriptNode function, PointsToPrediction packageModel, boolean dst)
  {
    StatementAnalysisVisitor visitor = new StatementAnalysisVisitor(facts, scfc, AnalysisUtilities.getFunctionName(function), function, dst);
    function.visit(visitor);
  }
  
  private StatementAnalysisVisitor(Map<IPredicate, IRelation> facts, SourceCodeFileChange scfc, String functionName, ScriptNode root, boolean dst)
  {
    this.facts = facts;
    this.dst = dst;
  }
  
  public boolean visit(AstNode node)
  {
    if ((!(node instanceof BreakStatement)) && (!(node instanceof ContinueStatement)) && (!(node instanceof EmptyStatement)) && (!(node instanceof ExpressionStatement)) && (!(node instanceof IfStatement)) && (!(node instanceof LabeledStatement)) && (!(node instanceof ReturnStatement)) && (!(node instanceof SwitchStatement)) && (!(node instanceof ThrowStatement)) && (!(node instanceof TryStatement)) && (!(node instanceof WithStatement)) && (!(node instanceof ForLoop)) && (!(node instanceof ForInLoop)) && (!(node instanceof WhileLoop)) && (!(node instanceof DoLoop)) && (!(node instanceof FunctionNode)))
    {
      if ((node instanceof VariableDeclaration)) {
        if (!node.isStatement()) {}
      }
    }
    else if ((node.getChangeType() != ClassifiedASTNode.ChangeType.UNCHANGED) && 
      (node.getChangeType() != ClassifiedASTNode.ChangeType.UNKNOWN) && (
      ((node.getChangeType() == ClassifiedASTNode.ChangeType.REMOVED) && (!this.dst)) || (this.dst))) {
      addStatementFact(node);
    }
    return true;
  }
  
  private void addStatementFact(AstNode node)
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
