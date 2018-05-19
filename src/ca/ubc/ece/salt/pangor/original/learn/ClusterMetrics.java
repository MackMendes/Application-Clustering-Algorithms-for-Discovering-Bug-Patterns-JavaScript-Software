package ca.ubc.ece.salt.pangor.original.learn;

import ca.ubc.ece.salt.pangor.original.learn.Cluster;
import ca.ubc.ece.salt.pangor.original.learn.EvaluationResult;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class ClusterMetrics {
    public Map<Integer, Cluster> clusters = new HashMap<Integer, Cluster>();
    public int totalInstances = 0;
    public int totalClusteredInstances = 0;
    public double epsilon = 0.0D;
    public int[] confusionMatrix;

    public void setTotalInstances(int totalInstances) {
        this.totalInstances = totalInstances;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public void addInstance(int clusterNumber, int instanceID, String project, String expected, int modifiedStatements, List<String> keywords) {
        Cluster cluster = this.clusters.get(clusterNumber);
        if (cluster == null) {
            cluster = new Cluster(clusterNumber);
            this.clusters.put(clusterNumber, cluster);
        }
        cluster.addInstance(instanceID, modifiedStatements, project, expected, keywords);
        this.totalClusteredInstances += 1;
    }

    public int getTotalInstances() {
        return this.totalClusteredInstances;
    }

    public int getAverageInsances() {
        return this.totalClusteredInstances / this.clusters.size();
    }

    public int getMedianInstances() {
        int[] instances = new int[this.clusters.size()];
        int i = 0;
        for (Cluster cluster : this.clusters.values()) {
            instances[i] = cluster.instances.size();
            i++;
        }
        return instances[instances.length / 2];
    }

    public int getClusterCount() {
        return this.clusters.size();
    }

    public int getAverageComplexity() {
        int complexity = 0;
        for (Cluster cluster : this.clusters.values()) {
            complexity += cluster.getAverageModifiedStatements();
        }
        return Math.round(complexity / this.clusters.size());
    }

    public TreeSet<Cluster> getRankedClusters() {
        TreeSet<Cluster> rankedClusters = new TreeSet<Cluster>(new Comparator<Cluster>(){

            @Override
            public int compare(Cluster c1, Cluster c2) {
                if (c1.instances == c2.instances) {
                    return c1.toString().compareTo(c2.toString());
                }
                if (c1.instances.size() < c2.instances.size()) {
                    return 1;
                }
                return -1;
            }
        });
        rankedClusters.addAll(this.clusters.values());
        return rankedClusters;
    }

  public EvaluationResult evaluate(Map<Integer, String> oracle)
  {
    double tp = 0.0D;double fp = 0.0D;double tn = 0.0D;double fn = 0.0D;
    double classified = 0.0D;
    double p = 0.0D;double r = 0.0D;double f = 0.0D;double fm = 0.0D;double inspect = 0.0D;
    int captured = 0;
    
    Map<String, Double> clusterCompositions = new HashMap<String, Double>();
    
    Map<String, Double> classCompositions = new HashMap<String, Double>();
    Map.Entry<Integer, String> entity;
    Map<String, List<Integer>> expected = new HashMap<String, List<Integer>> ();
    for (Iterator<Entry<Integer, String>> localIterator1 = oracle.entrySet().iterator(); localIterator1.hasNext();)
    {
      entity = localIterator1.next();
      if (!((String)entity.getValue()).equals("?"))
      {
        List<Integer> instancesInClass = expected.get(entity.getValue());
        if (instancesInClass == null)
        {
          instancesInClass = new LinkedList<Integer>();
          expected.put(entity.getValue(), instancesInClass);
        }
        instancesInClass.add(entity.getKey());
        classified += 1.0D;
      }
    }
    
    Set<String> actual = new HashSet<String>();
    for (Cluster cluster : this.clusters.values())
    {
      int tpForCluster = 0;
      
      System.out.println("Composition of cluster " + cluster.getClusterID());
      
      List<Map.Entry<String, Integer>> composition = cluster.getClusterComposition();
      for (Map.Entry<String, Integer> entry : composition)
      {
        String classID = (String)entry.getKey();
        if (!classID.equals("?"))
        {
          double intersection = ((Integer)entry.getValue()).intValue();
          double classSize = (expected.get(entry.getKey())).size();
          double clusterSize = cluster.instances.size();
          
          double percentOfClass = intersection / classSize;
          
          double percentOfCluster = intersection / clusterSize;
          if ((percentOfClass >= 0.0D) && (percentOfCluster >= 0.0D) && (intersection >= 2.0D))
          {
            (actual).add(String.valueOf(expected.get(entry.getKey())));
            tp += intersection;
            tpForCluster = (int)(tpForCluster + intersection);
            
            Double clusterComposition = (Double)clusterCompositions.get(classID);
            clusterComposition = Double.valueOf(clusterComposition == null ? 0.0D : clusterComposition.doubleValue());
            if (percentOfCluster > clusterComposition.doubleValue()) {
              clusterCompositions.put(classID, Double.valueOf(percentOfCluster));
            }
            Double classComposition = (Double)classCompositions.get(classID);
            classComposition = Double.valueOf(classComposition == null ? 0.0D : classComposition.doubleValue());
            if (percentOfClass > classComposition.doubleValue()) {
              classCompositions.put(classID, Double.valueOf(percentOfClass));
            }
          }
          System.out.println((String)entry.getKey() + ": " + entry.getValue() + "(" + 
            Math.round(100.0D * percentOfCluster) + "% of cluster)" + "(" + 
            Math.round(100.0D * percentOfClass) + "% of class)");
        }
        else
        {
          System.out.println("?: " + entry.getValue());
        }
      }
      fp += cluster.instances.size() - tpForCluster;
    }
    fn = classified - tp;
    tn = this.totalInstances - tp - fp - fn;
    
    p = tp / (tp + fp);
    r = tp / (tp + fn);
    
    f = 2.0D * (p * r / (p + r));
    fm = Math.sqrt(tp / (tp + fp) * (tp / (tp + fn)));
    
    inspect = (tp + fp) / (tp + fp + tn + fn);
    
    captured = (actual).size();
    
    ConfusionMatrix confusionMatrix = new ConfusionMatrix((int)tp, (int)fp, (int)tn, (int)fn);
    
    EvaluationResult evaluationResult = new EvaluationResult(confusionMatrix, this.epsilon, p, r, f, fm, inspect, captured, clusterCompositions, classCompositions);
    
    return evaluationResult;
  }
  
    public String getRankedClusterTable() {
        String ranked = "";
        int i = 1;
        for (Cluster cluster : this.getRankedClusters()) {
            ranked = ranked + String.format("%-10s%s\n", i, cluster);
            ++i;
        }
        return ranked;
    }

    public static String getLatexTable(Set<ClusterMetrics> metrics) {
        String table = "\\begin{table*}\n";
        table = table + "\t\\centering\n";
        table = table + "\t\\caption{Clustering and Inspection Results}\n";
        table = table + "\t\\label{tbl:clusteringResults}\n";
        table = table + "{\\scriptsize\n";
        table = table + "\t\\begin{tabular}{ | l | r | r | r | r | r | }\n";
        table = table + "\t\t\\hline\n";
        table = table + "\t\t\\textbf{Keyword} & \\textbf{Clusters} & \\textbf{Tot Intances (I)}  & \\textbf{Avg I} & \\textbf{Mdn I} & \\textbf{Avg Complex.} \\\\ \\hline\n";
        for (ClusterMetrics metric : metrics) {
            table = table + "\t\tALL_KEYWORDS & " + metric.clusters.size() + " & " + metric.totalClusteredInstances + " & " + Math.round(metric.getAverageInsances()) + " & " + metric.getMedianInstances() + " & " + metric.getAverageComplexity() + "\\\\\n";
        }
        table = table + "\t\t\\hline\n";
        table = table + "\t\\end{tabular}\n";
        table = table + "}\n";
        table = table + "\\end{table*}\n";
        return table.replace("_", "\\_");
    }

    public String toString() {
        return "C = " + this.clusters + ", I = " + this.totalClusteredInstances + ", AVGI = " + this.getAverageInsances() + ", MDNI = " + this.getMedianInstances();
    }

    public class ConfusionMatrix {
        public int tp;
        public int fp;
        public int tn;
        public int fn;

        public ConfusionMatrix(int tp, int fp, int tn, int fn) {
            this.tp = tp;
            this.fp = fp;
            this.tn = tn;
            this.fn = fn;
        }
    }

}

