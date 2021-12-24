/**
  * Plugins View
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

package omega.plugin.management;

import static omega.comp.Animations.*;
import static omega.utils.UIManager.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import omega.Screen;
import omega.comp.FlexPanel;
import omega.comp.TextComp;
import omega.plugin.Plugin;
import omega.plugin.PluginCategory;
import omega.utils.IconManager;

public class PluginsView extends JDialog {

  public PluginManager pluginManager;

  public TextComp iconComp;
  public TextComp titleComp;
  public TextComp closeComp;

  public TextComp statusComp;

  public FlexPanel mainPanel;
  public FlexPanel listPanel;
  public FlexPanel contentPanel;

  public JScrollPane listScrollPane;
  public JPanel listContentPanel;

  public LinkedList<LocalPluginComp> localPluginComps = new LinkedList<>();

  /*
   * Detailed Plugin View Components
   */

  public TextComp nameComp;
  public TextComp descriptionComp;
  public TextComp licenseComp;
  public TextComp authorComp;
  public TextComp categoryComp;

  public TextComp leftComp;
  public TextComp rightComp;
  public TextComp screenshotComp;

  public LocalPluginComp currentComp;

  public PluginsView(Screen screen, PluginManager pluginManager) {
    super(screen, false);
    this.pluginManager = pluginManager;

    setTitle("Manage Installed Plugins");
    setUndecorated(true);
    setSize(843, 580);
    setLocationRelativeTo(null);
    setResizable(false);
    JPanel panel = new JPanel(null);
    panel.setBackground(back2);
    setContentPane(panel);
    init();
  }

  public void init() {
    iconComp =
      new TextComp(IconManager.ideImage64, 25, 25, back2, back2, back2, null);
    iconComp.setBounds(0, 0, 30, 30);
    iconComp.setClickable(false);
    iconComp.setArc(0, 0);
    add(iconComp);

    titleComp =
      new TextComp("Manage Installed Plugins", back2, back2, glow, null);
    titleComp.setBounds(30, 0, getWidth() - 60, 30);
    titleComp.setFont(PX14);
    titleComp.setArc(0, 0);
    titleComp.setClickable(false);
    titleComp.attachDragger(this);
    add(titleComp);

    closeComp =
      new TextComp(
        IconManager.fluentcloseImage,
        20,
        20,
        back2,
        c2,
        TOOLMENU_COLOR2_SHADE,
        this::dispose
      );
    closeComp.setBounds(getWidth() - 30, 0, 30, 30);
    closeComp.setArc(0, 0);
    add(closeComp);

    statusComp = new TextComp("", back1, back1, glow, null);
    statusComp.setBounds(0, getHeight() - 25, getWidth(), 25);
    statusComp.setArc(0, 0);
    statusComp.setFont(PX14);
    statusComp.setClickable(false);
    statusComp.alignX = 5;
    add(statusComp);

    setStatus(null);

    mainPanel =
      new FlexPanel(null, c2, null) {
        String hint = "None Plugins are Installed!";

        @Override
        public void paint(Graphics graphics) {
          if (localPluginComps.isEmpty()) {
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
            g.setColor(c2);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), arcX, arcY);
            g.setColor(TOOLMENU_COLOR2);
            g.setFont(PX14);
            g.drawString(
              hint,
              getWidth() / 2 - g.getFontMetrics().stringWidth(hint) / 2,
              getHeight() /
              2 -
              g.getFontMetrics().getHeight() /
              2 +
              g.getFontMetrics().getAscent() -
              g.getFontMetrics().getDescent() +
              1
            );
          } else super.paint(graphics);
        }
      };
    mainPanel.setBounds(5, 35, getWidth() - 10, getHeight() - 70);
    mainPanel.setArc(10, 10);
    add(mainPanel);

    listPanel = new FlexPanel(null, back1, null);
    listPanel.setBounds(
      5,
      5,
      mainPanel.getWidth() / 3 - 5,
      mainPanel.getHeight() - 10
    );
    listPanel.setArc(10, 10);
    mainPanel.add(listPanel);

    contentPanel = new FlexPanel(null, back1, null);
    contentPanel.setBounds(
      mainPanel.getWidth() / 3 + 5,
      5,
      mainPanel.getWidth() - mainPanel.getWidth() / 3 - 10,
      mainPanel.getHeight() - 10
    );
    contentPanel.setArc(10, 10);
    mainPanel.add(contentPanel);

    listScrollPane = new JScrollPane(listContentPanel = new JPanel(null));
    listScrollPane.setBounds(
      5,
      5,
      listPanel.getWidth() - 10,
      listPanel.getHeight() - 10
    );
    listScrollPane.setBorder(null);
    listContentPanel.setBackground(back1);
    listPanel.add(listScrollPane);

    //Initializing Detailed Plugin View Components

    nameComp = new TextComp("", back1, back1, glow, null);
    nameComp.setBounds(5, 5, contentPanel.getWidth() - 10, 30);
    nameComp.setFont(PX14);
    nameComp.setClickable(false);
    nameComp.setPaintTextGradientEnabled(true);
    nameComp.setGradientColor(TOOLMENU_COLOR5);
    nameComp.setArc(0, 0);
    nameComp.alignX = 5;
    contentPanel.add(nameComp);

    descriptionComp = new TextComp("", back1, back1, TOOLMENU_COLOR3, null);
    descriptionComp.setBounds(5, 40, contentPanel.getWidth() - 10, 25);
    descriptionComp.setFont(PX14);
    descriptionComp.setClickable(false);
    descriptionComp.setArc(0, 0);
    descriptionComp.alignX = 5;
    contentPanel.add(descriptionComp);

    authorComp = new TextComp("", back1, back1, TOOLMENU_COLOR3, null);
    authorComp.setBounds(5, 70, contentPanel.getWidth() - 10, 25);
    authorComp.setArc(0, 0);
    authorComp.setFont(PX14);
    authorComp.setClickable(false);
    authorComp.alignX = 5;
    contentPanel.add(authorComp);

    licenseComp = new TextComp("", back1, back1, TOOLMENU_COLOR1, null);
    licenseComp.setBounds(5, 100, contentPanel.getWidth() - 10, 25);
    licenseComp.setFont(PX14);
    licenseComp.setArc(0, 0);
    licenseComp.setClickable(false);
    licenseComp.alignX = 5;
    licenseComp.setPaintTextGradientEnabled(true);
    licenseComp.setGradientColor(TOOLMENU_COLOR4);
    contentPanel.add(licenseComp);

    categoryComp = new TextComp("", back1, back1, glow, null);
    categoryComp.setBounds(5, 130, getWidth() - 10, 25);
    categoryComp.setFont(UBUNTU_PX12);
    categoryComp.setArc(0, 0);
    categoryComp.setClickable(false);
    categoryComp.setPaintTextGradientEnabled(true);
    categoryComp.setGradientColor(glow);
    categoryComp.alignX = 5;
    contentPanel.add(categoryComp);

    leftComp =
      new TextComp("<", TOOLMENU_COLOR5_SHADE, back2, glow, this::moveLeft);
    leftComp.setBounds(
      2,
      160 + (contentPanel.getHeight() - 160) / 2 - 30 / 2,
      30,
      30
    );
    leftComp.setFont(PX22);
    leftComp.setArc(2, 2);
    leftComp.setVisible(false);
    contentPanel.add(leftComp);

    rightComp =
      new TextComp(">", TOOLMENU_COLOR5_SHADE, back2, glow, this::moveRight);
    rightComp.setBounds(
      contentPanel.getWidth() - 2 - 30,
      160 + (contentPanel.getHeight() - 160) / 2 - 30 / 2,
      30,
      30
    );
    rightComp.setFont(PX22);
    rightComp.setArc(2, 2);
    rightComp.setVisible(false);
    contentPanel.add(rightComp);

    screenshotComp = new TextComp("", back1, back1, TOOLMENU_COLOR5, null);
    screenshotComp.setBounds(
      35,
      160 +
      (contentPanel.getHeight() - 160) /
      2 -
      (contentPanel.getHeight() - 200) /
      2,
      contentPanel.getWidth() - 70,
      contentPanel.getHeight() - 200
    );
    screenshotComp.setFont(PX14);
    screenshotComp.setArc(0, 0);
    screenshotComp.setClickable(false);
    screenshotComp.w = screenshotComp.getWidth();
    screenshotComp.h = screenshotComp.getHeight();
    contentPanel.add(screenshotComp);
  }

  public void moveLeft() {
    if (currentComp.screenshots.isEmpty()) return;
    if (currentComp.pointer - 1 >= 0) currentComp.pointer--;
    screenshotComp.image = currentComp.screenshots.get(currentComp.pointer);
    screenshotComp.repaint();
  }

  public void moveRight() {
    if (currentComp.screenshots.isEmpty()) return;
    if (
      currentComp.pointer + 1 < currentComp.screenshots.size()
    ) currentComp.pointer++;
    screenshotComp.image = currentComp.screenshots.get(currentComp.pointer);
    screenshotComp.repaint();
  }

  public void doPostInit() {
    if (pluginManager.plugins.isEmpty()) return;
    new Thread(() -> {
      genView();
    })
      .start();
  }

  public void genView() {
    if (!localPluginComps.isEmpty()) return;
    int block = 0;

    for (Plugin plugin : pluginManager.plugins) {
      LocalPluginComp comp = new LocalPluginComp(
        this,
        plugin,
        listScrollPane.getWidth(),
        60
      );
      comp.setLocation(0, block);
      listContentPanel.add(comp);
      localPluginComps.add(comp);

      block += 60;
    }

    listContentPanel.setPreferredSize(
      new Dimension(listScrollPane.getWidth(), block)
    );
    layout();
    repaint();

    new Thread(() -> {
      setStatus("Loading Plugin Icons ...");
      localPluginComps.forEach(comp -> {
        comp.loadIcon();
      });
      setStatus(localPluginComps.size() + " Plugin(s) currently Installed!");
      genView(localPluginComps.get(0));
    })
      .start();
  }

  public void genView(LocalPluginComp comp) {
    this.currentComp = comp;

    nameComp.color3 =
      PluginCategory.getSuitableColor(comp.plugin.getPluginCategory());
    nameComp.setText(comp.plugin.getName() + " " + comp.plugin.getVersion());

    descriptionComp.setText(comp.plugin.getDescription());

    authorComp.setText("Developed By " + comp.plugin.getAuthor());

    licenseComp.setText("Licensed Under " + comp.plugin.getLicense());

    categoryComp.color3 = nameComp.color3;
    categoryComp.setText(
      "Available Under Category of " +
      (
        comp.plugin.getPluginCategory().equals("")
          ? "All"
          : comp.plugin.getPluginCategory().toUpperCase()
      )
    );
    categoryComp.repaint();

    new Thread(() -> {
      if (comp.screenshots == null) {
        screenshotComp.image = null;
        try {
          if (!comp.plugin.getScreenshots().isEmpty()) {
            comp.screenshots = new LinkedList<>();
            for (int i = 0; i < comp.plugin.getScreenshots().size(); i++) {
              screenshotComp.setText(
                "Fetching Screenshots ... " +
                (i + 1) +
                " of " +
                comp.plugin.getScreenshots().size()
              );
              comp.screenshots.add(
                ImageIO.read(comp.plugin.getScreenshots().get(i))
              );
            }
            screenshotComp.image = comp.screenshots.get(comp.pointer = 0);
            screenshotComp.setText("");
            leftComp.setVisible(true);
            rightComp.setVisible(true);
          } else {
            screenshotComp.image = null;
            leftComp.setVisible(false);
            rightComp.setVisible(false);
            screenshotComp.setText("No Screenshots Available");
          }
        } catch (Exception e) {
          screenshotComp.setText("Unable to Fetch Screenshots!");
          screenshotComp.image = null;
          leftComp.setVisible(false);
          rightComp.setVisible(false);
          System.err.println(
            "Unable to load screenshot of plugin " +
            comp.plugin.getName() +
            " at Plugin Manager!"
          );
          e.printStackTrace();
        }
      } else {
        if (!comp.screenshots.isEmpty()) {
          screenshotComp.image = comp.screenshots.get(comp.pointer);
          screenshotComp.repaint();
          leftComp.setVisible(true);
          rightComp.setVisible(true);
        } else {
          screenshotComp.image = null;
          leftComp.setVisible(false);
          rightComp.setVisible(false);
          screenshotComp.setText("No Screenshots Available");
        }
      }
    })
      .start();
  }

  public synchronized void setStatus(String text) {
    if (text == null) statusComp.setText(
      "Copyright 2021 Omega UI. All Right Reserved."
    ); else statusComp.setText(text);
  }

  @Override
  public void setVisible(boolean value) {
    super.setVisible(value);
    if (value) {
      doPostInit();
    }
  }

  @Override
  public void setSize(int width, int height) {
    super.setSize(width, height);
    setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
  }
}
