package ca.ubc.ece.salt.pangor.original.analysis.flow;

import ca.ubc.ece.salt.pangor.original.cfg.CFGEdge;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractLatticeElement
{
  protected Map<Long, Integer> visitedEdges;
  
  public AbstractLatticeElement(Map<Long, Integer> visitedEdges)
  {
    this.visitedEdges = visitedEdges;
  }
  
  public AbstractLatticeElement()
  {
    this.visitedEdges = new HashMap<Long, Integer>();
  }
  
  public void visit(CFGEdge edge)
  {
    Integer count = (Integer)this.visitedEdges.get(Long.valueOf(edge.getId()));
    if (count == null) {
      count = Integer.valueOf(1);
    } else {
      count = Integer.valueOf(count.intValue() + 1);
    }
    this.visitedEdges.put(Long.valueOf(edge.getId()), count);
  }
  
  public Integer getVisitedCount(CFGEdge edge)
  {
    Integer count = (Integer)this.visitedEdges.get(Long.valueOf(edge.getId()));
    if (count == null) {
      return Integer.valueOf(0);
    }
    return count;
  }
}


