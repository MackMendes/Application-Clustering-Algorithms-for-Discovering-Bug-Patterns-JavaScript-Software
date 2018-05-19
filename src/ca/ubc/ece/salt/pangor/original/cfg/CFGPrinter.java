/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode$ChangeType
 */
package ca.ubc.ece.salt.pangor.original.cfg;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class CFGPrinter
{
  public static String print(Output type, CFG cfg)
  {
    switch (type)
    {
    case ADJACENCY_LIST: 
      return adjacencyList(cfg);
    case DOT: 
      return graphViz(cfg);
    case DOT_TEST: 
      return graphVizTest(cfg);
	default:
		return null;
    }
  }
  
  public static enum Output
  {
    ADJACENCY_LIST,  DOT,  DOT_TEST,  NONE;
    
    private Output() {}
  }
  
  public static String graphVizTest(CFG cfg)
  {
    String graph = graphViz(cfg).replace("\\", "\\\\").replace("\n", " ").replace("\t", "").replace("\"", "\\\"");
    return graph;
  }
  
  public static String graphViz(CFG cfg)
  {
    Queue<CFGNode> queue = new LinkedList<CFGNode>();
    Set<CFGNode> visited = new HashSet<CFGNode>();
    queue.add(cfg.getEntryNode());
    visited.add(cfg.getEntryNode());
    String serial = "digraph control_flow_graph {\n";
    serial = serial + "node [ style = filled fillcolor = \"white\" ];\n";
    CFGNode current;
    for (;;)
    {
      current = (CFGNode)queue.poll();
      if (current == null) {
        break;
      }
      String label = current.getStatement().getCFGLabel();
      if (label.equals(";")) {
        label = "";
      }
      serial = serial + "\t" + current.getId() + " [ fillcolor = \"" + getFillColor(current.getStatement().getChangeType()) + "\" label = \"" + label + "\" ];\n";
      for (CFGEdge edge : current.getEdges())
      {
        serial = serial + "\t" + current.getId() + " -> " + edge.getTo().getId();
        if (edge.getCondition() != null) {
          serial = serial + " [ color = \"" + getFillColor(edge.changeType) + "\" fontcolor = \"" + getFillColor(edge.getCondition().getChangeType()) + "\" label = \"" + edge.getCondition().getCFGLabel() + "\" ];\n";
        } else {
          serial = serial + " [ color = \"" + getFillColor(edge.changeType) + "\" ];\n";
        }
        if (!visited.contains(edge.getTo()))
        {
          queue.add(edge.getTo());
          visited.add(edge.getTo());
        }
      }
    }
    serial = serial + "}";
    
    return serial;
  }
  
  private static String getFillColor(ClassifiedASTNode.ChangeType changeType)
  {
    switch (changeType)
    {
    case INSERTED: 
      return "green";
    case REMOVED: 
      return "red";
    case MOVED: 
      return "yellow";
    case UPDATED: 
      return "blue";
    case UNCHANGED: 
      return "grey";
	default:
		return "black";
    }
    
  }
  
  public static String adjacencyList(CFG cfg)
  {
    Queue<CFGNode> queue = new LinkedList<CFGNode>();
    Set<CFGNode> visited = new HashSet<CFGNode>();
    queue.add(cfg.getEntryNode());
    visited.add(cfg.getEntryNode());
    String serial = "";
    for (;;)
    {
      CFGNode current = (CFGNode)queue.poll();
      if (current == null) {
        break;
      }
      serial = serial + current.getName() + "(" + current.getId() + "){";
      for (CFGEdge edge : current.getEdges())
      {
        if (edge.getCondition() != null) {
          serial = serial + edge.getCondition().getCFGLabel() + ":" + edge.getTo().getId() + ",";
        } else {
          serial = serial + edge.getTo().getId() + ",";
        }
        if (!visited.contains(edge.getTo()))
        {
          queue.add(edge.getTo());
          visited.add(edge.getTo());
        }
      }
      if (serial.charAt(serial.length() - 1) == ',') {
        serial = serial.substring(0, serial.length() - 1);
      }
      serial = serial + "}";
      if (queue.peek() != null) {
        serial = serial + ",";
      }
    }
    return serial;
  }
}

