/*
 * 01/12/2009
 *
 * ContentAssistable.java - A component, such as text field, that supports
 * content assist.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui;

/**
 * A component (such as a text field) that supports content assist.
 * Implementations will fire a property change event of type
 * {@link #ASSISTANCE_IMAGE} when content assist is enabled or disabled.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface ContentAssistable {
  /**
   * Property event fired when the image to use when the component is focused
   * changes.  This will either be <code>null</code> for "no image," or
   * a <code>java.awt.Image</code>.
   */
  String ASSISTANCE_IMAGE = "AssistanceImage";
}
