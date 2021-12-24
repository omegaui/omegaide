/*
 * 03/19/2014
 *
 * SpellCheckableTokenIdentifier.java - Identifies tokens to spell check.
 *
 * This library is distributed under the LGPL.  See the included
 * SpellChecker.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.ui.rsyntaxtextarea.Token;

/**
 * Identifies tokens that contain spell-checkable text.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface SpellCheckableTokenIdentifier {
  /**
   * Called before each parsing of the document for tokens to spell check.
   *
   * @see #end()
   */
  void begin();

  /**
   * Called when each parsing of the document completes.
   *
   * @see #begin()
   */
  void end();

  /**
   * Returns whether a particular token should be spell-checked.
   *
   * @param t The token.
   * @return Whether that token should be spell checked.
   */
  boolean isSpellCheckable(Token t);
}
