/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.eclipse.jgit.api.errors.GitAPIException
 *  org.eclipse.jgit.api.errors.InvalidRemoteException
 *  org.eclipse.jgit.api.errors.TransportException
 *  org.kohsuke.args4j.CmdLineException
 *  org.kohsuke.args4j.CmdLineParser
 */
package ca.ubc.ece.salt.pangor.original.git;

import ca.ubc.ece.salt.pangor.original.batch.GitProjectAnalysisException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class GitMetricsExtractorMain
{
  public static final String CHECKOUT_DIR = new String("repositories");
  
  public static void main(String[] args)
    throws InvalidRemoteException, TransportException, GitProjectAnalysisException, GitAPIException
  {
    GitMetricsExtractorOptions options = new GitMetricsExtractorOptions();
    CmdLineParser parser = new CmdLineParser(options);
    try
    {
      parser.parseArgument(args);
    }
    catch (CmdLineException e)
    {
      printUsage(e.getMessage(), parser);
      return;
    }
    if (options.getHelp())
    {
      printHelp(parser);
      return;
    }
    List<String> lines = parseInputFile(options.getInputPath());
    
    GitMetricsExtractorOutput metricsOutput = new GitMetricsExtractorOutput(options.getOutputPath());
    for (String line : lines)
    {
      GitProject project;
      if (line.contains(","))
      {
        String uri = line.split(",")[0];
        Integer downloadsLastMonth = Integer.valueOf(Integer.parseInt(line.split(",")[1]));
        
        project = GitProject.fromURI(uri, CHECKOUT_DIR, options.getCommitMessageRegex());
        project.setDownloadsLastMonth(downloadsLastMonth);
      }
      else
      {
        project = GitProject.fromURI(line, CHECKOUT_DIR, options.getCommitMessageRegex());
      }
      System.out.println("* Accessing repository: " + project.getURI());
      
      metricsOutput.output(project);
    }
    metricsOutput.closeStream();
  }
  
  private static List<String> parseInputFile(String filePath)
  {
    List<String> lines = new LinkedList<String>();
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(filePath));Throwable localThrowable3 = null;
      try
      {
        String line;
        while ((line = br.readLine()) != null) {
          if ((!line.isEmpty()) && (line.charAt(0) != '#')) {
            lines.add(line);
          }
        }
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      }
      finally
      {
        if (br != null) {
          if (localThrowable3 != null) {
            try
            {
              br.close();
            }
            catch (Throwable localThrowable2)
            {
              localThrowable3.addSuppressed(localThrowable2);
            }
          } else {
            br.close();
          }
        }
      }
    }
    catch (Exception e)
    {
      System.err.println("Error while reading URI file: " + e.getMessage());
    }
    return lines;
  }
  
  private static void printHelp(CmdLineParser parser)
  {
    System.out.print("Usage: DataSetMain ");
    parser.setUsageWidth(Integer.MAX_VALUE);
    parser.printSingleLineUsage(System.out);
    System.out.println("\n");
    parser.printUsage(System.out);
    System.out.println("");
  }
  
  private static void printUsage(String error, CmdLineParser parser)
  {
    System.out.println(error);
    System.out.print("Usage: DataSetMain ");
    parser.setUsageWidth(Integer.MAX_VALUE);
    parser.printSingleLineUsage(System.out);
    System.out.println("");
  }
}
