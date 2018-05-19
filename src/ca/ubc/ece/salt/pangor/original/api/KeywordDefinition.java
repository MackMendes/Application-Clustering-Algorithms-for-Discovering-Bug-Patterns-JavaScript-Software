/*
 * Decompiled with CFR 0_123.
 */
package ca.ubc.ece.salt.pangor.original.api;

import ca.ubc.ece.salt.pangor.original.api.AbstractAPI;

public class KeywordDefinition
{
  public KeywordType type;
  public String keyword;
  public AbstractAPI api;
  
  public KeywordDefinition(KeywordType type, String keyword)
  {
    this.type = type;
    this.keyword = keyword;
  }
  
  public KeywordDefinition(KeywordType type, String keyword, AbstractAPI api)
  {
    this(type, keyword);
    this.api = api;
  }
  
  public void setAPI(AbstractAPI api)
  {
    this.api = api;
  }
  
  public String getPackageName()
  {
    if (this.api != null) {
      return this.api.getPackageName();
    }
    return null;
  }
  
  public boolean equals(Object obj)
  {
    if ((obj instanceof KeywordDefinition))
    {
      KeywordDefinition that = (KeywordDefinition)obj;
      if ((this.type == that.type) && (this.keyword.equals(that.keyword))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return (this.type.toString() + "_" + this.keyword).hashCode();
  }
  
  public String toString()
  {
    return this.type.toString() + "_" + this.keyword;
  }
  
  public static enum KeywordType
  {
    UNKNOWN,  
    RESERVED,  
    PACKAGE,  
    CLASS,  
    METHOD,  
    FIELD,  
    CONSTANT,  
    VARIABLE,  
    PARAMETER,  
    EXCEPTION,  
    EVENT;
    
    private KeywordType() {}
  }
}
