/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceCodeFileChange
{
  public String buggyFile;
  public String repairedFile;
  public String buggyCode;
  public String repairedCode;
  
  public SourceCodeFileChange(String buggyFile, String repairedFile, String buggyCode, String repairedCode)
  {
    this.buggyFile = buggyFile;
    this.repairedFile = repairedFile;
    this.buggyCode = buggyCode;
    this.repairedCode = repairedCode;
  }
  
  public String getFileName()
  {
    Pattern pattern = Pattern.compile("/([A-za-z]+)\\.java");
    Matcher m = pattern.matcher(this.repairedFile);
    if (m.find()) {
      return m.group(1);
    }
    return "[unknown]";
  }
  
  public boolean equals(Object o)
  {
    if ((o instanceof SourceCodeFileChange))
    {
      SourceCodeFileChange a = (SourceCodeFileChange)o;
      if ((this.buggyFile.equals(a.buggyFile)) && 
        (this.repairedFile.equals(a.repairedFile))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return (this.buggyFile + this.repairedCode).hashCode();
  }
}
