package ca.ubc.ece.salt.pangor.original.analysis.flow;

import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.original.js.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.original.pointsto.PointsToPrediction;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

public abstract class PathSensitiveFlowAnalysis<LE extends AbstractLatticeElement>
extends FlowAnalysis<LE> {
    @Override
    public void analyze(SourceCodeFileChange sourceCodeFileChange, Map<IPredicate, IRelation> facts, CFG cfg, Scope<AstNode> scope, PointsToPrediction model)
    {
      @SuppressWarnings("unused")
	long pathsComplete = 0L;
      long edgesVisited = 0L;
      
      Stack<PathSensitiveFlowAnalysis<LE>.PathState> stack = new Stack<PathSensitiveFlowAnalysis<LE>.PathState>();
      CFGEdge edge;
      for (Iterator<CFGEdge> localIterator = cfg.getEntryNode().getEdges().iterator(); localIterator.hasNext(); stack.add(new PathState(edge, entryValue((ScriptNode)cfg.getEntryNode().getStatement())))) {
        edge = (CFGEdge)localIterator.next();
      }
      PathState state;
      while ((!stack.isEmpty()) && (edgesVisited < 100000L))
      {
        state = stack.pop();
        edgesVisited += 1L;
        
        transfer((state).edge, (state).le, scope);
        
        transfer((state).edge.getTo(), (state).le, scope);
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
    }

    private class PathState {
        public CFGEdge edge;
        public LE le;

        public PathState(CFGEdge edge, LE le) {
            this.edge = edge;
            this.le = le;
        }
    }

}

