/*
 * 09/20/2013
 *
 * SearchListener - Listens for events in find/replace dialogs and tool bars.
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.license.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.util.EventListener;
import org.fife.ui.rtextarea.SearchEngine;

/**
 * Listens for events fired from a Find or Replace dialog/tool bar.
 * Applications can implement this class to listen for the user searching for
 * text, and actually perform the operation via {@link SearchEngine} in
 * response.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface SearchListener extends EventListener {
  /**
   * Callback called whenever a search event occurs.
   *
   * @param e The event.
   */
  void searchEvent(SearchEvent e);

  String getSelectedText();
}
