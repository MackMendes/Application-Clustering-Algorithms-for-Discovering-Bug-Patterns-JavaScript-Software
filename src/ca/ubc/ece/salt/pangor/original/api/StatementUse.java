/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode
 *  ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode$ChangeType
 */
package ca.ubc.ece.salt.pangor.original.api;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;

public class StatementUse {
    public String type;
    public ClassifiedASTNode.ChangeType changeType;

    public StatementUse(String type, ClassifiedASTNode.ChangeType changeType) {
        this.type = type;
        this.changeType = changeType;
    }

    public String toString() {
        return "STATEMENT:" + this.type + ":" + this.changeType.name();
    }
}

