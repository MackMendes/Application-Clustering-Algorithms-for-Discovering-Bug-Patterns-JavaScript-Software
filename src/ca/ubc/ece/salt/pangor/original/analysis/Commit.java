package ca.ubc.ece.salt.pangor.original.analysis;

import java.util.LinkedList;
import java.util.List;

public class Commit
{
  public String projectID;
  public String url;
  public String buggyCommitID;
  public String repairedCommitID;
  public String commitMessage;
  public Type commitMessageType;
  public List<SourceCodeFileChange> sourceCodeFileChanges;
  
  public Commit(String projectID, String projectHomepage, String buggyCommitID, String repairedCommitID, Type commitMessageType, String commitMessage)
  {
    this.projectID = projectID;
    this.url = projectHomepage;
    this.buggyCommitID = buggyCommitID;
    this.repairedCommitID = repairedCommitID;
    this.commitMessageType = commitMessageType;
    this.commitMessage = 
    		(commitMessage == null || commitMessage == "") ? "" : 
    			commitMessage.replaceAll("\n", " ").replace("\r", "").replace("\t", "").replace(",", ";").trim();
    
    this.sourceCodeFileChanges = new LinkedList<SourceCodeFileChange>();
  }
  
  public void addSourceCodeFileChange(SourceCodeFileChange scfc)
  {
    this.sourceCodeFileChanges.add(scfc);
  }
  
  public boolean equals(Object o)
  {
    if ((o instanceof Commit))
    {
      Commit a = (Commit)o;
      if ((this.projectID.equals(a.projectID)) && 
        (this.buggyCommitID.equals(a.buggyCommitID)) && 
        (this.repairedCommitID.equals(a.repairedCommitID))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return (this.projectID + this.repairedCommitID).hashCode();
  }
  
  public static enum Type
  {
    BUG_FIX,  MERGE,  OTHER, REFACTORING;
    
    private Type() {}
  }
}
