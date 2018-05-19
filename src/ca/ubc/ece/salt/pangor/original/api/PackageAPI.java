/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.api;

import ca.ubc.ece.salt.pangor.original.api.AbstractAPI;
import ca.ubc.ece.salt.pangor.original.api.ClassAPI;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import java.util.List;

public class PackageAPI
extends AbstractAPI {
    protected String includeName;

    public PackageAPI(String includeName, List<String> methodNames, List<String> fieldNames, List<String> constantNames, List<String> eventNames, List<ClassAPI> classes) {
        super(methodNames, fieldNames, constantNames, eventNames, classes);
        this.includeName = includeName;
        this.keywords.add(new KeywordDefinition(KeywordDefinition.KeywordType.PACKAGE, includeName, this));
    }

    @Override
    public String getName() {
        return this.includeName;
    }

    @Override
    public String getPackageName() {
        return this.includeName;
    }
}

