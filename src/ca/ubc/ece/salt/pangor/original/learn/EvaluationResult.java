/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.learn;

import ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics;
import java.util.Map;

public class EvaluationResult {
    public ClusterMetrics.ConfusionMatrix confusionMatrix;
    public double epsilon;
    public double precision;
    public double recall;
    public double fMeasure;
    public double fowlkesMallows;
    public double inspected;
    public int patternRecall;
    public Map<String, Double> clusterComposition;
    public Map<String, Double> classComposition;

    public EvaluationResult(ClusterMetrics.ConfusionMatrix confusionMatrix, double epsilon, double precision, double recall, double fMeasure, double fowlkesMallows, double inspected, int patternRecall, Map<String, Double> clusterComposition, Map<String, Double> classComposition) {
        this.epsilon = epsilon;
        this.confusionMatrix = confusionMatrix;
        this.precision = precision;
        this.recall = recall;
        this.fMeasure = fMeasure;
        this.fowlkesMallows = fowlkesMallows;
        this.inspected = inspected;
        this.patternRecall = patternRecall;
        this.clusterComposition = clusterComposition;
        this.classComposition = classComposition;
    }

    public String getConfusionMatrix() {
        String matrix = "              \tClustered\tNot Clustered\n";
        matrix = matrix + "Classified    \t" + this.confusionMatrix.tp + "\t\t" + this.confusionMatrix.fn + "\n";
        matrix = matrix + "Not Classified\t" + this.confusionMatrix.fp + "\t\t" + this.confusionMatrix.tn + "\n";
        return matrix;
    }

    public String getResultsArrayHeader() {
        return "Class, Epsilon, Precision, Recall, FMeasure, FowlkesMallows, Inspected, CapturedPatterns";
    }

    public String getResultsArray(String[] classes) {
        String array = "";
        array = array + String.format("%.2f,%.2f, %.2f, %.2f, %.2f, %.2f, %s", this.epsilon, this.precision, this.recall, this.fMeasure, this.fowlkesMallows, this.inspected, this.patternRecall);
        double totalClusterComp = 0.0D;
        double totalClassComp = 0.0D;
        double totalInst = 0.0D;
        for (int k = 0; k < classes.length; k++) {
            Double clusterComp = this.clusterComposition.get(classes[k]);
            if (clusterComp == null) {
                array = array + ",NA";
            } else {
                totalClusterComp += clusterComp.doubleValue();
                totalInst += 1.0D;
                array = array + String.format(",%.2f", clusterComp);
            }
            Double classComp = this.classComposition.get(classes[k]);
            if (classComp == null) {
                array = array + ",NA";
                continue;
            }
            totalClassComp += classComp.doubleValue();
            array = array + String.format(",%.2f", classComp);
        }
        array = array + String.format(",%.2f,%.2f", totalClusterComp / totalInst, totalClassComp / totalInst);
        return array;
    }
}

