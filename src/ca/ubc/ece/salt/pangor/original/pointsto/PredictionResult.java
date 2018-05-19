/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.pointsto;

import ca.ubc.ece.salt.pangor.original.api.AbstractAPI;

public class PredictionResult
{
  public AbstractAPI api;
  public double likelihood;
  
  public PredictionResult(AbstractAPI api, double likelihood)
  {
    this.api = api;
    this.likelihood = likelihood;
  }
}
