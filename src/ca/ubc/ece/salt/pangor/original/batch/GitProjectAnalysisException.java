/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.batch;

public class GitProjectAnalysisException
extends Exception {
    private static final long serialVersionUID = 1;
    private String message;

    public GitProjectAnalysisException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

