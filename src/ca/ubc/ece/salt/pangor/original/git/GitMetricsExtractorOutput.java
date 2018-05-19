/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.git;

import ca.ubc.ece.salt.pangor.original.git.GitProject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class GitMetricsExtractorOutput
{
  private PrintStream stream;
  
  public GitMetricsExtractorOutput(String filePath)
  {
    try
    {
      File path = new File(filePath);
      path.getParentFile().mkdirs();
      
      this.stream = new PrintStream(new FileOutputStream(filePath));
      writeHeaders();
    }
    catch (IOException e)
    {
      System.err.println(e.getMessage());
    }
  }
  
  public void output(GitProject gitProject)
  {
    String name = gitProject.getName();
    String URI = gitProject.getURI();
    String totalCommits = gitProject.getTotalCommits().toString();
    String totalBugFixingCommits = gitProject.getBugFixingCommits().toString();
    String numberAuthors = gitProject.getNumberAuthors().toString();
    String numberFiles = gitProject.getNumberOfFiles().toString();
    String linesOfCode = gitProject.getNumberOfLines().toString();
    String stargazers = gitProject.getStargazers().toString();
    String downloadsLastMonth = gitProject.getDownloadsLastMonth().toString();
    String lastCommit = gitProject.getLastCommitDate().toString();
    String firstCommit = gitProject.getFirstCommitDate().toString();
    
    String row = String.join(",", new CharSequence[] { name, URI, totalCommits, totalBugFixingCommits, numberAuthors, numberFiles, linesOfCode, stargazers, downloadsLastMonth, lastCommit, firstCommit });
    
    this.stream.println(row);
  }
  
  public void closeStream()
  {
    this.stream.close();
  }
  
  private void writeHeaders()
  {
    String header = String.join(",", new CharSequence[] { "Name", "URI", "TotalCommits", "BugFixingCommits", "NumberAuthors", "NumberFiles", "LinesOfCode", "Stargazers", "DownloadsLastMonth", "LastCommit", "FirstCommit" });
    
    this.stream.println(header);
  }
}
