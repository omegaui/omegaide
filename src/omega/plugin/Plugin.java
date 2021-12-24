/**
  * The Plugin Interface
  * Copyright (C) 2021 Omega UI

  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.

  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package omega.plugin;

import java.net.URL;
import java.util.LinkedList;

public interface Plugin {
  //Note: The Plugin File should end with a ".jar" extension & its name will be the full qualified name of the Plugin Class e.g: omega.sample.SamplePlugin.jar

  /**
   * method <code>init()</code> gets called during the initialization of the IDE or when the plugin is activated provided that it does not requires restart
   */
  boolean init();
  /**
   * method <code>enable()</code> gets called after successful initialization of the plugin
   */
  boolean enable();
  /**
   * method <code>disable()</code> gets called when the user attempts to disable the plugin in the Plugin Manager
   */
  boolean disable();
  /**
   * method <code>needsRestart()</code> as it name suggests it is used to check whether enabling the plugin requires restart
   */
  boolean needsRestart();
  /**
   * method <code>getName()</code> to get plugin 's conventional name
   */
  String getName();
  /**
   * method <code>getVersion()</code> to get ide version with which the plugin is compatible.
   */
  String getVersion();
  /**
   * method <code>getAuthor()</code> to get plugins author name
   */
  String getAuthor();
  /**
   * method <code>getDescription()</code> to get One-Line description of the Plugin
   */
  String getDescription();
  /**
   * method <code>getSizeInMegaBytes()</code> as the name suggests to get plugin 's size in megabytes e.g: "13.1 MB"
   */
  String getSizeInMegaBytes();
  /**
   * method <code>getLicense()</code> to get plugin 's license name e.g: GNU GPL v3
   */
  String getLicense();
  /**
   * method <code>getImage()</code> to get plugin 's icon image
   */
  URL getImage();
  /**
   * method <code>getPluginCategory()</code> to get plugin 's category (category labels are available in {@link omega.plugin.PluginCategory})e.g: PluginCategory.EDITING
   */
  String getPluginCategory();

  /**
   * method <code>getScreenshots()</code> to get plugin 's screenshots if any, if there are no screenshots then don't implement this method
   */
  default LinkedList<URL> getScreenshots() {
    return new LinkedList<URL>();
  }

  /**
   * method <code>registerReaction()</code> should be used when using the {@link omega.plugin.event API} for binding plugin reactions
   */
  default void registerReactions() {}
}
