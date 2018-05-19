package ca.ubc.ece.salt.pangor.original.cfg.diff;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.original.cfg.CFGNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class CFGDifferencing
{
  public static void computeEdgeChanges(CFG srcCFG, CFG dstCFG)
  {
    Map<ClassifiedASTNode, CFGNode> srcASTMap = new HashMap<ClassifiedASTNode, CFGNode>();
    Map<ClassifiedASTNode, CFGNode> dstASTMap = new HashMap<ClassifiedASTNode, CFGNode>();
    
    buildASTMap(srcCFG, srcASTMap);
    buildASTMap(dstCFG, dstASTMap);
    
    srcCFG.getEntryNode().setMappedNode(dstCFG.getEntryNode());
    dstCFG.getEntryNode().setMappedNode(srcCFG.getEntryNode());
    CFGNode srcExit = mapCFGNodes(srcCFG, dstASTMap);
    CFGNode dstExit = mapCFGNodes(dstCFG, srcASTMap);
    if ((srcExit != null) && (dstExit != null))
    {
      srcExit.setMappedNode(dstExit);
      dstExit.setMappedNode(srcExit);
    }
    classifyEdges(srcCFG, ClassifiedASTNode.ChangeType.REMOVED);
    classifyEdges(dstCFG, ClassifiedASTNode.ChangeType.INSERTED);
  }
  
  private static void classifyEdges(CFG cfg, ClassifiedASTNode.ChangeType changeType)
  {
    Set<CFGNode> visited = new HashSet<CFGNode>();
    Queue<CFGNode> queue = new LinkedList<CFGNode>();
    queue.add(cfg.getEntryNode());
    visited.add(cfg.getEntryNode());
    CFGNode mappedCFGNode;
    while (!queue.isEmpty())
    {
      CFGNode cfgNode = (CFGNode)queue.remove();
      Map<CFGNode, Stack<CFGEdge>> toNonEmpty;
      Set<CFGNode> mappedKeySet;
      if ((!cfgNode.getStatement().isEmpty()) || (cfgNode.getName().equals("FUNCTION_ENTRY")) || (cfgNode.getName().equals("SCRIPT_ENTRY")))
      {
        mappedCFGNode = cfgNode.getMappedNode();
        
        toNonEmpty = getPathsToNext(cfgNode, new Stack<CFGEdge>());
        if (mappedCFGNode == null)
        {
          for (CFGNode key : toNonEmpty.keySet()) {
            labelEdgesOnPath((Stack<CFGEdge>)toNonEmpty.get(key), changeType);
          }
        }
        else
        {
          Map<CFGNode, Stack<CFGEdge>>  mappedNonEmpty = getPathsToNext(mappedCFGNode, new Stack<CFGEdge>());
          mappedKeySet = (mappedNonEmpty).keySet();
          for (CFGNode key : toNonEmpty.keySet()) {
            if (mappedKeySet.contains(key.getMappedNode())) {
              labelEdgesOnPath((Stack<CFGEdge>)toNonEmpty.get(key), ClassifiedASTNode.ChangeType.UNCHANGED);
            } else if (key.getStatement().isEmpty()) {
              labelEdgesOnPath((Stack<CFGEdge>)toNonEmpty.get(key), ClassifiedASTNode.ChangeType.UNKNOWN);
            } else {
              labelEdgesOnPath((Stack<CFGEdge>)toNonEmpty.get(key), changeType);
            }
          }
        }
      }
      for (CFGEdge edge : cfgNode.getEdges()) {
        if (!visited.contains(edge.getTo()))
        {
          queue.add(edge.getTo());
          visited.add(edge.getTo());
        }
      }
    }
  }
  
  private static void labelEdgesOnPath(Stack<CFGEdge> path, ClassifiedASTNode.ChangeType changeType)
  {
    while (!path.empty())
    {
      CFGEdge edge = (CFGEdge)path.pop();
      if (edge.changeType == ClassifiedASTNode.ChangeType.UNKNOWN) {
        edge.changeType = changeType;
      } else if ((changeType == ClassifiedASTNode.ChangeType.INSERTED) || (changeType == ClassifiedASTNode.ChangeType.REMOVED)) {
        edge.changeType = changeType;
      }
    }
  }
  
  private static Map<CFGNode, Stack<CFGEdge>> getPathsToNext(CFGNode current, Stack<CFGEdge> path)
  {
    Map<CFGNode, Stack<CFGEdge>> paths = new HashMap<CFGNode, Stack<CFGEdge>>();
    for (CFGEdge edge : current.getEdges())
    {
      CFGNode to = edge.getTo();
      
      @SuppressWarnings("unchecked")
	Stack<CFGEdge> newPath = (Stack<CFGEdge>)path.clone();
      newPath.add(edge);
      if ((to.getName().equals("FUNCTION_EXIT")) || (to.getName().equals("SCRIPT_EXIT")))
      {
        paths.put(to, newPath);
      }
      else if (!to.getStatement().isEmpty())
      {
        paths.put(to, newPath);
      }
      else if (path.contains(edge))
      {
        paths.put(to, newPath);
      }
      else
      {
        Map<CFGNode, Stack<CFGEdge>> subPaths = getPathsToNext(edge.getTo(), newPath);
        paths.putAll(subPaths);
      }
    }
    return paths;
  }
  
  private static CFGNode mapCFGNodes(CFG cfg, Map<ClassifiedASTNode, CFGNode> map)
  {
    CFGNode functExitNode = null;
    Set<CFGNode> visited = new HashSet<CFGNode>();
    Queue<CFGNode> queue = new LinkedList<CFGNode>();
    queue.add(cfg.getEntryNode());
    visited.add(cfg.getEntryNode());
    CFGNode cfgMapping;
    while (!queue.isEmpty())
    {
      CFGNode cfgNode = (CFGNode)queue.remove();
      ClassifiedASTNode astNode = cfgNode.getStatement();
      if ((cfgNode.getName().equals("FUNCTION_EXIT")) || (cfgNode.getName().equals("SCRIPT_EXIT"))) {
        functExitNode = cfgNode;
      }
      ClassifiedASTNode astMapping = astNode.getMapping();
      if (astMapping != null)
      {
        cfgMapping = (CFGNode)map.get(astMapping);
        
        cfgNode.setMappedNode(cfgMapping);
      }
      for (CFGEdge edge : cfgNode.getEdges()) {
        if (!visited.contains(edge.getTo()))
        {
          queue.add(edge.getTo());
          visited.add(edge.getTo());
        }
      }
    }
    return functExitNode;
  }
  
  private static void buildASTMap(CFG cfg, Map<ClassifiedASTNode, CFGNode> map)
  {
    Set<CFGNode> visited = new HashSet<CFGNode>();
    Queue<CFGNode> queue = new LinkedList<CFGNode>();
    queue.add(cfg.getEntryNode());
    visited.add(cfg.getEntryNode());
    while (!queue.isEmpty())
    {
      CFGNode cfgNode = (CFGNode)queue.remove();
      ClassifiedASTNode astNode = cfgNode.getStatement();
      map.put(astNode, cfgNode);
      for (CFGEdge edge : cfgNode.getEdges()) {
        if (!visited.contains(edge.getTo()))
        {
          queue.add(edge.getTo());
          visited.add(edge.getTo());
        }
      }
    }
  }
}
