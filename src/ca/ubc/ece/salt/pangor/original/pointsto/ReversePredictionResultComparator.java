/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.pointsto;

import ca.ubc.ece.salt.pangor.original.pointsto.PredictionResult;
import java.util.Comparator;

class ReversePredictionResultComparator
implements Comparator<PredictionResult> {
    ReversePredictionResultComparator() {
    }

    @Override
    public int compare(PredictionResult o1, PredictionResult o2) {
        if (o2.likelihood > o1.likelihood) {
            return 1;
        }
        if (o2.likelihood < o1.likelihood) {
            return -1;
        }
        return 0;
    }
}

