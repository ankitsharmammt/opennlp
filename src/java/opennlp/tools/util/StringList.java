///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2008 OpenNlp
// 
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
// 
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
// 
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////

package opennlp.tools.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@link StringList} is an immutable list of {@link String}s.
 */
public class StringList {
  
  private String tokens[];
  
  /**
   * Initializes the current instance.
   * 
   * @param singleToken one single token
   */
  public StringList(String singleToken) {
    tokens = new String[] {
          singleToken.intern()
        };
  }
  
  /**
   * Initializes the current instance.
   * 
   * @param tokens
   */
  public StringList(String... tokens) {
    
    if (tokens == null || tokens.length == 0) {
      throw new IllegalArgumentException();
    }
    
    this.tokens = new String[tokens.length];
    
    for (int i = 0; i < tokens.length; i++) {
      this.tokens[i] = tokens[i].intern();
    }
  }
  
  /**
   * Retrieves a token from the given index.
   * 
   * @param index
   * 
   * @return token at the given index
   */
  public String getToken(int index) {
    return tokens[index];
  }
  
  /**
   * Retrieves the number of tokens inside this list.
   *  
   * @return number of tokens
   */
  public int size() {
    return tokens.length;
  }
  
  /**
   * Retrieves an {@link Iterator} over all {@link Token}s.
   * 
   * @return iterator over tokens
   */
  public Iterator<String> iterator() {
    return new Iterator<String>() {
      
      private int index;
      
      public boolean hasNext() {
        return index < size();
      }

      public String next() {
        
        if (hasNext()) {
          return getToken(index++);
        }
        else {
          throw new NoSuchElementException();
        }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
      
    };
  }
  
  /**
   * Compares to tokens list and ignores the case of the tokens.
   * 
   * Note: This can cause problems with some locals.
   * 
   * @param tokens
   * 
   * @return true if identically with ignore the case otherwise false
   */
  public boolean compareToIgnoreCase(StringList tokens) {
    
    if (size() == tokens.size()) {
      for (int i = 0; i < size(); i++) {
        
        if (getToken(i).compareToIgnoreCase(
            tokens.getToken(i)) != 0) {
          return false;
        } 
      }
    }
    else {
      return false;
    }
    
    return true;
  }
  
  
  public boolean equals(Object obj) {
    
    boolean result;
    
    if (this == obj) {
      result = true;
    }
    else if (obj != null && obj instanceof StringList) {
      StringList tokenList = (StringList) obj;
      
      result = Arrays.equals(tokens, tokenList.tokens);
    }
    else {
      result = false;
    }
    
    return result;
  }
  
  public int hashCode() {
    int numBitsRegular = 32 / size();
    int numExtra = 32 % size();
    int maskExtra = 0xFFFFFFFF >>> (32 - numBitsRegular + 1);
    int maskRegular = 0xFFFFFFFF >>> 32 - numBitsRegular;
    int code = 0x000000000;
    int leftMostBit = 0;

    for (int wi = 0; wi < size(); wi++) {
      int word;
      int mask;
      int numBits;
      if (wi < numExtra) {
        mask = maskExtra;
        numBits = numBitsRegular + 1;
      } else {
        mask = maskRegular;
        numBits = numBitsRegular;
      }
      word = getToken(wi).hashCode() & mask; // mask off top bits
      word <<= 32 - leftMostBit - numBits; // move to correct position
      leftMostBit += numBits; // set for next iteration
      code |= word;
    }
    
    return code;
  }
  
  public String toString() {
    StringBuffer string = new StringBuffer();
    
    string.append('[');
    
    for (int i = 0; i < size(); i++) {
      string.append(getToken(i));
      
      if (i < size() - 1) {
        string.append(',');
      }
    }
    
    string.append(']');
    
    return string.toString();
  }
}