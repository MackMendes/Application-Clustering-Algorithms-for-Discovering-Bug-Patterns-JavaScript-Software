/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode$ChangeType
 */
package ca.ubc.ece.salt.pangor.original.cfg;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.cfg.CFGNode;

public class CFGEdge {
    private static long idGen = 0;
    private long id;
    private ClassifiedASTNode condition;
    private CFGNode from;
    private CFGNode to;
    public ClassifiedASTNode.ChangeType changeType;
    public boolean loopEdge;

    public CFGEdge(ClassifiedASTNode condition, CFGNode from, CFGNode to) {
        this.condition = condition;
        this.to = to;
        this.from = from;
        this.changeType = ClassifiedASTNode.ChangeType.UNKNOWN;
        this.id = CFGEdge.getUniqueId();
        this.loopEdge = false;
    }

    public CFGEdge(ClassifiedASTNode condition, CFGNode from, CFGNode to, boolean loopEdge) {
        this.condition = condition;
        this.to = to;
        this.from = from;
        this.changeType = ClassifiedASTNode.ChangeType.UNKNOWN;
        this.id = CFGEdge.getUniqueId();
        this.loopEdge = loopEdge;
    }

    public CFGEdge copy() {
        return new CFGEdge(this.condition, this.from, this.to, this.loopEdge);
    }

    public void setTo(CFGNode to) {
        this.to = to;
    }

    public CFGNode getTo() {
        return this.to;
    }

    public void setFrom(CFGNode from) {
        this.from = from;
    }

    public CFGNode getFrom() {
        return this.from;
    }

    public void setCondition(ClassifiedASTNode condition) {
        this.condition = condition;
    }

    public ClassifiedASTNode getCondition() {
        return this.condition;
    }

    public long getId() {
        return this.id;
    }

    private static synchronized long getUniqueId() {
        long id = idGen++;
        return id;
    }

    public static synchronized void resetIdGen() {
        idGen = 0;
    }

    public boolean equals(Object o) {
        if (o instanceof CFGEdge) {
            return ((CFGEdge)o).condition == this.condition;
        }
        return false;
    }

    public int hashCode() {
        assert (false);
        return 42;
    }

    public String toString() {
        return this.from.toString() + "-[" + (Object)this.condition + "]->" + this.to.toString();
    }
}

