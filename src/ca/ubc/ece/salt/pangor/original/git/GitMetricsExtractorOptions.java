/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.kohsuke.args4j.Option
 */
package ca.ubc.ece.salt.pangor.original.git;

import org.kohsuke.args4j.Option;

public class GitMetricsExtractorOptions
{
  @Option(name="-h", aliases={"--help"}, usage="Display the help file.")
  private boolean help = false;
  @Option(name="-i", aliases={"--input"}, usage="The file with git repositories. One repository per line.")
  private String inputPath = "./input/repositories.txt";
  @Option(name="-o", aliases={"--output"}, usage="The output CSV file with the repository and the metrics.")
  private String outputPath = "./input/repositories_metrics.csv";
  @Option(name="-mrx", aliases={"--message_regex"}, usage="The regular expression that a commit message must match in order to be analyzed")
  private String commitMessageRegex = "^.*$";
  
  public boolean getHelp()
  {
    return this.help;
  }
  
  public String getInputPath()
  {
    return this.inputPath;
  }
  
  public String getOutputPath()
  {
    return this.outputPath;
  }
  
  public String getCommitMessageRegex()
  {
    return this.commitMessageRegex;
  }
}