package ca.ubc.ece.salt.pangor.original.js.learn.ctet;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.AnalysisUtilities;
import ca.ubc.ece.salt.pangor.original.js.api.JSAPIUtilities;
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
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.WhileLoop;

public class CTETSourceAnalysisVisitor
  implements NodeVisitor
{
  private static Integer uniqueID = Integer.valueOf(0);
  private Map<IPredicate, IRelation> facts;
  
  public static void getLearningFacts(Map<IPredicate, IRelation> facts, SourceCodeFileChange scfc, ScriptNode function, PointsToPrediction packageModel, boolean dst)
  {
    CTETSourceAnalysisVisitor visitor = new CTETSourceAnalysisVisitor(facts, scfc, AnalysisUtilities.getFunctionName(function), function, dst);
    function.visit(visitor);
  }
  
  private CTETSourceAnalysisVisitor(Map<IPredicate, IRelation> facts, SourceCodeFileChange scfc, String functionName, ScriptNode root, boolean dst)
  {
    this.facts = facts;
  }
  
  public boolean visit(AstNode node)
  {
    KeywordDefinition.KeywordType type = JSAPIUtilities.getTokenType(node);
    KeywordUse.KeywordContext context = JSAPIUtilities.getTokenContext(node);
    if ((type == KeywordDefinition.KeywordType.UNKNOWN) || (context == KeywordUse.KeywordContext.UNKNOWN)) {
      return true;
    }
    checkFunctionName(node);
    checkFunctionality(node);
    checkParameter(node, type, context);
    checkElsePart(node);
    checkStatements(node);
    checkConditionExpression(node);
    
    return true;
  }
  
  private void addCTETFact(String changeType, String entityType)
  {
    IPredicate predicate = Factory.BASIC.createPredicate("CTET", 5);
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
      .createString(changeType), Factory.TERM
      .createString(entityType), Factory.TERM
      .createString(getUniqueID().toString()) });
    relation.add(tuple);
    
    this.facts.put(predicate, relation);
  }
  
  private void checkFunctionality(AstNode node)
  {
    if (((node instanceof FunctionNode)) && (node.getChangeType() == ClassifiedASTNode.ChangeType.INSERTED)) {
      addCTETFact("AdditionalFunctionality", node.getClass().getSimpleName());
    }
  }
  
  private void checkFunctionName(AstNode node)
  {
    if ((node instanceof FunctionNode))
    {
      FunctionNode functionNode = (FunctionNode)node;
      if ((functionNode.getMapping() != null) && 
        (functionNode.getName() != ((FunctionNode)functionNode.getMapping()).getName())) {
        addCTETFact("MethodRenaming", node.getClass().getSimpleName());
      }
    }
  }
  
  private void checkParameter(AstNode node, KeywordDefinition.KeywordType type, KeywordUse.KeywordContext context)
  {
    if ((type == KeywordDefinition.KeywordType.PARAMETER) && (node.getChangeType() == ClassifiedASTNode.ChangeType.INSERTED)) {
      addCTETFact("ParameterInsert", "SingleVariableDeclaration");
    }
  }
  
  private void checkElsePart(AstNode node)
  {
    if ((node instanceof IfStatement))
    {
      IfStatement ifStatement = (IfStatement)node;
      if (ifStatement.getElsePart().getChangeType() == ClassifiedASTNode.ChangeType.INSERTED) {
        addCTETFact("Else-PartInsert", ifStatement.getClass().getSimpleName());
      }
    }
  }
  
  private void checkStatements(AstNode node)
  {
    ClassifiedASTNode.ChangeType change = node.getChangeType();
    if (node.isStatement()) {
      switch (change)
      {
      case INSERTED: 
        addCTETFact("StatementInsert", node.getClass().getSimpleName());
        break;
      case UPDATED: 
        addCTETFact("StatementUpdate", node.getClass().getSimpleName());
        break;
      case MOVED: 
        if (node.getParent().getMapping() == ((AstNode)node.getMapping()).getParent()) {
          addCTETFact("StatementParentChange", node.getClass().getSimpleName());
        } else {
          addCTETFact("StatementOrderingChange", node.getClass().getSimpleName());
        }
        break;
	default:
		break;
      }
    }
  }
  
  private void checkConditionExpression(AstNode node)
  {
    if ((node instanceof IfStatement))
    {
      IfStatement ifStatement = (IfStatement)node;
      if (ifStatement.getCondition().getChangeType() != ClassifiedASTNode.ChangeType.UNCHANGED) {
        addCTETFact("ConditionExpressionChange", node.getClass().getSimpleName());
      }
    }
    else if ((node instanceof ForLoop))
    {
      ForLoop forLoop = (ForLoop)node;
      if (forLoop.getCondition().getChangeType() != ClassifiedASTNode.ChangeType.UNCHANGED) {
        addCTETFact("ConditionExpressionChange", node.getClass().getSimpleName());
      }
    }
    else if ((node instanceof WhileLoop))
    {
      WhileLoop whileLoop = (WhileLoop)node;
      if (whileLoop.getCondition().getChangeType() != ClassifiedASTNode.ChangeType.UNCHANGED) {
        addCTETFact("ConditionExpressionChange", node.getClass().getSimpleName());
      }
    }
    else if ((node instanceof DoLoop))
    {
      DoLoop doLoop = (DoLoop)node;
      if (doLoop.getCondition().getChangeType() != ClassifiedASTNode.ChangeType.UNCHANGED) {
        addCTETFact("ConditionExpressionChange", node.getClass().getSimpleName());
      }
    }
    else if ((node instanceof ConditionalExpression))
    {
      ConditionalExpression ce = (ConditionalExpression)node;
      if (ce.getTestExpression().getChangeType() != ClassifiedASTNode.ChangeType.UNCHANGED) {
        addCTETFact("ConditionExpressionChange", node.getClass().getSimpleName());
      }
    }
    else if ((node instanceof SwitchStatement))
    {
      SwitchStatement ss = (SwitchStatement)node;
      if (ss.getExpression().getChangeType() != ClassifiedASTNode.ChangeType.UNCHANGED) {
        addCTETFact("ConditionExpressionChange", node.getClass().getSimpleName());
      }
    }
  }
  
  private static synchronized Integer getUniqueID()
  {
    Integer id = uniqueID;
    uniqueID = Integer.valueOf(uniqueID.intValue() + 1);
    return id;
  }
}
