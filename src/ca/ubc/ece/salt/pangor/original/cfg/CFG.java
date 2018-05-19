/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.cfg;

import ca.ubc.ece.salt.pangor.original.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.original.cfg.CFGNode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class CFG {
    private CFGNode entryNode;
    private List<CFGNode> exitNodes;
    private List<CFGNode> breakNodes;
    private List<CFGNode> continueNodes;
    private List<CFGNode> throwNodes;
    private List<CFGNode> returnNodes;

    public CFG(CFGNode entryNode) {
        this.entryNode = entryNode;
        this.exitNodes = new LinkedList<CFGNode>();
        this.breakNodes = new LinkedList<CFGNode>();
        this.continueNodes = new LinkedList<CFGNode>();
        this.throwNodes = new LinkedList<CFGNode>();
        this.returnNodes = new LinkedList<CFGNode>();
    }

    public CFG copy()
    {
      CFGNode entryNodeCopy = CFGNode.copy(this.entryNode);
      
      Map<CFGNode, CFGNode> newNodes = new HashMap<CFGNode, CFGNode>();
      newNodes.put(this.entryNode, entryNodeCopy);
      
      Stack<CFGNode> stack = new Stack<CFGNode>();
      stack.push(entryNodeCopy);
      List<CFGEdge> copiedEdges;
      while (!stack.isEmpty())
      {
        CFGNode node = (CFGNode)stack.pop();
        
        copiedEdges = new LinkedList<CFGEdge>(node.getEdges());
        for (CFGEdge copiedEdge : copiedEdges)
        {
          CFGNode nodeCopy = (CFGNode)newNodes.get(copiedEdge.getTo());
          if (nodeCopy == null)
          {
            nodeCopy = CFGNode.copy(copiedEdge.getTo());
            newNodes.put(copiedEdge.getTo(), nodeCopy);
            stack.push(nodeCopy);
          }
          copiedEdge.setTo(nodeCopy);
        }
        node.setEdges(copiedEdges);
      }
      CFG cfg = new CFG(entryNodeCopy);
      for (CFGNode exitNode : this.exitNodes)
      {
        CFGNode node = (CFGNode)newNodes.get(exitNode);
        if (node != null) {
          cfg.addExitNode(node);
        }
      }
      for (CFGNode node : this.breakNodes)
      {
        CFGNode copy = (CFGNode)newNodes.get(node);
        if (copy != null) {
          cfg.addExitNode(copy);
        }
      }
      for (CFGNode node : this.continueNodes)
      {
        CFGNode copy = (CFGNode)newNodes.get(node);
        if (copy != null) {
          cfg.addExitNode(copy);
        }
      }
      for (CFGNode node : this.returnNodes)
      {
        CFGNode copy = (CFGNode)newNodes.get(node);
        if (copy != null) {
          cfg.addExitNode(copy);
        }
      }
      for (CFGNode node : this.throwNodes)
      {
        CFGNode copy = (CFGNode)newNodes.get(node);
        if (copy != null) {
          cfg.addExitNode(copy);
        }
      }
      return cfg;
    }
    
    public CFGNode getEntryNode()
    {
      return this.entryNode;
    }
    
    public void addExitNode(CFGNode node)
    {
      this.exitNodes.add(node);
    }
    
    public void addAllExitNodes(List<CFGNode> nodes)
    {
      this.exitNodes.addAll(nodes);
    }
    
    public List<CFGNode> getExitNodes()
    {
      return this.exitNodes;
    }
    
    public void addBreakNode(CFGNode node)
    {
      this.breakNodes.add(node);
    }
    
    public void addAllBreakNodes(List<CFGNode> nodes)
    {
      this.breakNodes.addAll(nodes);
    }
    
    public List<CFGNode> getBreakNodes()
    {
      return this.breakNodes;
    }
    
    public void addContinueNode(CFGNode node)
    {
      this.continueNodes.add(node);
    }
    
    public void addAllContinueNodes(List<CFGNode> nodes)
    {
      this.continueNodes.addAll(nodes);
    }
    
    public List<CFGNode> getContinueNodes()
    {
      return this.continueNodes;
    }
    
    public void addThrowNode(CFGNode node)
    {
      this.throwNodes.add(node);
    }
    
    public void addAllThrowNodes(List<CFGNode> nodes)
    {
      this.throwNodes.addAll(nodes);
    }
    
    public List<CFGNode> getThrowNodes()
    {
      return this.throwNodes;
    }
    
    public void addReturnNode(CFGNode node)
    {
      this.returnNodes.add(node);
    }
    
    public void addAllReturnNodes(List<CFGNode> nodes)
    {
      this.returnNodes.addAll(nodes);
    }
    
    public List<CFGNode> getReturnNodes()
    {
      return this.returnNodes;
    }
  }
