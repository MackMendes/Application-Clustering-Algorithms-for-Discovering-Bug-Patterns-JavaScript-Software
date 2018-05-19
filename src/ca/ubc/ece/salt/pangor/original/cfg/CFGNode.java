/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 */
package ca.ubc.ece.salt.pangor.original.cfg;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CFGNode
{
  private static long idGen = 0L;
  private long id;
  private String name;
  private ClassifiedASTNode statement;
  private List<CFGEdge> edges;
  private CFGNode mappedNode;
  private int edgesIn;
  
  public CFGNode(ClassifiedASTNode statement)
  {
    this.edges = new LinkedList<CFGEdge>();
    this.statement = statement;
    this.id = getUniqueId();
    this.name = null;
    setMappedNode(null);
    this.edgesIn = 0;
  }
  
  public CFGNode(ClassifiedASTNode statement, String name)
  {
    this.edges = new LinkedList<CFGEdge>();
    this.statement = statement;
    this.id = getUniqueId();
    this.name = name;
    this.edgesIn = 0;
  }
  
  public void incrementEdges()
  {
    this.edgesIn += 1;
  }
  
  public boolean decrementEdges()
  {
    this.edgesIn -= 1;
    return this.edgesIn == 0;
  }
  
  public void addEdge(ClassifiedASTNode condition, CFGNode node)
  {
    CFGEdge edge = new CFGEdge(condition, this, node);
    int index = this.edges.indexOf(edge);
    if (index >= 0) {
      ((CFGEdge)this.edges.get(index)).setTo(node);
    } else {
      this.edges.add(new CFGEdge(condition, this, node));
    }
  }
  
  public void addEdge(ClassifiedASTNode condition, CFGNode node, boolean loopEdge)
  {
    CFGEdge edge = new CFGEdge(condition, this, node, loopEdge);
    int index = this.edges.indexOf(edge);
    if (index >= 0) {
      ((CFGEdge)this.edges.get(index)).setTo(node);
    } else {
      this.edges.add(new CFGEdge(condition, this, node, loopEdge));
    }
  }
  
  public void addEdge(CFGEdge edge)
  {
    int index = this.edges.indexOf(edge);
    if (index >= 0) {
      ((CFGEdge)this.edges.get(index)).setTo(edge.getTo());
    } else {
      this.edges.add(edge);
    }
  }
  
  public List<CFGEdge> getEdges()
  {
    return this.edges;
  }
  
  public void setEdges(List<CFGEdge> edges)
  {
    this.edges = edges;
  }
  
  public ClassifiedASTNode getStatement()
  {
    return this.statement;
  }
  
  public void setStatement(ClassifiedASTNode statement)
  {
    this.statement = statement;
  }
  
  public long getId()
  {
    return this.id;
  }
  
  public CFGNode getMappedNode()
  {
    return this.mappedNode;
  }
  
  public void setMappedNode(CFGNode mappedNode)
  {
    this.mappedNode = mappedNode;
  }
  
  public String getName()
  {
    if (this.name != null) {
      return this.name;
    }
    return this.statement.getASTNodeType();
  }
  
  public static CFGNode copy(CFGNode node)
  {
    CFGNode newNode = new CFGNode(node.getStatement());
    CFGEdge edge;
    for (Iterator<CFGEdge> localIterator = node.getEdges().iterator(); localIterator.hasNext(); newNode.addEdge(edge.getCondition(), edge.getTo())) {
      edge = (CFGEdge)localIterator.next();
    }
    return newNode;
  }
  
  private static synchronized long getUniqueId()
  {
    long id = idGen;
    idGen += 1L;
    return id;
  }
  
  public static synchronized void resetIdGen()
  {
    idGen = 0L;
  }
  
  public String toString()
  {
    return this.id + "_" + getName();
  }
}


