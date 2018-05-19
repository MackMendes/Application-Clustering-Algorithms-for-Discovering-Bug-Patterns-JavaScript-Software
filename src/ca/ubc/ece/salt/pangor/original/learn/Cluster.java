/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.learn;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Cluster {
    public int cluster;
    public Map<Integer, String> instances;
    private int modifiedStatements;
    public Set<String> projects;
    public Map<String, Integer> keywords;

    public Cluster(int cluster) {
        this.cluster = cluster;
        this.modifiedStatements = 0;
        this.projects = new HashSet<String>();
        this.instances = new HashMap<Integer, String>();
        this.keywords = new HashMap<String, Integer>();
    }

    public void addInstance(int instanceID, int modifiedStatements, String project, String expected, List<String> keywords) {
        this.modifiedStatements += modifiedStatements;
        this.instances.put(instanceID, expected);
        this.projects.add(project);
        for (String modified : keywords) {
            int f = this.keywords.containsKey(modified) ? this.keywords.get(modified) + 1 : 1;
            this.keywords.put(modified, f);
        }
    }

    public int getAverageModifiedStatements() {
        double avg = this.modifiedStatements / this.instances.size();
        return (int)Math.round(avg);
    }

    public String getClusterID() {
        return String.valueOf(this.cluster);
    }

    public String getModifiedKeywords() {
        String modified = "{";
        for (Map.Entry<String, Integer> entry : this.keywords.entrySet()) {
          if (((Integer)entry.getValue()).intValue() / this.instances.size() >= 0.6D) {
            modified = modified + (String)entry.getKey() + " ";
          }
        }
        if (modified.length() > 1) {
          modified = modified.substring(0, modified.length() - 1);
        }
        return modified + "}";
    }

    public int getModifiedKeywordSize() {
    	 int count = 0;
    	    for (Map.Entry<String, Integer> entry : this.keywords.entrySet()) {
    	      if (((Integer)entry.getValue()).intValue() / this.instances.size() >= 0.6D) {
    	        count++;
    	      }
    	    }
        return count;
    }

    public int getProjectCount() {
        return this.projects.size();
    }

    public List<Map.Entry<String, Integer>> getClusterComposition() {
        HashMap<String, Integer> composition = new HashMap<String, Integer>();
        for (String expected : this.instances.values()) {
            Integer count = (Integer)composition.get(expected);
            count = count == null ? 1 : count + 1;
            composition.put(expected, count);
        }
        LinkedList<Map.Entry<String, Integer>> sorted = new LinkedList<Map.Entry<String, Integer>>(composition.entrySet());
        Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>(){

            @Override
            public int compare(Map.Entry<String, Integer> l, Map.Entry<String, Integer> r) {
                return l.getValue().compareTo(r.getValue());
            }
        });
        return sorted;
    }

    public boolean equals(Object o) {
        if (o instanceof Cluster) {
            Cluster c = (Cluster)o;
            if (this.cluster == c.cluster) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return Integer.hashCode(this.cluster);
    }

    public String toString() {
        return this.getModifiedKeywords() + ": ClusterID = " + this.cluster + ", Instances = " + this.instances.size() + ", Complexity = " + this.getAverageModifiedStatements() + ", BasicChanges = " + this.getModifiedKeywordSize() + ", ProjectCount = " + this.getProjectCount();
    }

}

