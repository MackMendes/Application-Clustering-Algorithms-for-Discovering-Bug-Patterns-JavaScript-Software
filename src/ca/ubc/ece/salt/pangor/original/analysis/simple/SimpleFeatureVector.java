/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.analysis.simple;

import ca.ubc.ece.salt.pangor.original.analysis.Commit;
import ca.ubc.ece.salt.pangor.original.analysis.FeatureVector;

public class SimpleFeatureVector
  extends FeatureVector
{
  public String pattern;
  
  public SimpleFeatureVector(Commit commit, String pattern)
  {
    super(commit);
    this.pattern = pattern;
  }
  
  public String toString()
  {
    return this.commit.buggyCommitID + ", " + this.commit.repairedCommitID + ", " + this.pattern;
  }
  
  public boolean equals(Object o)
  {
    if ((o instanceof SimpleFeatureVector))
    {
      SimpleFeatureVector sa = (SimpleFeatureVector)o;
      if ((this.pattern.equals(sa.pattern)) && 
        (this.commit.equals(sa.commit))) {
        return true;
      }
    }
    return false;
  }
}

