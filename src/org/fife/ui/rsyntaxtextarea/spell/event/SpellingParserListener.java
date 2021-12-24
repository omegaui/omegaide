/*
 * 08/29/2009
 *
 * SpellingParserListener.java - Listens for events from a spelling parser.
 *
 * This library is distributed under the LGPL.  See the included
 * SpellChecker.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.spell.event;

import java.util.EventListener;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;

/**
 * Listens for events from a {@link SpellingParser}.  A listener of this type
 * will receive notification when:
 *
 * <ul>
 *    <li>A word is added to the user's dictionary.</li>
 *    <li>A word will be ignored for the rest of the JVM session.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface SpellingParserListener extends EventListener {
  /**
   * Called when an event occurs in the spelling parser.
   *
   * @param e The event.
   */
  void spellingParserEvent(SpellingParserEvent e);
}
