/**
* Flexible Panel
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class FlexPanel extends JComponent {

  public int arcX = 40;
  public int arcY = 40;
  private Color accentColor;
  private Color borderColor = Color.BLACK;
  private GradientPaint paint;
  private boolean paintGradientEnabled = false;
  private boolean paintBorder = false;

  public FlexPanel(LayoutManager layout, Color background, Color accentColor) {
    setLayout(layout);
    setBackground(background);
    this.accentColor = accentColor;
  }

  public boolean isPaintGradientEnabled() {
    return paintGradientEnabled;
  }

  public void setPaintGradientEnabled(boolean paintGradientEnabled) {
    this.paintGradientEnabled = paintGradientEnabled;
    repaint();
  }

  public Color getAccentColor() {
    return accentColor;
  }

  public void setAccentColor(Color accentColor) {
    this.accentColor = accentColor;
  }

  private void setGradient() {
    paint =
      new GradientPaint(
        0,
        0,
        getBackground(),
        getWidth(),
        getHeight(),
        accentColor == null ? getBackground() : accentColor
      );
  }

  public boolean isPaintBorder() {
    return paintBorder;
  }

  public void setPaintBorder(boolean paintBorder) {
    this.paintBorder = paintBorder;
    repaint();
  }

  public java.awt.Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(java.awt.Color borderColor) {
    this.borderColor = borderColor;
  }

  public void setArc(int x, int y) {
    arcX = x;
    arcY = y;
  }

  @Override
  public void paint(Graphics graphics) {
    if (paintGradientEnabled) setGradient();
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
    if (paintGradientEnabled) g.setPaint(paint); else g.setColor(
      getBackground()
    );
    g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
    if (paintBorder) {
      g.setColor(borderColor);
      g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcX, arcY);
    }
    super.paint(g);
  }
}
