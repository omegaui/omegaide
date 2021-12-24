/**
  * NotificationPopup
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
package omega.popup;

import static omega.comp.Animations.*;
import static omega.utils.UIManager.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import omega.comp.FlexPanel;
import omega.comp.TextComp;

public class NotificationPopup extends JDialog {

  private static Dimension MINIMUM_SIZE = new Dimension(250, 120);

  private TextComp closeComp;
  private TextComp dialogImageComp;
  private TextComp titleComp;
  private TextComp imageComp;
  private TextComp messageComp;
  private TextComp footerComp;

  private int offsetWidth = 20;
  private int offsetHeight = 50;

  public NotificationPopup(Frame f) {
    super(f, false);
    setUndecorated(true);
    FlexPanel panel = new FlexPanel(null, back2, c2);
    panel.setArc(20, 20);
    panel.setPaintGradientEnabled(true);
    setContentPane(panel);
    setLayout(null);
    setBackground(back2);
    setSize(MINIMUM_SIZE);
    setType(Type.UTILITY);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    init();
  }

  public void init() {
    closeComp =
      new TextComp(
        "x",
        TOOLMENU_COLOR4_SHADE,
        back2,
        TOOLMENU_COLOR2,
        this::dispose
      );
    closeComp.setFont(PX12);
    closeComp.setArc(6, 6);
    add(closeComp);

    dialogImageComp = new TextComp(null, 25, 25, ALPHA, ALPHA, ALPHA, null);
    dialogImageComp.setBounds(10, 7, 25, 25);
    add(dialogImageComp);

    titleComp = new TextComp("", ALPHA, ALPHA, glow, null);
    titleComp.alignX = 10;
    titleComp.setClickable(false);
    titleComp.setFont(PX14);
    titleComp.setArc(0, 0);
    titleComp.attachDragger(this);
    add(titleComp);

    imageComp = new TextComp(null, 20, 20, c1, ALPHA, glow, null);
    add(imageComp);

    messageComp = new TextComp("", ALPHA, ALPHA, glow, null);
    messageComp.alignX = 10;
    messageComp.setClickable(false);
    messageComp.setFont(PX14);
    messageComp.setArc(0, 0);
    add(messageComp);

    footerComp = new TextComp("", ALPHA, ALPHA, glow, null);
    footerComp.alignX = 10;
    footerComp.setClickable(false);
    footerComp.setFont(PX14);
    footerComp.setArc(0, 0);
    add(footerComp);

    putAnimationLayer(
      dialogImageComp,
      getImageSizeAnimationLayer(25, -5, true),
      ACTION_MOUSE_ENTERED
    );
    putAnimationLayer(
      imageComp,
      getImageSizeAnimationLayer(25, 5, true),
      ACTION_MOUSE_ENTERED
    );
  }

  public NotificationPopup title(String text) {
    titleComp.setText(text);
    setTitle(text);
    return this;
  }

  public NotificationPopup title(String text, Color textColor) {
    titleComp.setText(text);
    titleComp.color3 = textColor;
    setTitle(text);
    return this;
  }

  public NotificationPopup message(String text) {
    messageComp.setText(text);
    return this;
  }

  public NotificationPopup message(String text, Color textColor) {
    message(text);
    messageComp.color3 = textColor;
    return this;
  }

  public NotificationPopup shortMessage(String text) {
    footerComp.setText(text);
    return this;
  }

  public NotificationPopup shortMessage(String text, Color textColor) {
    shortMessage(text);
    footerComp.color3 = textColor;
    return this;
  }

  public NotificationPopup size(int width, int height) {
    setSize(width, height);
    return this;
  }

  public NotificationPopup iconButton(BufferedImage image, Runnable action) {
    imageComp.image = image;
    imageComp.setRunnable(action);
    return this;
  }

  public NotificationPopup iconButton(
    BufferedImage image,
    Runnable action,
    String toolTip
  ) {
    iconButton(image, action);
    imageComp.setToolTipText(toolTip);
    return this;
  }

  public NotificationPopup dialogIcon(BufferedImage image) {
    dialogImageComp.image = image;
    return this;
  }

  public NotificationPopup locateOnBottomLeft() {
    setLocation(
      getOwner().getX() + getOwner().getWidth() - getWidth() - this.offsetWidth,
      getOwner().getY() +
      getOwner().getHeight() -
      getHeight() -
      this.offsetHeight
    );
    return this;
  }

  public NotificationPopup locateOnBottomLeft(
    int offsetWidth,
    int offsetHeight
  ) {
    this.offsetWidth = offsetWidth;
    this.offsetHeight = offsetHeight;
    setLocation(
      getOwner().getX() + getOwner().getWidth() - getWidth() - this.offsetWidth,
      getOwner().getY() +
      getOwner().getHeight() -
      getHeight() -
      this.offsetHeight
    );
    return this;
  }

  public NotificationPopup build() {
    setShape(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

    //Initializing UI Bounds
    titleComp.setBounds(
      dialogImageComp.image == null ? 10 : 40,
      7,
      getWidth() - 50,
      25
    );
    closeComp.setBounds(getWidth() - 30, 10, 20, 20);
    imageComp.setBounds(10, getHeight() - 40, 30, 30);
    messageComp.setBounds(
      10,
      titleComp.getY() + titleComp.getHeight() + 10,
      getWidth() - 20,
      25
    );
    footerComp.setBounds(
      imageComp.image == null ? 10 : 50,
      getHeight() - 40,
      getWidth() - 100,
      30
    );

    if (imageComp.image == null) {
      remove(imageComp);
    }

    if (dialogImageComp.image == null) {
      remove(dialogImageComp);
    }

    setLocationRelativeTo(getOwner());
    return this;
  }

  public NotificationPopup showIt() {
    setVisible(true);
    return this;
  }

  public static NotificationPopup create(Frame f) {
    return new NotificationPopup(f);
  }
}
