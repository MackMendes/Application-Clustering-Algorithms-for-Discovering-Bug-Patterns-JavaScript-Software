/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.pointsto;

import ca.ubc.ece.salt.pangor.original.pointsto.PredictionResult;
import ca.ubc.ece.salt.pangor.original.pointsto.ReversePredictionResultComparator;
import java.util.PriorityQueue;

public class PredictionResults
  extends PriorityQueue<PredictionResult>
{
  private static final long serialVersionUID = 5707048119553423942L;
  
  public PredictionResults()
  {
    super(new ReversePredictionResultComparator());
  }
}
