package ca.ubc.ece.salt.pangor.original.git;

import ca.ubc.ece.salt.pangor.original.analysis.Commit;
import ca.ubc.ece.salt.pangor.original.batch.GitProjectAnalysis;
import ca.ubc.ece.salt.pangor.original.batch.GitProjectAnalysisException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GitProject
{
  protected final Logger logger = LogManager.getLogger(GitProject.class);
  protected Git git;
  protected Repository repository;
  protected String projectID;
  protected String projectHomepage;
  protected String URI;
  protected Integer mergeCommits;
  protected Integer totalCommits;
  protected Integer numberAuthors;
  protected Integer numberOfFiles;
  protected Integer numberOfLines;
  protected Integer downloadsLastMonth = Integer.valueOf(-1);
  protected Integer stargazers = Integer.valueOf(-1);
  protected Date lastCommitDate;
  protected Date firstCommitDate;
  protected String commitMessageRegex;
  
  protected GitProject(Git git, Repository repository, String URI, String commitMessageRegex)
  {
    this.git = git;
    this.repository = repository;
    this.URI = URI;
    this.commitMessageRegex = commitMessageRegex;
    try
    {
      this.projectID = getGitProjectName(URI);
      this.projectHomepage = getGitProjectHomepage(URI);
    }
    catch (GitProjectAnalysisException e)
    {
      e.printStackTrace();
    }
  }
  
  protected GitProject(GitProject project, String commitMessageRegex)
  {
    this(project.git, project.repository, project.URI, commitMessageRegex);
  }
  
  public String getName()
  {
    return this.projectID;
  }
  
  public String getURI()
  {
    return this.URI;
  }
  
  public Integer getTotalCommits()
  {
    if (this.totalCommits == null) {
      getCommitPairs();
    }
    return this.totalCommits;
  }
  
  public Integer getBugFixingCommits()
  {
    if ((this.mergeCommits == null) || (this.totalCommits == null)) {
      getCommitPairs();
    }
    return Integer.valueOf(this.mergeCommits.intValue() - this.totalCommits.intValue());
  }
  
  public Integer getNumberAuthors()
  {
    if (this.numberAuthors == null) {
      getCommitPairs();
    }
    return this.numberAuthors;
  }
  
  public Date getLastCommitDate()
  {
    if (this.lastCommitDate == null) {
      getCommitPairs();
    }
    return this.lastCommitDate;
  }
  
  public Date getFirstCommitDate()
  {
    if (this.firstCommitDate == null) {
      getCommitPairs();
    }
    return this.firstCommitDate;
  }
  
  public Integer getNumberOfFiles()
  {
    if (this.numberOfFiles == null) {
      getFilesMetrics();
    }
    return this.numberOfFiles;
  }
  
  public Integer getNumberOfLines()
  {
    if (this.numberOfFiles == null) {
      getFilesMetrics();
    }
    return this.numberOfLines;
  }
  
  public Integer getDownloadsLastMonth()
  {
    return this.downloadsLastMonth;
  }
  
  public void setDownloadsLastMonth(Integer downloadsLastMonth)
  {
    this.downloadsLastMonth = downloadsLastMonth;
  }
  
  public Integer getStargazers()
  {
    if (!this.URI.contains("github.com")) {
      return Integer.valueOf(-1);
    }
    if (this.stargazers.intValue() == -1) {
      try
      {
        GitHub github = GitHub.connectAnonymously();
        
        GHRepository repository = github.getRepository(this.URI.split("github\\.com/")[1].split("\\.git")[0]);
        this.stargazers = Integer.valueOf(repository.getWatchers());
      }
      catch (IOException e)
      {
        System.err.println("Error while accessing GitHub API: " + e.getMessage());
        return Integer.valueOf(-1);
      }
    }
    return this.stargazers;
  }
  
  protected void getFilesMetrics()
  {
    Runtime runtime = Runtime.getRuntime();
    
    String[] command = { "/bin/sh", "-c", "ohcount " + this.repository.getDirectory().getParent().toString() + " | grep javascript | tr -s ' ' | cut -d ' ' -f 2,3" };
    try
    {
      Process process = runtime.exec("which ohcount");
      process.waitFor();
      if (process.exitValue() != 0) {
        throw new RuntimeException("Could not find ohcount command tool. Perphaphs not installed?");
      }
      process = runtime.exec(command);
      process.waitFor();
      
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String[] output = bufferedReader.readLine().split(" ");
      
      this.numberOfFiles = Integer.valueOf(Integer.parseInt(output[0]));
      this.numberOfLines = Integer.valueOf(Integer.parseInt(output[1]));
      
      bufferedReader.close();
    }
    catch (IOException|InterruptedException e)
    {
      e.printStackTrace();
      
      this.numberOfFiles = Integer.valueOf(0);
      this.numberOfLines = Integer.valueOf(0);
    }
  }
  
  protected List<Triple<String, String, Commit.Type>> getCommitPairs()
  {
    List<Triple<String, String, Commit.Type>> bugFixingCommits = new LinkedList<Triple<String, String, Commit.Type>>();
    int mergeCommits = 0;int commitCounter = 0;
    
    Set<String> authorsEmails = new HashSet<String>();
    Iterable<RevCommit> commits;
    Date lastCommitDate = null;
    Date firstCommitDate = null;
    try
    {
      commits = this.git.log().call();
    }
    catch (GitAPIException e)
    {
      e.printStackTrace();
      return bugFixingCommits;
    }

    for (RevCommit commit : commits)
    {
      PersonIdent authorIdent = commit.getAuthorIdent();
      authorsEmails.add(authorIdent.getEmailAddress());
      
      Commit.Type commitMessageType = Commit.Type.OTHER;
      String message = commit.getFullMessage();
      commitCounter++;
      
      Pattern pEx = Pattern.compile("merge", 2);
      Matcher mEx = pEx.matcher(message);
      
      Pattern pBFC = Pattern.compile(this.commitMessageRegex, 2);
      Matcher mBFC = pBFC.matcher(message);
      if (mEx.find()) {
        commitMessageType = Commit.Type.MERGE;
      } else if (mBFC.find()) {
        commitMessageType = Commit.Type.BUG_FIX;
      }
      if (commit.getParentCount() > 0) {
        bugFixingCommits.add(Triple.of(commit.getParent(0).name(), commit.name(), commitMessageType));
      }
      if (commitCounter == 1) {
        lastCommitDate = authorIdent.getWhen();
      }
      firstCommitDate = authorIdent.getWhen();
    }
    this.mergeCommits = Integer.valueOf(mergeCommits);
    this.totalCommits = Integer.valueOf(commitCounter);
    this.numberAuthors = Integer.valueOf(authorsEmails.size());
    this.lastCommitDate = lastCommitDate;
    this.firstCommitDate = firstCommitDate;
    
    return bugFixingCommits;
  }
  
  protected static String getGitProjectName(String uri)
    throws GitProjectAnalysisException
  {
    Pattern namePattern = Pattern.compile("([^/]+)\\.git");
    Matcher matcher = namePattern.matcher(uri);
    if (!matcher.find()) {
      throw new GitProjectAnalysisException("Could not find the .git name in the URI.");
    }
    return matcher.group(1);
  }
  
  protected static String getGitProjectHomepage(String uri)
    throws GitProjectAnalysisException
  {
    return uri.substring(0, uri.lastIndexOf(".git"));
  }
  
  protected static File getGitDirectory(String uri, String directory)
    throws GitProjectAnalysisException
  {
    return new File(directory, getGitProjectName(uri));
  }
  
  public static GitProject fromDirectory(String directory, String commitMessageRegex)
    throws GitProjectAnalysisException
  {
	  Git git;
	  Repository repository;
    try
    {
      repository = ((RepositoryBuilder)new RepositoryBuilder().findGitDir(new File(directory))).build();
      git = Git.wrap(repository);
    }
    catch (IOException e)
    {
      throw new GitProjectAnalysisException("The git project was not found in the directory " + directory + ".");
    }

    return new GitProject(git, repository, repository.getConfig().getString("remote", "origin", "url"), commitMessageRegex);
  }
  
  public static GitProject fromURI(String uri, String directory, String commitMessageRegex)
    throws GitProjectAnalysisException, InvalidRemoteException, TransportException, GitAPIException
  {
    File gitDirectory = GitProjectAnalysis.getGitDirectory(uri, directory);
    Git git;
    Repository repository;
    if (gitDirectory.exists())
    {
      try
      {
        repository = ((RepositoryBuilder)new RepositoryBuilder().findGitDir(gitDirectory)).build();
        git = Git.wrap(repository);
      }
      catch (IOException e)
      {
        throw new GitProjectAnalysisException("The git project was not found in the directory " + directory + ".");
      }

      StoredConfig config = repository.getConfig();
      if (!config.getString("remote", "origin", "url").equals(uri)) {
        throw new GitProjectAnalysisException("The directory " + gitDirectory + " is being used by a different remote repository.");
      }
      PullCommand pullCommand = git.pull();
      PullResult pullResult = pullCommand.call();
      if (!pullResult.isSuccessful()) {
        throw new GitProjectAnalysisException("Pull was not succesfull for " + gitDirectory);
      }
    }
    else
    {
      CloneCommand cloneCommand = Git.cloneRepository().setURI(uri).setDirectory(gitDirectory);
      git = cloneCommand.call();
      repository = git.getRepository();
    }
    GitProject gitProject = new GitProject(git, repository, uri, commitMessageRegex);
    
    return gitProject;
  }
}
