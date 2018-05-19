/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.analysis;

import ca.ubc.ece.salt.pangor.original.analysis.Commit;

public abstract class FeatureVector {

	  private static int idCounter;
	  public int id;
	  public Commit commit;
	  
	  public FeatureVector(Commit commit)
	  {
	    this.commit = commit;
	    this.id = getNextID();
	  }
	  
	  public FeatureVector(Commit commit, int id)
	  {
	    this.commit = commit;
	    this.id = id;
	  }
	  
	  private static synchronized int getNextID()
	  {
	    idCounter += 1;
	    return idCounter;
	  }
	  
	  public abstract boolean equals(Object paramObject);
}

