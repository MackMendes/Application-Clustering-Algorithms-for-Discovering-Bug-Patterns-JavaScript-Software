/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 *  fr.labri.gumtree.gen.jdt.JdtTreeGenerator
 *  fr.labri.gumtree.gen.js.RhinoTreeGenerator
 *  fr.labri.gumtree.io.TreeGenerator
 */
package ca.ubc.ece.salt.pangor.original.analysis.simple;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.cfg.CFGFactory;
import fr.labri.gumtree.gen.jdt.JdtTreeGenerator;
import fr.labri.gumtree.gen.js.RhinoTreeGenerator;
import fr.labri.gumtree.io.TreeGenerator;
import java.util.LinkedList;
import java.util.List;

public class SimpleCFGFactory
implements CFGFactory
{
public List<CFG> createCFGs(ClassifiedASTNode root)
{
  List<CFG> cfgs = new LinkedList<CFG>();
  
  return cfgs;
}

public TreeGenerator getTreeGenerator(String extension)
{
  switch (extension)
  {
  case "java": 
    return new JdtTreeGenerator();
  case "js": 
    return new RhinoTreeGenerator();
  }
  return null;
}

public boolean acceptsExtension(String extension)
{
  return (extension.equals("java")) || (extension.equals("js"));
}
}


