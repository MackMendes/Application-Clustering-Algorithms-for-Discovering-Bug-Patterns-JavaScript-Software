package ca.ubc.ece.salt.pangor.original.cfd;

import ca.ubc.ece.salt.gumtree.ast.ASTClassifier;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import ca.ubc.ece.salt.pangor.original.cfg.CFGFactory;
import ca.ubc.ece.salt.pangor.original.cfg.diff.CFGDifferencing;
import fr.labri.gumtree.actions.RootAndLeavesClassifier;
import fr.labri.gumtree.actions.TreeClassifier;
import fr.labri.gumtree.client.DiffOptions;
import fr.labri.gumtree.io.TreeGenerator;
import fr.labri.gumtree.matchers.MappingStore;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class ControlFlowDifferencing
{
  private CFDContext context;
  
  public ControlFlowDifferencing(CFGFactory cfgFactory, String[] args)
    throws Exception
  {
    this(cfgFactory, args, null, null);
  }
  
  public ControlFlowDifferencing(CFGFactory cfgFactory, String[] args, String srcSourceCode, String dstSourceCode)
    throws Exception
  {
    DiffOptions options = getAnalysisOptions(args);
    
    this.context = setup(cfgFactory, options, srcSourceCode, dstSourceCode);
  }
  
  public CFDContext getContext()
  {
    return this.context;
  }
  
  public static CFDContext setup(CFGFactory cfgFactory, String[] args)
    throws Exception
  {
    DiffOptions options = getAnalysisOptions(args);
    
    return setup(cfgFactory, options);
  }
  
  public static CFDContext setup(CFGFactory cfgFactory, DiffOptions options)
    throws Exception
  {
    return setup(cfgFactory, options, null, null);
  }
  
  public static CFDContext setup(CFGFactory cfgFactory, DiffOptions options, String srcSourceCode, String dstSourceCode)
    throws Exception
  {
    Tree src = null;
    Tree dst = null;
    if (srcSourceCode == null) {
      src = createGumTree(cfgFactory, options.getSrc(), options.getPreProcess());
    } else {
      src = createGumTree(cfgFactory, srcSourceCode, options.getSrc(), options.getPreProcess());
    }
    if (dstSourceCode == null) {
      dst = createGumTree(cfgFactory, options.getDst(), options.getPreProcess());
    } else {
      dst = createGumTree(cfgFactory, dstSourceCode, options.getDst(), options.getPreProcess());
    }
    fr.labri.gumtree.matchers.Matcher matcher = matchTreeNodes(src, dst);
    
    classifyTreeNodes(src, dst, matcher);
    
    List<CFG> srcCFGs = cfgFactory.createCFGs(src.getClassifiedASTNode());
    List<CFG> dstCFGs = cfgFactory.createCFGs(dst.getClassifiedASTNode());
    
    computeCFGChanges(srcCFGs, dstCFGs);
    
    ClassifiedASTNode srcRoot = src.getClassifiedASTNode();
    ClassifiedASTNode dstRoot = dst.getClassifiedASTNode();
    return new CFDContext(srcRoot, dstRoot, srcCFGs, dstCFGs);
  }
  
  public static DiffOptions getAnalysisOptions(String[] args)
    throws CmdLineException
  {
    DiffOptions options = new DiffOptions();
    CmdLineParser parser = new CmdLineParser(options);
    try
    {
      parser.parseArgument(args);
    }
    catch (CmdLineException e)
    {
      throw new CmdLineException(parser, "Usage:\ncfdiff /path/to/src /path/to/dst");
    }
    return options;
  }
  
  public static Tree createGumTree(CFGFactory cfgFactory, String path, boolean preProcess)
    throws IOException
  {
    Tree tree = null;
    
    String extension = getSourceCodeFileExtension(path);
    if (extension != null)
    {
      TreeGenerator treeGenerator = cfgFactory.getTreeGenerator(extension);
      tree = treeGenerator.fromFile(path, preProcess);
    }
    return tree;
  }
  
  public static Tree createGumTree(CFGFactory cfgFactory, String source, String path, boolean preProcess)
    throws IOException
  {
    Tree tree = null;
    
    String extension = getSourceCodeFileExtension(path);
    if (extension != null)
    {
      TreeGenerator treeGenerator = cfgFactory.getTreeGenerator(extension);
      tree = treeGenerator.fromSource(source, path, preProcess);
    }
    return tree;
  }
  
  public static fr.labri.gumtree.matchers.Matcher matchTreeNodes(Tree src, Tree dst)
  {
    fr.labri.gumtree.matchers.Matcher matcher = MatcherFactories.newMatcher(src, dst);
    matcher.match();
    return matcher;
  }
  
  public static void classifyTreeNodes(Tree src, Tree dst, fr.labri.gumtree.matchers.Matcher matcher)
    throws InvalidClassException
  {
    TreeClassifier classifier = new RootAndLeavesClassifier(src, dst, matcher);
    
    MappingStore mappings = matcher.getMappings();
    
    ASTClassifier astClassifier = new ASTClassifier(src, dst, classifier, mappings);
    astClassifier.classifyASTNodes();
  }
  
  public static void computeCFGChanges(List<CFG> srcCFGs, List<CFG> dstCFGs)
  {
    Map<ClassifiedASTNode, CFG> dstEntryMap = new HashMap<ClassifiedASTNode, CFG>();
    Map<ClassifiedASTNode, CFG> srcEntryMap = new HashMap<ClassifiedASTNode, CFG>();
    for (CFG dstCFG : dstCFGs) {
      dstEntryMap.put(dstCFG.getEntryNode().getStatement(), dstCFG);
    }
    for (CFG srcCFG : srcCFGs) {
      srcEntryMap.put(srcCFG.getEntryNode().getStatement(), srcCFG);
    }
    for (CFG dstCFG : dstCFGs) {
      if (srcEntryMap.containsKey(dstCFG.getEntryNode().getStatement().getMapping()))
      {
        CFG srcCFG = (CFG)srcEntryMap.get(dstCFG.getEntryNode().getStatement().getMapping());
        CFGDifferencing.computeEdgeChanges(srcCFG, dstCFG);
      }
    }
  }
  
  private static String getSourceCodeFileExtension(String path)
  {
    Pattern pattern = Pattern.compile("\\.[a-z]+$");
    java.util.regex.Matcher preMatcher = pattern.matcher(path);
    
    String preExtension = null;
    if (preMatcher.find())
    {
      preExtension = preMatcher.group();
      return preExtension.substring(1);
    }
    return null;
  }
}
