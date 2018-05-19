package ca.ubc.ece.salt.pangor.original.analysis.flow;

import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.original.cfg.CFGNode;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.original.pointsto.PointsToPrediction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

public abstract class PathInsensitiveFlowAnalysis<LE extends AbstractLatticeElement>
  extends FlowAnalysis<LE>
{
  public void analyze(SourceCodeFileChange sourceCodeFileChange, Map<IPredicate, IRelation> facts, CFG cfg, Scope<AstNode> scope, PointsToPrediction model)
  {
    @SuppressWarnings("unused")
	long pathsComplete = 0L;
    long edgesVisited = 0L;
    
    Map<CFGNode, LE> leMap = new HashMap<CFGNode, LE>();
    
    addNodeCounters(cfg);
    
    Stack<PathInsensitiveFlowAnalysis<LE>.PathState> stack = new Stack<PathInsensitiveFlowAnalysis<LE>.PathState>();
    CFGEdge edge;
    for (Iterator<CFGEdge> localIterator1 = cfg.getEntryNode().getEdges().iterator(); localIterator1.hasNext(); stack.add(new PathState(edge, entryValue((ScriptNode)cfg.getEntryNode().getStatement())))) {
      edge = (CFGEdge)localIterator1.next();
    }
    PathState state;
    LE joined;
    while ((!stack.isEmpty()) && (edgesVisited < 100000L))
    {
      state = (PathState)stack.pop();
      edgesVisited += 1L;
      
      transfer((state).edge, (state).le, scope);
      
      joined = join((state).le, leMap.get(((PathState)state).edge.getTo()));
      
      leMap.put(((PathState)state).edge.getTo(), joined);
      if (((PathState)state).edge.getTo().decrementEdges())
      {
        transfer(((PathState)state).edge.getTo(), joined, scope);
        for (CFGEdge edge1 : (state).edge.getTo().getEdges())
        {
          if (edge1.getTo().getEdges().size() == 0) {
            pathsComplete += 1L;
          }
          if ((edge1.getCondition() == null) || (((PathState)state).le.getVisitedCount(edge1).intValue() == 0))
          {
            LE copy = copy(((PathState)state).le);
            copy.visit(edge1);
            stack.add(new PathState(edge1, copy));
          }
        }
      }
      else
      {
        for (CFGEdge edge1 : (state).edge.getTo().getEdges()) {
          if (edge1.loopEdge)
          {
            LE copy = copy(joined);
            transfer(((PathState)state).edge.getTo(), copy, scope);
            if (((PathState)state).le.getVisitedCount(edge1).intValue() == 0)
            {
              copy.visit(edge1);
              stack.add(new PathState(edge1, copy));
            }
          }
        }
      }
    }
  }
  
  protected abstract LE join(LE paramLE1, LE paramLE2);
  
  private void addNodeCounters(CFG cfg)
  {
    Set<CFGNode> visited = new HashSet<CFGNode>();
    
    Stack<CFGNode> stack = new Stack<CFGNode>();
    stack.push(cfg.getEntryNode());
    visited.add(cfg.getEntryNode());
    while (!stack.isEmpty())
    {
      CFGNode node = (CFGNode)stack.pop();
      for (CFGEdge edge : node.getEdges())
      {
        edge.getTo().incrementEdges();
        if (!visited.contains(edge.getTo()))
        {
          stack.push(edge.getTo());
          visited.add(edge.getTo());
        }
      }
    }
  }
  
  private class PathState
  {
    public CFGEdge edge;
    public LE le;
    
    public PathState(CFGEdge edge, LE le)
    {
      this.edge = edge;
      this.le = le;
    }
  }
}
