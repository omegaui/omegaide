/*
Jazzy - a Java library for Spell Checking
Copyright (C) 2001 Mindaugas Idzelis
Full text of license can be found in LICENSE.txt

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package org.fife.com.swabunga.spell.event;

/**
 * An exception to indicate that there not enough words as expected.
 */
public class WordNotFoundException extends RuntimeException {

  //~ Constructors ............................................................

  /**
   * Creates a new WordNotFoundException object.
   */
  public WordNotFoundException() {
    super();
  }

  /**
   * Creates a new WordNotFoundException object.
   *
   * @param s a message.
   */
  public WordNotFoundException(String s) {
    super(s);
  }
}
