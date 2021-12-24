/*
 * 09/20/2013
 *
 * SearchEvent - The event fired for find/replace/mark all operations.
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.license.txt file for details.
 */
package org.fife.rsta.ui.search;

import java.util.EventObject;
import org.fife.ui.rtextarea.SearchContext;

/**
 * The event fired whenever a user wants to search for or replace text in a
 * Find or Replace dialog/tool bar.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SearchEvent extends EventObject {

  private SearchContext context;
  private Type type;

  public SearchEvent(Object source, Type type, SearchContext context) {
    super(source);
    this.type = type;
    this.context = context;
  }

  public Type getType() {
    return type;
  }

  public SearchContext getSearchContext() {
    return context;
  }

  /**
   * Types of search events.
   */
  public enum Type {
    /**
     * The event fired when the text to "mark all" has changed.
     */
    MARK_ALL,

    /**
     * The event fired when the user wants to find text in the editor.
     */
    FIND,

    /**
     * The event fired when the user wants to replace text in the editor.
     */
    REPLACE,

    /**
     * The event fired when the user wants to replace all instances of
     * specific text with new text in the editor.
     */
    REPLACE_ALL,
  }
}
