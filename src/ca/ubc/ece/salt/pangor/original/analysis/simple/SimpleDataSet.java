/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.deri.iris.api.IKnowledgeBase
 *  org.deri.iris.api.basics.IQuery
 *  org.deri.iris.api.basics.IRule
 *  org.deri.iris.api.basics.ITuple
 *  org.deri.iris.storage.IRelation
 */
package ca.ubc.ece.salt.pangor.original.analysis.simple;

import ca.ubc.ece.salt.pangor.original.analysis.Commit;
import ca.ubc.ece.salt.pangor.original.analysis.DataSet;
import java.util.LinkedList;
import java.util.List;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.storage.IRelation;

public class SimpleDataSet
  extends DataSet
{
  private List<SimpleFeatureVector> alerts;
  
  public SimpleDataSet(List<IRule> rules, List<IQuery> queries)
  {
    super(rules, queries);
    this.alerts = new LinkedList<SimpleFeatureVector>();
  }
  
  public void registerAlerts(Commit commit, IKnowledgeBase knowledgeBase)
    throws Exception
  {
    for (IQuery query : this.queries)
    {
      IRelation results = knowledgeBase.execute(query);
      for (int i = 0; i < results.size(); i++)
      {
        ITuple tuple = results.get(i);
        
        this.alerts.add(new SimpleFeatureVector(commit, "[" + query.toString() + "](" + tuple.toString() + ")"));
      }
    }
  }
  
  public List<SimpleFeatureVector> getAlerts()
  {
    return this.alerts;
  }
  
  public boolean contains(SimpleFeatureVector alert)
  {
    return this.alerts.contains(alert);
  }
  
  public String printAlerts()
  {
    String file = "";
    int i = 1;
    for (SimpleFeatureVector alert : this.alerts)
    {
      file = file + i + ", " + alert.toString() + "\n";
      i++;
    }
    return file;
  }
}
