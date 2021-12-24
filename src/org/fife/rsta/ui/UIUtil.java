/*
 * 09/08/2005
 *
 * UIUtil.java - Utility methods for org.fife.rsta.ui classes.
 * This library is distributed under a modified BSD license.  See the included
 * RSTAUI.License.txt file for details.
 */
package org.fife.rsta.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

/**
 * Utility methods for <code>org.fife.rsta.ui</code> GUI components.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class UIUtil {

  private static boolean desktopCreationAttempted;
  private static Object desktop;
  private static final Object LOCK_DESKTOP_CREATION = new Object();

  /**
   * A very common border that can be shared across many components.
   */
  private static final Border EMPTY_5_BORDER = BorderFactory.createEmptyBorder(
    5,
    5,
    5,
    5
  );

  /**
   * Private constructor so we cannot instantiate this class.
   */
  private UIUtil() {}

  /**
	 * Attempts to open a web browser to the specified URI.
	 *
	 * @param uri The URI to open.  If this is <code>null</code>, nothing
	          happens and this method returns <code>false</code>.
	 * @return Whether the operation was successful.  This will be
	 *         <code>false</code> on JRE's older than 1.6.
	 * @see #browse(URI)
	 */
  public static boolean browse(String uri) {
    if (uri == null) {
      return false;
    }
    try {
      return browse(new URI(uri));
    } catch (URISyntaxException e) {
      return false;
    }
  }

  /**
	 * Attempts to open a web browser to the specified URI.
	 *
	 * @param uri The URI to open.  If this is <code>null</code>, nothing
	          happens and this method returns <code>false</code>.
	 * @return Whether the operation was successful.  This will be
	 *         <code>false</code> on JRE's older than 1.6.
	 * @see #browse(String)
	 */
  public static boolean browse(URI uri) {
    boolean success = false;

    if (uri != null) {
      Object desktop = getDesktop();
      if (desktop != null) {
        try {
          Method m = desktop.getClass().getDeclaredMethod("browse", URI.class);
          m.invoke(desktop, uri);
          success = true;
        } catch (RuntimeException re) {
          throw re; // Keep FindBugs happy
        } catch (Exception e) {
          // Ignore, just return "false" below.
        }
      }
    }

    return success;
  }

  /**
   * Fixes the orientation of the renderer of a combo box.  I can't believe
   * Swing standard LaFs don't handle this on their own.
   *
   * @param combo The combo box.
   */
  public static void fixComboOrientation(JComboBox<?> combo) {
    ListCellRenderer<?> r = combo.getRenderer();
    if (r instanceof Component) {
      ComponentOrientation o = ComponentOrientation.getOrientation(
        Locale.getDefault()
      );
      ((Component) r).setComponentOrientation(o);
    }
  }

  /**
   * Used by makeSpringCompactGrid.  This is ripped off directly from
   * <code>SpringUtilities.java</code> in the Sun Java Tutorial.
   *
   * @param parent The container whose layout must be an instance of
   *        <code>SpringLayout</code>.
   * @return The spring constraints for the specified component contained
   *         in <code>parent</code>.
   */
  private static SpringLayout.Constraints getConstraintsForCell(
    int row,
    int col,
    Container parent,
    int cols
  ) {
    SpringLayout layout = (SpringLayout) parent.getLayout();
    Component c = parent.getComponent(row * cols + col);
    return layout.getConstraints(c);
  }

  /**
   * Returns the singleton <code>java.awt.Desktop</code> instance, or
   * <code>null</code> if it is unsupported on this platform (or the JRE
   * is older than 1.6).
   *
   * @return The desktop, as an {@link Object}.
   */
  private static Object getDesktop() {
    synchronized (LOCK_DESKTOP_CREATION) {
      if (!desktopCreationAttempted) {
        desktopCreationAttempted = true;

        try {
          Class<?> desktopClazz = Class.forName("java.awt.Desktop");
          Method m = desktopClazz.getDeclaredMethod("isDesktopSupported");

          boolean supported = (Boolean) m.invoke(null);
          if (supported) {
            m = desktopClazz.getDeclaredMethod("getDesktop");
            desktop = m.invoke(null);
          }
        } catch (RuntimeException re) {
          throw re; // Keep FindBugs happy
        } catch (Exception e) {
          // Ignore; keeps desktop as null.
        }
      }
    }

    return desktop;
  }

  /**
   * Returns an empty border of width 5 on all sides.  Since this is a
   * very common border in GUI's, the border returned is a singleton.
   *
   * @return The border.
   */
  public static Border getEmpty5Border() {
    return EMPTY_5_BORDER;
  }

  /**
   * Returns a color to use for "error" text in a text field.  This will
   * pick red for dark-text-on-light-background LookAndFeels, and a
   * brighter color for light-text-on-dark-background LookAndFeels.
   *
   * @return The color to use.
   */
  public static Color getErrorTextForeground() {
    Color defaultFG = UIManager.getColor("TextField.foreground");
    if (
      defaultFG.getRed() >= 160 &&
      defaultFG.getGreen() >= 160 &&
      defaultFG.getBlue() >= 160
    ) {
      return new Color(255, 160, 160);
    }
    return Color.red;
  }

  /**
   * Returns the mnemonic specified by the given key in a resource bundle.
   *
   * @param msg The resource bundle.
   * @param key The key for the mnemonic.
   * @return The mnemonic, or <code>0</code> if not found.
   */
  public static int getMnemonic(ResourceBundle msg, String key) {
    int mnemonic = 0;
    try {
      Object value = msg.getObject(key);
      if (value instanceof String) {
        mnemonic = ((String) value).charAt(0);
      }
    } catch (MissingResourceException mre) {
      // Swallow.  TODO: When we drop 1.4/1.5 support, use containsKey().
    }
    return mnemonic;
  }

  /**
   * Returns the text editor component for the specified combo box.
   *
   * @param combo The combo box.
   * @return The text component.
   */
  public static JTextComponent getTextComponent(JComboBox<?> combo) {
    return (JTextComponent) combo.getEditor().getEditorComponent();
  }

  /**
   * This method is ripped off from <code>SpringUtilities.java</code> found
   * on Sun's Java Tutorial pages.  It takes a component whose layout is
   * <code>SpringLayout</code> and organizes the components it contains into
   * a nice grid.
   * Aligns the first <code>rows</code> * <code>cols</code> components of
   * <code>parent</code> in a grid. Each component in a column is as wide as
   * the maximum preferred width of the components in that column; height is
   * similarly determined for each row.  The parent is made just big enough
   * to fit them all.
   *
   * @param parent The container whose layout is <code>SpringLayout</code>.
   * @param rows The number of rows of components to make in the container.
   * @param cols The number of columns of components to make.
   * @param initialX The x-location to start the grid at.
   * @param initialY The y-location to start the grid at.
   * @param xPad The x-padding between cells.
   * @param yPad The y-padding between cells.
   */
  public static void makeSpringCompactGrid(
    Container parent,
    int rows,
    int cols,
    int initialX,
    int initialY,
    int xPad,
    int yPad
  ) {
    SpringLayout layout;
    try {
      layout = (SpringLayout) parent.getLayout();
    } catch (ClassCastException cce) {
      System.err.println(
        "The first argument to makeCompactGrid " + "must use SpringLayout."
      );
      return;
    }

    //Align all cells in each column and make them the same width.
    Spring x = Spring.constant(initialX);
    for (int c = 0; c < cols; c++) {
      Spring width = Spring.constant(0);
      for (int r = 0; r < rows; r++) {
        width =
          Spring.max(
            width,
            getConstraintsForCell(r, c, parent, cols).getWidth()
          );
      }
      for (int r = 0; r < rows; r++) {
        SpringLayout.Constraints constraints = getConstraintsForCell(
          r,
          c,
          parent,
          cols
        );
        constraints.setX(x);
        constraints.setWidth(width);
      }
      x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
    }

    //Align all cells in each row and make them the same height.
    Spring y = Spring.constant(initialY);
    for (int r = 0; r < rows; r++) {
      Spring height = Spring.constant(0);
      for (int c = 0; c < cols; c++) {
        height =
          Spring.max(
            height,
            getConstraintsForCell(r, c, parent, cols).getHeight()
          );
      }
      for (int c = 0; c < cols; c++) {
        SpringLayout.Constraints constraints = getConstraintsForCell(
          r,
          c,
          parent,
          cols
        );
        constraints.setY(y);
        constraints.setHeight(height);
      }
      y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
    }

    //Set the parent's size.
    SpringLayout.Constraints pCons = layout.getConstraints(parent);
    pCons.setConstraint(SpringLayout.SOUTH, y);
    pCons.setConstraint(SpringLayout.EAST, x);
  }

  /**
   * Returns a button with the specified text.  If another
   * property with name <code>getString(textKey) + ".Mnemonic"</code> is
   * defined, it is used as the mnemonic for the button.
   *
   * @param bundle The resource bundle in which to get the int.
   * @param key The key into the bundle containing the string text value.
   * @return The button.
   */
  public static JButton newButton(ResourceBundle bundle, String key) {
    JButton b = new JButton(bundle.getString(key));
    b.setMnemonic(getMnemonic(bundle, key + ".Mnemonic"));
    return b;
  }

  /**
   * Returns an <code>JLabel</code> with the specified text.  If another
   * property with name <code>getString(textKey) + ".Mnemonic"</code> is
   * defined, it is used as the mnemonic for the label.
   *
   * @param msg The resource bundle.
   * @param key The key into the bundle containing the string text value.
   * @param labelFor The component the label is labeling.
   * @return The <code>JLabel</code>.
   */
  public static JLabel newLabel(
    ResourceBundle msg,
    String key,
    Component labelFor
  ) {
    JLabel label = new JLabel(msg.getString(key));
    String mnemonicKey = key + ".Mnemonic";
    label.setDisplayedMnemonic(getMnemonic(msg, mnemonicKey));
    if (labelFor != null) {
      label.setLabelFor(labelFor);
    }
    return label;
  }
}
