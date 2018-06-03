package ca.ubc.ece.salt.pangor.original.batch;

import ca.ubc.ece.salt.pangor.original.analysis.Commit;
import ca.ubc.ece.salt.pangor.original.analysis.CommitAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.git.GitProject;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GitProjectAnalysis
  extends GitProject
{
  private CommitAnalysis commitAnalysis;
  
  protected GitProjectAnalysis(GitProject gitProject, CommitAnalysis commitAnalysis, String commitMessageRegex)
  {
    super(gitProject, commitMessageRegex);
    this.commitAnalysis = commitAnalysis;
  }
  
  public void analyze()
    throws GitAPIException, IOException, Exception
  {
    long startTime = System.currentTimeMillis();
    this.logger.info("[START ANALYSIS] {}", new Object[] { getURI() });
    
    List<Triple<String, String, Pair<Commit.Type, String>>> commits = getCommitPairs();
    
    this.logger.info(" [ANALYZING] {} bug fixing commits", new Object[] { Integer.valueOf(commits.size()) });
    for (Triple<String, String, Pair<Commit.Type, String>> commit : commits) {
      try
      {
        analyzeDiff((String)commit.getLeft(), (String)commit.getMiddle(), (Pair<Commit.Type, String>)commit.getRight());
      }
      catch (Exception e)
      {
        this.logger.error("[ERROR] {} ", new Object[] { e.getMessage() });
      }
    }
    long endTime = System.currentTimeMillis();
    this.logger.info("[END ANALYSIS] {}. Time (in seconds): {} ", new Object[] { getURI(), Double.valueOf((endTime - startTime) / 1000.0D) });
  }
  
  private void analyzeDiff(String buggyRevision, String bugFixingRevision, Pair<Commit.Type, String> commitMessageTypeAndMessageFull)
    throws IOException, GitAPIException, Exception
  {
    ObjectId buggy = this.repository.resolve(buggyRevision + "^{tree}");
    ObjectId repaired = this.repository.resolve(bugFixingRevision + "^{tree}");
    
    ObjectReader reader = this.repository.newObjectReader();
    
    CanonicalTreeParser buggyTreeIter = new CanonicalTreeParser();
    buggyTreeIter.reset(reader, buggy);
    
    CanonicalTreeParser repairedTreeIter = new CanonicalTreeParser();
    repairedTreeIter.reset(reader, repaired);
    
    DiffCommand diffCommand = this.git.diff().setShowNameAndStatusOnly(true).setOldTree(buggyTreeIter).setNewTree(repairedTreeIter);
    
    List<DiffEntry> diffs = diffCommand.call();
    
    Commit commit = new Commit(this.projectID, this.projectHomepage, buggyRevision, bugFixingRevision, commitMessageTypeAndMessageFull.getLeft(), commitMessageTypeAndMessageFull.getRight());
    for (DiffEntry diff : diffs) {
      if ((diff.getOldPath().matches("^.*jquery.*$")) || (diff.getNewPath().matches("^.*jquery.*$")))
      {
        this.logger.info("[SKIP_FILE] jquery file: " + diff.getOldPath());
      }
      else
      {
        if ((diff.getOldPath().endsWith(".min.js")) || (diff.getNewPath().endsWith(".min.js")))
        {
          this.logger.info("[SKIP_FILE] Skipping minifed file: " + diff.getOldPath());
          return;
        }
        this.logger.debug("Exploring diff \n {} \n {} - {} \n {} - {}", new Object[] { getURI(), buggyRevision, diff.getOldPath(), bugFixingRevision, diff
          .getNewPath() });
        
        String oldFile = fetchBlob(buggyRevision, diff.getOldPath());
        String newFile = fetchBlob(bugFixingRevision, diff.getNewPath());
        
        commit.addSourceCodeFileChange(new SourceCodeFileChange(diff
          .getOldPath(), diff.getNewPath(), oldFile, newFile));
      }
    }
    
    try
    {
      this.commitAnalysis.analyze(commit);
    }
    catch (Exception ignore)
    {
      System.err.println("Ignoring exception in ProjectAnalysis.runSDJSB.\nBuggy Revision: " + buggyRevision + "\nBug Fixing Revision: " + bugFixingRevision);
      throw ignore;
    }
    catch (Error e)
    {
      System.err.println("Ignoring error in ProjectAnalysis.runSDJSB.\nBuggy Revision: " + buggyRevision + "\nBug Fixing Revision: " + bugFixingRevision);
      throw e;
    }
  }
  
  private String fetchBlob(String revSpec, String path)
    throws MissingObjectException, IncorrectObjectTypeException, IOException
  {
    ObjectId id = this.repository.resolve(revSpec);
    
    ObjectReader reader = this.repository.newObjectReader();
    try
    {
      RevWalk walk = new RevWalk(reader);
      RevCommit commit = walk.parseCommit(id);
      
      RevTree tree = commit.getTree();
      
      TreeWalk treewalk = TreeWalk.forPath(reader, path, new AnyObjectId[] { tree });
      byte[] data;
      if (treewalk != null)
      {
        data = reader.open(treewalk.getObjectId(0)).getBytes();
        return new String(data, "utf-8");
      }
      return "";
    }
    finally
    {
      reader.release();
    }
  }
  
  public static GitProjectAnalysis fromDirectory(String directory, String commitMessageRegex, CommitAnalysis commitAnalysis)
    throws GitProjectAnalysisException
  {
    GitProject gitProject = GitProject.fromDirectory(directory, commitMessageRegex);
    
    return new GitProjectAnalysis(gitProject, commitAnalysis, commitMessageRegex);
  }
  
  public static GitProjectAnalysis fromURI(String uri, String directory, String commitMessageRegex, CommitAnalysis commitAnalysis)
    throws GitProjectAnalysisException, InvalidRemoteException, TransportException, GitAPIException
  {
    GitProject gitProject = GitProject.fromURI(uri, directory, commitMessageRegex);
    
    return new GitProjectAnalysis(gitProject, commitAnalysis, commitMessageRegex);
  }
}
