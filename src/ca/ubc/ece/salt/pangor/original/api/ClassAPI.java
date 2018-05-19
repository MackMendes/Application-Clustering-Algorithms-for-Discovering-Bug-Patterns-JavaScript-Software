/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.api;

import ca.ubc.ece.salt.pangor.original.api.AbstractAPI;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import java.util.List;

public class ClassAPI
extends AbstractAPI {
    protected String className;

    public ClassAPI(String className, List<String> methodNames, List<String> fieldNames, List<String> constantNames, List<String> eventNames, List<ClassAPI> classes) {
        super(methodNames, fieldNames, constantNames, eventNames, classes);
        this.className = className;
        this.keywords.add(new KeywordDefinition(KeywordDefinition.KeywordType.CLASS, className, this));
    }

    @Override
    public String getName() {
        return this.className;
    }
}

