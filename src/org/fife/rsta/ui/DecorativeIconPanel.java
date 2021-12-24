/*
 * 07/30/2011
 *
 * DecorativeIconPanel.java - Displays a small decorative icon beside some
 * other component.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import org.fife.ui.autocomplete.EmptyIcon;

/**
 * A panel that displays an 8x8 decorative icon for a component, such as a
 * text field or combo box.  This can be used to display error icons, warning
 * icons, etc.
 *
 * @author Robert Futrell
 * @version 1.1
 * @see AssistanceIconPanel
 */
public class DecorativeIconPanel extends JPanel {

  /**
   * The width of this icon panel, to help align the components we're
   * listening to with other combo boxes or text fields without a
   * DecorativeIconPanel.
   */
  private static final int DEFAULT_WIDTH = 8;

  private JLabel iconLabel;
  private boolean showIcon;
  private String tip;
  private EmptyIcon emptyIcon;

  /**
   * Constructor.
   */
  public DecorativeIconPanel() {
    this(DEFAULT_WIDTH);
  }

  /**
   * Constructor.
   *
   * @param iconWidth The with of decorative icons that will be added to
   *        this panel.  This is used to ensure the proper amount of padding
   *        when no icon is being shown.
   */
  public DecorativeIconPanel(int iconWidth) {
    setLayout(new BorderLayout());
    emptyIcon = new EmptyIcon(iconWidth);

    iconLabel =
      new JLabel(emptyIcon) {
        @Override
        public String getToolTipText(MouseEvent e) {
          return showIcon ? tip : null;
        }
      };
    iconLabel.setVerticalAlignment(SwingConstants.TOP);
    ToolTipManager.sharedInstance().registerComponent(iconLabel);
    add(iconLabel, BorderLayout.NORTH);
  }

  /**
   * Returns the icon being displayed.
   *
   * @return The icon.
   * @see #setIcon(Icon)
   */
  public Icon getIcon() {
    return iconLabel.getIcon();
  }

  /**
   * Returns whether the icon (if any) is being rendered.
   *
   * @return Whether the icon is being rendered.
   * @see #setShowIcon(boolean)
   */
  public boolean getShowIcon() {
    return showIcon;
  }

  /**
   * Returns the tool tip displayed when the mouse hovers over the icon.
   * If the icon is not being displayed, this parameter is ignored.
   *
   * @return The tool tip text.
   * @see #setToolTipText(String)
   */
  @Override
  public String getToolTipText() {
    return tip;
  }

  /**
   * Paints any child components.  Overridden so the user can explicitly
   * hide the icon.
   *
   * @param g The graphics context.
   * @see #setShowIcon(boolean)
   */
  @Override
  protected void paintChildren(Graphics g) {
    if (showIcon) {
      super.paintChildren(g);
    }
  }

  /**
   * Sets the icon to display.
   *
   * @param icon The new icon to display.
   * @see #getIcon()
   */
  public void setIcon(Icon icon) {
    if (icon == null) {
      icon = emptyIcon;
    }
    iconLabel.setIcon(icon);
  }

  /**
   * Toggles whether the icon should be shown.
   *
   * @param show Whether to show the icon.
   * @see #getShowIcon()
   */
  public void setShowIcon(boolean show) {
    if (show != showIcon) {
      showIcon = show;
      repaint();
    }
  }

  /**
   * Sets the tool tip text to display when the mouse is over the icon.
   * This parameter is ignored if the icon is not being displayed.
   *
   * @param tip The tool tip text to display.
   * @see #getToolTipText()
   */
  @Override
  public void setToolTipText(String tip) {
    this.tip = tip;
  }
}
