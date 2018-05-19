/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 */
package ca.ubc.ece.salt.pangor.original.cfd;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.cfg.CFG;
import java.util.List;

public class CFDContext {
    public ClassifiedASTNode srcScript;
    public ClassifiedASTNode dstScript;
    public List<CFG> srcCFGs;
    public List<CFG> dstCFGs;

    public CFDContext(ClassifiedASTNode srcScript, ClassifiedASTNode dstScript, List<CFG> srcCFGs, List<CFG> dstCFGs) {
        this.srcScript = srcScript;
        this.dstScript = dstScript;
        this.srcCFGs = srcCFGs;
        this.dstCFGs = dstCFGs;
    }
}

