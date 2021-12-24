/*
 * 03/19/2014
 *
 * DefaultSpellCheckableTokenIdentifier.java - Identifies comment tokens to
 * be spell checked.
 *
 * This library is distributed under the LGPL.  See the included
 * SpellChecker.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell;

import org.fife.ui.rsyntaxtextarea.Token;

/**
 * The spell-checkable token identifier used by {@link SpellingParser} if
 * none is explicitly identified.  It causes all comment tokens to be
 * spell checked.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DefaultSpellCheckableTokenIdentifier
  implements SpellCheckableTokenIdentifier {

  /**
   * The default implementation of this method does nothing; this token
   * identifier does not have state.
   */
  @Override
  public void begin() {}

  /**
   * The default implementation of this method does nothing; this token
   * identifier does not have state.
   */
  @Override
  public void end() {}

  /**
   * Returns <code>true</code> if the token is a comment.
   *
   * @return <code>true</code> only if the token is a comment.
   */
  @Override
  public boolean isSpellCheckable(Token t) {
    return t.isComment();
  }
}
