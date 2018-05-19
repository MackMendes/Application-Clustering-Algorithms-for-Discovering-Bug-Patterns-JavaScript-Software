package ca.ubc.ece.salt.pangor.original.js.learn.analysis;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.AnalysisUtilities;
import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.SpecialTypeAnalysisUtilities;
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
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;

public class LearningAnalysisVisitor
  implements NodeVisitor
{
  private static Integer uniqueID = Integer.valueOf(0);
  private boolean dst;
  private Map<IPredicate, IRelation> facts;
  private ScriptNode root;
  private PointsToPrediction packageModel;
  private boolean visitFunctions;
  
  public static void getLearningFacts(Map<IPredicate, IRelation> facts, SourceCodeFileChange scfc, ScriptNode function, PointsToPrediction packageModel, boolean dst)
  {
    LearningAnalysisVisitor visitor = new LearningAnalysisVisitor(facts, scfc, AnalysisUtilities.getFunctionName(function), function, packageModel, false, dst);
    
    function.visit(visitor);
  }
  
  private LearningAnalysisVisitor(Map<IPredicate, IRelation> facts, SourceCodeFileChange scfc, String functionName, ScriptNode root, PointsToPrediction packageModel, boolean visitFunctions, boolean dst)
  {
    this.facts = facts;
    this.packageModel = packageModel;
    this.root = root;
    this.visitFunctions = visitFunctions;
    this.dst = dst;
  }
  
  public boolean visit(AstNode node)
  {
    registerKeyword(node, node.getChangeType());
    if ((!this.visitFunctions) && ((node instanceof FunctionNode)) && (node != this.root)) {
      return false;
    }
    return true;
  }
  
  private void registerKeyword(AstNode node, ClassifiedASTNode.ChangeType changeType)
  {
    String token = "";
    
    KeywordDefinition.KeywordType type = JSAPIUtilities.getTokenType(node);
    KeywordUse.KeywordContext context = JSAPIUtilities.getTokenContext(node);
    if ((type == KeywordDefinition.KeywordType.UNKNOWN) || (context == KeywordUse.KeywordContext.UNKNOWN)) {
      return;
    }
    if (changeType == ClassifiedASTNode.ChangeType.MOVED) {
      changeType = ClassifiedASTNode.ChangeType.UNCHANGED;
    }
    if (SpecialTypeAnalysisUtilities.isFalsey(node))
    {
      KeywordUse keyword = null;
      if (this.packageModel != null)
      {
        keyword = this.packageModel.getKeyword(type, context, "falsey", changeType);
      }
      else
      {
        keyword = new KeywordUse(type, context, "falsey", changeType);
        keyword.apiPackage = "global";
      }
      if (keyword != null) {
        addKeywordChangeFact(keyword);
      }
    }
    if ((node instanceof ReturnStatement))
    {
      token = "return";
    }
    else if ((node instanceof BreakStatement))
    {
      token = "break";
    }
    else if ((node instanceof ContinueStatement))
    {
      token = "continue";
    }
    else if ((node instanceof VariableDeclaration))
    {
      token = "var";
    }
    else if ((node instanceof NewExpression))
    {
      token = "new";
    }
    else if ((node instanceof TryStatement))
    {
      token = "try";
    }
    else if ((node instanceof Name))
    {
      Name name = (Name)node;
      token = name.getIdentifier();
      if (token.matches("e|err|error|exception"))
      {
        type = KeywordDefinition.KeywordType.RESERVED;
        token = "error";
      }
      else if (token.matches("cb|callb|callback"))
      {
        type = KeywordDefinition.KeywordType.RESERVED;
        token = "callback";
      }
    }
    else if ((node instanceof KeywordLiteral))
    {
      KeywordLiteral kl = (KeywordLiteral)node;
      token = kl.toSource();
    }
    else if ((node instanceof NumberLiteral))
    {
      NumberLiteral nl = (NumberLiteral)node;
      try
      {
        if (Double.parseDouble(nl.getValue()) == 0.0D) {
          token = "zero";
        }
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    else if ((node instanceof StringLiteral))
    {
      StringLiteral sl = (StringLiteral)node;
      if (sl.getValue().isEmpty()) {
        token = "blank";
      } else {
        token = sl.getValue();
      }
    }
    else if ((node instanceof UnaryExpression))
    {
      UnaryExpression ue = (UnaryExpression)node;
      switch (ue.getOperator())
      {
      case 32: 
        token = "typeof";
      }
    }
    else if ((node instanceof InfixExpression))
    {
      InfixExpression ie = (InfixExpression)node;
      if ((ie.getType() == 46) || (ie.getType() == 47))
      {
        if ((SpecialTypeAnalysisUtilities.getSpecialType(ie.getLeft()) != null) || 
          (SpecialTypeAnalysisUtilities.getSpecialType(ie.getRight()) != null)) {
          token = "typeof";
        } else {
          token = "sheq";
        }
      }
      else if ((ie.getType() == 12) || (ie.getType() == 13)) {
        if ((SpecialTypeAnalysisUtilities.getSpecialType(ie.getLeft()) != null) || 
          (SpecialTypeAnalysisUtilities.getSpecialType(ie.getRight()) != null)) {
          token = "typeof";
        } else {
          token = "eq";
        }
      }
    }
    KeywordUse keyword = null;
    if (this.packageModel != null) {
      keyword = this.packageModel.getKeyword(type, context, token, changeType);
    } else {
      keyword = new KeywordUse(type, context, token, changeType);
    }
    if (keyword != null) {
      addKeywordChangeFact(keyword);
    }
  }
  
  private void addKeywordChangeFact(KeywordUse keyword)
  {
    if ((this.dst) && (keyword.changeType != ClassifiedASTNode.ChangeType.INSERTED) && (keyword.changeType != ClassifiedASTNode.ChangeType.UPDATED)) {
      return;
    }
    if ((!this.dst) && (keyword.changeType != ClassifiedASTNode.ChangeType.REMOVED)) {
      return;
    }
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
      .createString(keyword.type.toString()), Factory.TERM
      .createString(keyword.context.toString()), Factory.TERM
      .createString(keyword.getPackageName()), Factory.TERM
      .createString(keyword.changeType.toString()), Factory.TERM
      .createString(keyword.keyword), Factory.TERM
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
