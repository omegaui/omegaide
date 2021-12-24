/**
* omega.comp.Comp - A custom component that shares some UI behaviour with omega.comp.TextComp.
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

package omega.comp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

public class Comp extends JComponent {

  /*
   * A boolean value to check whether the mouse has entered the component or not.
   */
  private volatile boolean enter;

  /*
   * A boolean value to check whether the mouse has been pressed or not.
   */
  private volatile boolean press;

  /*
   * A boolean variable to store whether the component is clickable or not.
   */
  private volatile boolean clickable = true;

  /*
   * A boolean variable to check whether toggle is enabled or not.
   */
  private volatile boolean toggleON;

  /*
   * A String variable to store the text to be displayed.
   */
  private String text;

  /*
   * A String variable to store original value of the variable text.
   */
  private String originalText;

  /*
   * Variable to store the inactive text when the toggle is off.
   */
  private String inactiveText;

  /*
   * Variable to store the active text when the toggle is on.
   */
  private String activeText;

  /*
   * Color used when mouse hovers.
   */
  public Color color1;

  /*
   * Color used for drawing background.
   */
  public Color color2;

  /*
   * Color used for drawing foreground.
   */
  public Color color3;

  /*
   * Runnable to do an action when clicked.
   */
  public Runnable runnable;

  /*
   * Runnable to do an action when clicked.
   */
  public Runnable runnable_temp;

  /*
   * Left arrow component for navigation.
   */
  public TextComp leftComp;

  /*
   * Right arrow component for navigation.
   */
  public TextComp rightComp;

  /*
   * Stores the x-axis arc size.
   */
  public int arcX = 40;

  /*
   * Stores the y-axis arc size.
   */
  public int arcY = 40;

  /*
   * Constructor that takes all required arguments.
   */
  public Comp(
    String text,
    Color color1,
    Color color2,
    Color color3,
    Runnable runnable
  ) {
    this.text = text;
    this.originalText = text;
    this.color1 = color1;
    this.color2 = color2;
    this.color3 = color3;
    this.runnable = runnable;

    addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
          if (!clickable) return;
          enter = true;
          repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
          if (!clickable) return;
          enter = false;
          repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
          if (!clickable) return;
          press = true;
          repaint();
          if (
            Comp.this.runnable != null && e.getButton() == 1
          ) Comp.this.runnable.run();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          if (!clickable) return;
          press = false;
          repaint();
        }
      }
    );
  }

  /*
   * Setter for text.
   */
  public void setText(String text) {
    this.text = text;
    repaint();
  }

  /*
   * Setter for runnable.
   */
  public void setAction(Runnable runnable) {
    this.runnable = runnable;
  }

  /*
   * Setter for clickable.
   */
  public void setClickable(boolean value) {
    this.clickable = value;
  }

  /*
   * Setter for toggleON.
   */
  public void setToggle(boolean toggle) {
    this.toggleON = toggle;
    setText(this.toggleON ? activeText : inactiveText);
  }

  /*
   * Setter for arcX and arcY.
   */
  public void setArc(int x, int y) {
    this.arcX = x;
    this.arcY = y;
    repaint();
  }

  /*
   * Generates a Toggle.
   */
  public void createToggle(
    boolean toggleON,
    String activeText,
    String inactiveText,
    ToggleListener tL
  ) {
    this.toggleON = toggleON;
    this.inactiveText = inactiveText;
    this.activeText = activeText;
    setText(this.toggleON ? activeText : inactiveText);
    runnable_temp = runnable;
    runnable =
      () -> {
        this.toggleON = !this.toggleON;
        setText(this.toggleON ? activeText : inactiveText);
        tL.toggle(this.toggleON);
      };
  }

  /*
   * Removes the generated Toggle.
   */
  public void removeToggle() {
    runnable = runnable_temp;
  }

  /*
   * Generates the left Arrow.
   */
  public void createLeftArrow(int x, int y, int w, int h, Runnable runnable) {
    leftComp = new TextComp("<", color1, color2, color3, runnable);
    leftComp.setBounds(x, y, w, h);
    leftComp.setFont(getFont());
    add(leftComp);
    repaint();
  }

  /*
   * Removes the left Arrow.
   */
  public void removeLeftArrow() {
    if (leftComp != null) {
      remove(leftComp);
      repaint();
    }
  }

  /*
   * Generates the right Arrow.
   */
  public void createRightArrow(int x, int y, int w, int h, Runnable runnable) {
    rightComp = new TextComp(">", color1, color2, color3, runnable);
    rightComp.setBounds(x, y, w, h);
    rightComp.setFont(getFont());
    add(rightComp);
    repaint();
  }

  /*
   * Removes the right Arrow.
   */
  public void removeRightArrow() {
    if (rightComp != null) {
      remove(rightComp);
      repaint();
    }
  }

  /*
   * the paint method.
   */
  @Override
  public void paint(Graphics graphics) {
    Graphics2D g = (Graphics2D) graphics;
    g.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY
    );
    g.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );
    g.setRenderingHint(
      RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    );
    g.setColor(color1);
    g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
    g.setColor(Color.WHITE);
    g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, arcX, arcY);
    g.setColor(color2);
    g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, arcX, arcY);
    g.setColor(color3);
    g.setFont(getFont());
    int textLength = g.getFontMetrics().stringWidth(text);
    g.drawString(
      text,
      getWidth() / 2 - textLength / 2,
      getHeight() /
      2 -
      g.getFontMetrics().getHeight() /
      2 +
      g.getFontMetrics().getAscent()
    );
    if (enter) {
      g.setColor(color3);
      g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
      g.setColor(Color.WHITE);
      g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, arcX, arcY);
      g.setColor(color2);
      g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, arcX, arcY);
      g.setColor(color3);
      g.drawString(
        text,
        getWidth() / 2 - textLength / 2,
        getHeight() /
        2 -
        g.getFontMetrics().getHeight() /
        2 +
        g.getFontMetrics().getAscent()
      );
    }
    if (press) {
      paintPress(g, textLength);
    }
    super.paint(graphics);
  }

  /*
   * Paints press event.
   */
  public void paintPress(Graphics2D g, int textLength) {
    g.setColor(color1);
    g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
    g.setColor(Color.WHITE);
    g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, arcX, arcY);
    g.setColor(color2);
    g.fillRoundRect(6, 6, getWidth() - 11, getHeight() - 11, arcX, arcY);
    g.setColor(color3);
    g.drawString(
      text,
      getWidth() / 2 - textLength / 2,
      getHeight() /
      2 -
      g.getFontMetrics().getHeight() /
      2 +
      g.getFontMetrics().getAscent()
    );
  }
}
