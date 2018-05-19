package ca.ubc.ece.salt.pangor.original.js.analysis.utilities;

import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.SpecialTypeAnalysisUtilities;

public class SpecialTypeCheck {
    public String identifier;
    public SpecialTypeAnalysisUtilities.SpecialType specialType;
    public boolean isSpecialType;

    public SpecialTypeCheck(String identifier, SpecialTypeAnalysisUtilities.SpecialType specialType, boolean isSpecialType) {
        this.identifier = identifier;
        this.specialType = specialType;
        this.isSpecialType = isSpecialType;
    }
}

