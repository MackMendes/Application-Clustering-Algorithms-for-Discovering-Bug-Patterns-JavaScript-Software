package ca.ubc.ece.salt.pangor.original.js.cfg;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.original.cfg.CFGFactory;
import ca.ubc.ece.salt.pangor.original.cfg.CFGNode;
import ca.ubc.ece.salt.pangor.original.js.cfg.FunctionNodeVisitor;
import fr.labri.gumtree.gen.js.RhinoTreeGenerator;
import fr.labri.gumtree.io.TreeGenerator;
import java.util.LinkedList;
import java.util.List;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.EmptyStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;

public class JavaScriptCFGFactory
implements CFGFactory {
    public List<CFG> createCFGs(ClassifiedASTNode root) {
        if (!(root instanceof AstRoot)) {
            throw new IllegalArgumentException("The AST must be parsed from Apache Rhino.");
        }
        AstRoot script = (AstRoot)root;
        LinkedList<CFG> cfgs = new LinkedList<CFG>();
        cfgs.add(JavaScriptCFGFactory.buildScriptCFG((ScriptNode)script));
        List<FunctionNode> functions = FunctionNodeVisitor.getFunctions((AstNode)script);
        for (FunctionNode function : functions) {
            cfgs.add(JavaScriptCFGFactory.buildScriptCFG((ScriptNode)function));
        }
        return cfgs;
    }

    public TreeGenerator getTreeGenerator(String extension) {
        if (this.acceptsExtension(extension)) {
            return new RhinoTreeGenerator();
        }
        return null;
    }

    public boolean acceptsExtension(String extension) {
        return extension.equals("js");
    }

    private static CFG buildScriptCFG(ScriptNode scriptNode) {
        String name = "FUNCTION";
        if (scriptNode instanceof AstRoot) {
            name = "SCRIPT";
        }
        CFGNode scriptEntry = new CFGNode((ClassifiedASTNode)scriptNode, name + "_ENTRY");
        CFGNode scriptExit = new CFGNode((ClassifiedASTNode)new EmptyStatement(), name + "_EXIT");
        CFG cfg = new CFG(scriptEntry);
        cfg.addExitNode(scriptExit);
        CFG subGraph = JavaScriptCFGFactory.build(scriptNode);
        if (subGraph == null) {
        	CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            subGraph = new CFG((CFGNode)empty);
            subGraph.addExitNode((CFGNode)empty);
        }
        scriptEntry.addEdge(null, subGraph.getEntryNode());
        for (CFGNode exitNode : subGraph.getExitNodes()) {
            exitNode.addEdge(null, scriptExit);
        }
        for (CFGNode returnNode : subGraph.getReturnNodes()) {
            returnNode.addEdge(null, scriptExit);
        }
        return cfg;
    }

    private static CFG build(Block block) {
        return JavaScriptCFGFactory.buildBlock(block);
    }

    private static CFG build(Scope scope) {
        return JavaScriptCFGFactory.buildBlock(scope);
    }

    private static CFG build(ScriptNode script) {
        if (script instanceof AstRoot) {
            return JavaScriptCFGFactory.buildBlock(script);
        }
        return JavaScriptCFGFactory.buildSwitch(((FunctionNode)script).getBody());
    }

    private static CFG buildBlock(Iterable<Node> block) {
        CFG cfg = null;
        CFG previous = null;
        for (Node statement : block) {
            assert (statement instanceof AstNode);
            CFG subGraph = JavaScriptCFGFactory.buildSwitch((AstNode)statement);
            if (subGraph == null) continue;
            if (previous == null) {
                cfg = new CFG(subGraph.getEntryNode());
            } else {
                for (CFGNode exitNode : previous.getExitNodes()) {
                    exitNode.addEdge(null, subGraph.getEntryNode());
                }
            }
            cfg.addAllReturnNodes(subGraph.getReturnNodes());
            cfg.addAllBreakNodes(subGraph.getBreakNodes());
            cfg.addAllContinueNodes(subGraph.getContinueNodes());
            cfg.addAllThrowNodes(subGraph.getThrowNodes());
            previous = subGraph;
        }
        if (previous != null) {
            cfg.addAllExitNodes(previous.getExitNodes());
        } else assert (cfg == null);
        return cfg;
    }

    private static CFG build(IfStatement ifStatement) {
        CFGNode ifNode = new CFGNode((ClassifiedASTNode)new EmptyStatement(), "IF");
        CFG cfg = new CFG(ifNode);
        CFG trueBranch = JavaScriptCFGFactory.buildSwitch(ifStatement.getThenPart());
        if (trueBranch == null) {
            CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            trueBranch = new CFG(empty);
            trueBranch.addExitNode(empty);
        }
        ifNode.addEdge(new CFGEdge((ClassifiedASTNode)ifStatement.getCondition(), ifNode, trueBranch.getEntryNode()));
        cfg.addAllExitNodes(trueBranch.getExitNodes());
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllBreakNodes(trueBranch.getBreakNodes());
        cfg.addAllContinueNodes(trueBranch.getContinueNodes());
        cfg.addAllThrowNodes(trueBranch.getThrowNodes());
        CFG falseBranch = JavaScriptCFGFactory.buildSwitch(ifStatement.getElsePart());
        if (falseBranch == null) {
            CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            falseBranch = new CFG(empty);
            falseBranch.addExitNode(empty);
        }
        ParenthesizedExpression pe = new ParenthesizedExpression();
        pe.setExpression(ifStatement.getCondition().clone((AstNode)pe));
        UnaryExpression falseBranchCondition = new UnaryExpression(26, 0, (AstNode)pe);
        falseBranchCondition.setParent((AstNode)ifStatement);
        falseBranchCondition.setChangeType(ifStatement.getCondition().getChangeType());
        ifNode.addEdge(new CFGEdge((ClassifiedASTNode)falseBranchCondition, ifNode, falseBranch.getEntryNode()));
        cfg.addAllExitNodes(falseBranch.getExitNodes());
        cfg.addAllReturnNodes(falseBranch.getReturnNodes());
        cfg.addAllBreakNodes(falseBranch.getBreakNodes());
        cfg.addAllContinueNodes(falseBranch.getContinueNodes());
        cfg.addAllThrowNodes(falseBranch.getThrowNodes());
        return cfg;
    }

    private static CFG build(WhileLoop whileLoop) {
        CFGNode whileNode = new CFGNode((ClassifiedASTNode)new EmptyStatement(), "WHILE");
        CFG cfg = new CFG(whileNode);
        CFG trueBranch = JavaScriptCFGFactory.buildSwitch(whileLoop.getBody());
        if (trueBranch == null) {
        	CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            trueBranch = new CFG((CFGNode)empty);
            trueBranch.addExitNode((CFGNode)empty);
        }
        whileNode.addEdge(new CFGEdge((ClassifiedASTNode)whileLoop.getCondition(), whileNode, trueBranch.getEntryNode(), true));
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllThrowNodes(trueBranch.getThrowNodes());
        cfg.addAllExitNodes(trueBranch.getBreakNodes());
        for (CFGNode exitNode : trueBranch.getExitNodes()) {
            exitNode.addEdge(null, whileNode);
        }
        for (CFGNode continueNode : trueBranch.getContinueNodes()) {
            continueNode.addEdge(null, whileNode);
        }
        ParenthesizedExpression pe = new ParenthesizedExpression();
        pe.setExpression(whileLoop.getCondition().clone((AstNode)pe));
        UnaryExpression falseBranchCondition = new UnaryExpression(26, 0, (AstNode)pe);
        falseBranchCondition.setChangeType(whileLoop.getCondition().getChangeType());
        falseBranchCondition.setParent((AstNode)whileLoop);
        CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
        whileNode.addEdge(new CFGEdge((ClassifiedASTNode)falseBranchCondition, whileNode, empty));
        cfg.addExitNode(empty);
        return cfg;
    }

    private static CFG build(DoLoop doLoop) {
        CFGNode doNode = new CFGNode((ClassifiedASTNode)new EmptyStatement(), "DO");
        CFGNode whileNode = new CFGNode((ClassifiedASTNode)new EmptyStatement(), "WHILE");
        CFG cfg = new CFG(doNode);
        CFG loopBranch = JavaScriptCFGFactory.buildSwitch(doLoop.getBody());
        if (loopBranch == null) {
        	CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            loopBranch = new CFG((CFGNode)empty);
            loopBranch.addExitNode((CFGNode)empty);
        }
        doNode.addEdge(null, loopBranch.getEntryNode());
        for (CFGNode exitNode : loopBranch.getExitNodes()) {
            exitNode.addEdge(null, whileNode);
        }
        cfg.addAllReturnNodes(loopBranch.getReturnNodes());
        cfg.addAllThrowNodes(loopBranch.getThrowNodes());
        cfg.addAllExitNodes(loopBranch.getBreakNodes());
        for (CFGNode continueNode : loopBranch.getContinueNodes()) {
            continueNode.addEdge(null, whileNode);
        }
        whileNode.addEdge((ClassifiedASTNode)doLoop.getCondition(), doNode, true);
        ParenthesizedExpression pe = new ParenthesizedExpression();
        pe.setExpression(doLoop.getCondition().clone((AstNode)pe));
        UnaryExpression falseBranchCondition = new UnaryExpression(26, 0, (AstNode)pe);
        falseBranchCondition.setChangeType(doLoop.getCondition().getChangeType());
        falseBranchCondition.setParent((AstNode)doLoop);
        CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
        whileNode.addEdge((ClassifiedASTNode)falseBranchCondition, empty);
        cfg.addExitNode(empty);
        return cfg;
    }

    private static CFG build(ForLoop forLoop) {
        CFGNode forNode = new CFGNode((ClassifiedASTNode)forLoop.getInitializer());
        CFG cfg = new CFG(forNode);
        CFGNode condition = new CFGNode((ClassifiedASTNode)new EmptyStatement(), "FOR");
        forNode.addEdge(null, condition);
        CFGNode increment = new CFGNode((ClassifiedASTNode)forLoop.getIncrement());
        increment.addEdge(null, condition);
        CFG trueBranch = JavaScriptCFGFactory.buildSwitch(forLoop.getBody());
        if (trueBranch == null) {
        	CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            trueBranch = new CFG((CFGNode)empty);
            trueBranch.addExitNode((CFGNode)empty);
        }
        condition.addEdge((ClassifiedASTNode)forLoop.getCondition(), trueBranch.getEntryNode(), true);
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllThrowNodes(trueBranch.getThrowNodes());
        cfg.addAllExitNodes(trueBranch.getBreakNodes());
        for (CFGNode exitNode : trueBranch.getExitNodes()) {
            exitNode.addEdge(null, increment);
        }
        for (CFGNode continueNode : trueBranch.getContinueNodes()) {
            continueNode.addEdge(null, increment);
        }
        ParenthesizedExpression pe = new ParenthesizedExpression();
        pe.setExpression(forLoop.getCondition().clone((AstNode)pe));
        UnaryExpression falseBranchCondition = new UnaryExpression(26, 0, (AstNode)pe);
        falseBranchCondition.setChangeType(forLoop.getCondition().getChangeType());
        falseBranchCondition.setParent((AstNode)forLoop);
        CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
        condition.addEdge((ClassifiedASTNode)falseBranchCondition, empty);
        cfg.addExitNode(empty);
        return cfg;
    }

    private static CFG build(ForInLoop forInLoop) {
        AstNode iterator = forInLoop.getIterator();
        CFGNode forInNode = new CFGNode((ClassifiedASTNode)iterator);
        CFG cfg = new CFG(forInNode);
        AstNode target = iterator instanceof VariableDeclaration ? ((VariableInitializer)((VariableDeclaration)iterator).getVariables().get(0)).getTarget() : (iterator instanceof Name ? iterator : new Name(0, "~error~"));
        Name getNextKey = new Name(0, "~getNextKey");
        getNextKey.setChangeType(iterator.getChangeType());
        PropertyGet keyIteratorMethod = new PropertyGet(forInLoop.getIteratedObject(), getNextKey);
        keyIteratorMethod.setChangeType(iterator.getChangeType());
        FunctionCall keyIteratorFunction = new FunctionCall();
        keyIteratorFunction.setTarget((AstNode)keyIteratorMethod);
        keyIteratorFunction.setChangeType(iterator.getChangeType());
        Assignment targetAssignment = new Assignment(target, (AstNode)keyIteratorFunction);
        targetAssignment.setType(90);
        targetAssignment.setChangeType(target.getChangeType());
        CFGNode assignment = new CFGNode((ClassifiedASTNode)targetAssignment);
        PropertyGet keyConditionMethod = new PropertyGet(forInLoop.getIteratedObject(), new Name(0, "~hasNextKey"));
        keyConditionMethod.setChangeType(iterator.getChangeType());
        FunctionCall keyConditionFunction = new FunctionCall();
        keyConditionFunction.setTarget((AstNode)keyConditionMethod);
        keyConditionFunction.setChangeType(iterator.getChangeType());
        CFGNode condition = new CFGNode((ClassifiedASTNode)new EmptyStatement(), "FORIN");
        forInNode.addEdge(null, condition);
        condition.addEdge(new CFGEdge((ClassifiedASTNode)keyConditionFunction, condition, assignment, true));
        CFG trueBranch = JavaScriptCFGFactory.buildSwitch(forInLoop.getBody());
        if (trueBranch == null) {
        	CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            trueBranch = new CFG((CFGNode)empty);
            trueBranch.addExitNode((CFGNode)empty);
        }
        cfg.addAllReturnNodes(trueBranch.getReturnNodes());
        cfg.addAllThrowNodes(trueBranch.getThrowNodes());
        cfg.addAllExitNodes(trueBranch.getBreakNodes());
        for (CFGNode exitNode : trueBranch.getExitNodes()) {
            exitNode.addEdge(null, condition);
        }
        for (CFGNode continueNode : trueBranch.getContinueNodes()) {
            continueNode.addEdge(null, condition);
        }
        CFGNode falseBranch = new CFGNode((ClassifiedASTNode)new EmptyStatement());
        cfg.addExitNode(falseBranch);
        ParenthesizedExpression pe = new ParenthesizedExpression();
        pe.setExpression(keyConditionFunction.clone((AstNode)pe));
        UnaryExpression falseBranchCondition = new UnaryExpression(26, 0, (AstNode)pe);
        falseBranchCondition.setChangeType(keyConditionFunction.getChangeType());
        falseBranchCondition.setParent(keyConditionFunction.getParent());
        assignment.addEdge(null, trueBranch.getEntryNode());
        condition.addEdge(new CFGEdge((ClassifiedASTNode)new UnaryExpression(26, 0, (AstNode)new ParenthesizedExpression((AstNode)keyConditionFunction)), condition, falseBranch));
        return cfg;
    }


private static CFG build(SwitchStatement switchStatement)
  {
    CFGNode switchNode = new CFGNode(new EmptyStatement(), "SWITCH");
    CFG cfg = new CFG(switchNode);
    
    CFGEdge defaultEdge = null;
    AstNode defaultCondition = null;
    
    List<SwitchCase> switchCases = switchStatement.getCases();
    CFG previousSubGraph = null;
    for (SwitchCase switchCase : switchCases)
    {
      CFG subGraph = null;
      if (switchCase.getStatements() != null)
      {
        List<Node> statements = new LinkedList<Node>(switchCase.getStatements());
        subGraph = buildBlock(statements);
      }
      if (subGraph == null)
      {
        CFGNode empty = new CFGNode(new EmptyStatement());
        subGraph = new CFG(empty);
        subGraph.addExitNode(empty);
      }
      InfixExpression compare;
      if (switchCase.getExpression() != null)
      {
        compare = new InfixExpression(switchStatement.getExpression(), switchCase.getExpression());
        compare.setType(46);
        switchNode.addEdge(new CFGEdge(compare, switchNode, subGraph.getEntryNode()));
        if (defaultCondition == null)
        {
          defaultCondition = compare;
          defaultCondition.setChangeType(compare.getChangeType());
        }
        else
        {
          AstNode infix = new InfixExpression(compare, defaultCondition);
          infix.setType(104);
          if (compare.getChangeType() == defaultCondition.getChangeType()) {
            infix.setChangeType(compare.getChangeType());
          } else {
            infix.setChangeType(ClassifiedASTNode.ChangeType.UPDATED);
          }
          defaultCondition = infix;
        }
      }
      else
      {
        defaultEdge = new CFGEdge(null, switchNode, subGraph.getEntryNode());
        switchNode.addEdge(defaultEdge);
      }
      cfg.addAllReturnNodes(subGraph.getReturnNodes());
      cfg.addAllThrowNodes(subGraph.getThrowNodes());
      
      cfg.addAllContinueNodes(subGraph.getContinueNodes());
      
      cfg.addAllExitNodes(subGraph.getBreakNodes());
      if (previousSubGraph != null) {
        for (CFGNode exitNode : previousSubGraph.getExitNodes()) {
          exitNode.addEdge(null, subGraph.getEntryNode());
        }
      }
      previousSubGraph = subGraph;
    }
    if (defaultEdge == null)
    {
      CFGNode defaultPath = new CFGNode(new EmptyStatement());
      defaultEdge = new CFGEdge(null, switchNode, new CFGNode(new EmptyStatement()));
      cfg.addExitNode(defaultPath);
    }
    if (defaultCondition != null)
    {
    	UnaryExpression falseBranchCondition = new UnaryExpression(26, 0, new ParenthesizedExpression(defaultCondition));
      ((AstNode)falseBranchCondition).setChangeType(defaultCondition.getChangeType());
      defaultCondition = (AstNode)falseBranchCondition;
    }
    defaultEdge.setCondition(defaultCondition);
    
    cfg.addAllExitNodes(previousSubGraph.getExitNodes());
    
    return cfg;
  }
    private static CFG build(WithStatement withStatement) {
        FunctionCall createScopeFunction = new FunctionCall();
        createScopeFunction.setTarget((AstNode)new Name(0, "~createScope"));
        createScopeFunction.addArgument(withStatement.getExpression());
        FunctionCall destroyScopeFunction = new FunctionCall();
        destroyScopeFunction.setTarget((AstNode)new Name(0, "~destroySceop"));
        destroyScopeFunction.addArgument(withStatement.getExpression());
        CFGNode withNode = new CFGNode((ClassifiedASTNode)createScopeFunction, "BEGIN_SCOPE");
        CFGNode endWithNode = new CFGNode((ClassifiedASTNode)destroyScopeFunction, "END_SCOPE");
        CFG cfg = new CFG(withNode);
        cfg.addExitNode(endWithNode);
        CFG scopeBlock = JavaScriptCFGFactory.buildSwitch(withStatement.getStatement());
        if (scopeBlock == null) {
            CFGNode empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            scopeBlock = new CFG(empty);
            scopeBlock.addExitNode(empty);
        }
        withNode.addEdge(null, scopeBlock.getEntryNode());
        for (CFGNode exitNode : scopeBlock.getExitNodes()) {
            exitNode.addEdge(null, endWithNode);
        }
        cfg.addAllReturnNodes(scopeBlock.getReturnNodes());
        cfg.addAllThrowNodes(scopeBlock.getThrowNodes());
        cfg.addAllBreakNodes(scopeBlock.getBreakNodes());
        cfg.addAllContinueNodes(scopeBlock.getContinueNodes());
        return cfg;
    }

    private static CFG build(TryStatement tryStatement) {
        CFGNode empty;
        CFGNode tryNode = new CFGNode((ClassifiedASTNode)new EmptyStatement(), "TRY");
        CFG cfg = new CFG(tryNode);
        CFGNode exit = new CFGNode((ClassifiedASTNode)new EmptyStatement());
        cfg.addExitNode(exit);
        CFG finallyBlock = JavaScriptCFGFactory.buildSwitch(tryStatement.getFinallyBlock());
        if (finallyBlock == null) {
            CFGNode empty2 = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            finallyBlock = new CFG(empty2);
            finallyBlock.addExitNode(empty2);
        } else {
            cfg.addAllReturnNodes(finallyBlock.getReturnNodes());
            cfg.addAllBreakNodes(finallyBlock.getBreakNodes());
            cfg.addAllContinueNodes(finallyBlock.getContinueNodes());
            cfg.addAllThrowNodes(finallyBlock.getThrowNodes());
            for (CFGNode exitNode : finallyBlock.getExitNodes()) {
                exitNode.addEdge(null, exit);
            }
        }
        List<CatchClause> catchClauses = tryStatement.getCatchClauses();
        for (CatchClause catchClause : catchClauses) {
            CFGNode empty3;
            CFG catchBlock = JavaScriptCFGFactory.buildSwitch((AstNode)catchClause.getBody());
            AstNode catchCondition = catchClause.getCatchCondition();
            if (catchCondition == null) {
                FunctionCall exception = new FunctionCall();
                List<AstNode> args = new LinkedList<AstNode>();
                args.add((AstNode)catchClause.getVarName());
                exception.setArguments(args);
                exception.setTarget((AstNode)new Name(0, "~exception"));
                catchCondition = exception;
            }
            if (catchBlock == null) {
                empty3 = new CFGNode((ClassifiedASTNode)new EmptyStatement());
                catchBlock = new CFG(empty3);
                catchBlock.addExitNode(empty3);
            } else {
                empty3 = new CFGNode((ClassifiedASTNode)new EmptyStatement());
                cfg.addExitNode(empty3);
                for (CFGNode exitNode : finallyBlock.getExitNodes()) {
                    exitNode.addEdge((ClassifiedASTNode)catchCondition, empty3);
                }
                cfg.addAllBreakNodes(JavaScriptCFGFactory.moveJumpAfterFinally(finallyBlock.copy(), catchBlock.getBreakNodes(), catchCondition));
                cfg.addAllContinueNodes(JavaScriptCFGFactory.moveJumpAfterFinally(finallyBlock.copy(), catchBlock.getContinueNodes(), catchCondition));
                cfg.addAllReturnNodes(JavaScriptCFGFactory.moveJumpAfterFinally(finallyBlock.copy(), catchBlock.getReturnNodes(), catchCondition));
                cfg.addAllThrowNodes(JavaScriptCFGFactory.moveJumpAfterFinally(finallyBlock.copy(), catchBlock.getThrowNodes(), catchCondition));
                for (CFGNode exitNode : catchBlock.getExitNodes()) {
                    exitNode.addEdge(null, finallyBlock.getEntryNode());
                }
            }
            tryNode.addEdge((ClassifiedASTNode)catchCondition, catchBlock.getEntryNode());
        }
        CFG tryBlock = JavaScriptCFGFactory.buildSwitch(tryStatement.getTryBlock());
        if (tryBlock == null) {
            empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            tryBlock = new CFG(empty);
            tryBlock.addExitNode(finallyBlock.getEntryNode());
        } else {
            empty = new CFGNode((ClassifiedASTNode)new EmptyStatement());
            cfg.addExitNode(empty);
            for (CFGNode exitNode : finallyBlock.getExitNodes()) {
                exitNode.addEdge(null, empty);
            }
            cfg.addAllBreakNodes(JavaScriptCFGFactory.moveJumpAfterFinally(finallyBlock.copy(), tryBlock.getBreakNodes(), null));
            cfg.addAllContinueNodes(JavaScriptCFGFactory.moveJumpAfterFinally(finallyBlock.copy(), tryBlock.getContinueNodes(), null));
            cfg.addAllReturnNodes(JavaScriptCFGFactory.moveJumpAfterFinally(finallyBlock.copy(), tryBlock.getReturnNodes(), null));
            for (CFGNode throwNode : tryBlock.getThrowNodes()) {
                throwNode.addEdge(null, finallyBlock.getEntryNode());
            }
            for (CFGNode exitNode : tryBlock.getExitNodes()) {
                exitNode.addEdge(null, finallyBlock.getEntryNode());
            }
        }
        tryNode.addEdge(null, tryBlock.getEntryNode());
        return cfg;
    }

    private static List<CFGNode> moveJumpAfterFinally(CFG finallyBlock, List<CFGNode> jumpNodes, AstNode condition) {
        LinkedList<CFGNode> newJumpNodes = new LinkedList<CFGNode>();
        for (CFGNode jumpNode : jumpNodes) {
            CFGNode newJumpNode = CFGNode.copy((CFGNode)jumpNode);
            newJumpNodes.add(newJumpNode);
            for (CFGNode exitNode : finallyBlock.getExitNodes()) {
                exitNode.addEdge((ClassifiedASTNode)condition, newJumpNode);
            }
            jumpNode.setStatement((ClassifiedASTNode)new EmptyStatement());
            jumpNode.getEdges().clear();
            jumpNode.addEdge(null, finallyBlock.getEntryNode());
        }
        return newJumpNodes;
    }

    private static CFG build(BreakStatement breakStatement) {
        CFGNode breakNode = new CFGNode((ClassifiedASTNode)breakStatement);
        CFG cfg = new CFG(breakNode);
        cfg.addBreakNode(breakNode);
        return cfg;
    }

    private static CFG build(ContinueStatement continueStatement) {
        CFGNode continueNode = new CFGNode((ClassifiedASTNode)continueStatement);
        CFG cfg = new CFG(continueNode);
        cfg.addContinueNode(continueNode);
        return cfg;
    }

    private static CFG build(ReturnStatement returnStatement) {
        CFGNode returnNode = new CFGNode((ClassifiedASTNode)returnStatement);
        CFG cfg = new CFG(returnNode);
        cfg.addReturnNode(returnNode);
        return cfg;
    }

    private static CFG build(ThrowStatement throwStatement) {
        CFGNode throwNode = new CFGNode((ClassifiedASTNode)throwStatement);
        CFG cfg = new CFG(throwNode);
        cfg.addThrowNode(throwNode);
        return cfg;
    }

    private static CFG build(AstNode statement) {
        CFGNode expressionNode = new CFGNode((ClassifiedASTNode)statement);
        CFG cfg = new CFG(expressionNode);
        cfg.addExitNode(expressionNode);
        return cfg;
    }

    private static CFG buildSwitch(AstNode node) {
        if (node == null) {
            return null;
        }
        if (node instanceof Block) {
            return JavaScriptCFGFactory.build((Block)node);
        }
        if (node instanceof IfStatement) {
            return JavaScriptCFGFactory.build((IfStatement)node);
        }
        if (node instanceof WhileLoop) {
            return JavaScriptCFGFactory.build((WhileLoop)node);
        }
        if (node instanceof DoLoop) {
            return JavaScriptCFGFactory.build((DoLoop)node);
        }
        if (node instanceof ForLoop) {
            return JavaScriptCFGFactory.build((ForLoop)node);
        }
        if (node instanceof ForInLoop) {
            return JavaScriptCFGFactory.build((ForInLoop)node);
        }
        if (node instanceof SwitchStatement) {
            return JavaScriptCFGFactory.build((SwitchStatement)node);
        }
        if (node instanceof WithStatement) {
            return JavaScriptCFGFactory.build((WithStatement)node);
        }
        if (node instanceof TryStatement) {
            return JavaScriptCFGFactory.build((TryStatement)node);
        }
        if (node instanceof BreakStatement) {
            return JavaScriptCFGFactory.build((BreakStatement)node);
        }
        if (node instanceof ContinueStatement) {
            return JavaScriptCFGFactory.build((ContinueStatement)node);
        }
        if (node instanceof ReturnStatement) {
            return JavaScriptCFGFactory.build((ReturnStatement)node);
        }
        if (node instanceof ThrowStatement) {
            return JavaScriptCFGFactory.build((ThrowStatement)node);
        }
        if (node instanceof FunctionNode) {
            return null;
        }
        if (node instanceof Scope) {
            return JavaScriptCFGFactory.build((Scope)node);
        }
        return JavaScriptCFGFactory.build(node);
    }
}

