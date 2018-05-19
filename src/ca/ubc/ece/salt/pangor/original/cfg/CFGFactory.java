/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 *  fr.labri.gumtree.io.TreeGenerator
 */
package ca.ubc.ece.salt.pangor.original.cfg;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import fr.labri.gumtree.io.TreeGenerator;
import java.util.List;

public interface CFGFactory {
	 public abstract List<CFG> createCFGs(ClassifiedASTNode paramClassifiedASTNode);
	  
	  public abstract TreeGenerator getTreeGenerator(String paramString);
	  
	  public abstract boolean acceptsExtension(String paramString);
}

