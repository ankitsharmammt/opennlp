///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2006 Calcucare GmbH
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

package opennlp.tools.namefind;

/**
 * An {@link Name} marks a span in a token array. It
 * contains the begin and end indexes.
 */
public class Name {
    
  private final String type;
  private final int begin;
  private final int end;
  
  public Name(String type, int begin, int end) {
    
    if (type == null) {
	throw new IllegalArgumentException("type must not be null");
    }
    
    this.type = type;
    
    if (begin < 0 || end < 0) {
	throw new IllegalArgumentException(
		"begin or end must be greater or equals zero");
    }
    
    if (begin > end) {
	throw new IllegalArgumentException(
		"begin index must be before the end index");
    }
    
    this.begin = begin;
    this.end = end;
  }
  
  public String type() {
      return type;
  }
  
  public int getBegin() {
    return begin;
  }
  
  public int getEnd() {
    return end;
  }
}