/*
 * JDKSelectionDialog
 * Copyright (C) 2022 Omega UI

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

package omega.instant.support.java.misc;

import omega.Screen;
import omega.instant.support.java.management.JDKManager;
import omega.io.AppDataManager;
import omega.io.IconManager;
import omegaui.component.TextComp;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.LinkedList;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class JDKSelectionDialog extends JDialog {
    private TextComp titleComp;
    private TextComp closeComp;

    private JPanel panel;
    private JScrollPane scrollPane;

    private LinkedList<TextComp> jdkComps = new LinkedList<>();

    private String selection;

    public JDKSelectionDialog(Screen screen) {
        super(screen, true);
        setTitle("JDK Selection Dialog");
        setUndecorated(true);
        setResizable(false);
        setSize(300, 300);
        setLocationRelativeTo(screen);

        JPanel panel = new JPanel(null);
        panel.setBackground(back2);
        setContentPane(panel);
        setBackground(back2);
        setLayout(null);

        init();
    }

    public void init() {
        titleComp = new TextComp("Select Project JDK", getBackground(), getBackground(), glow, null);
        titleComp.setBounds(0, 0, getWidth() - 30, 30);
        titleComp.setFont(PX14);
        titleComp.setHighlightColor(TOOLMENU_COLOR1);
        titleComp.addHighlightText("Select", "JDK");
        titleComp.setArc(0, 0);
        titleComp.setClickable(false);
        titleComp.attachDragger(this);
        add(titleComp);

        closeComp = new TextComp(IconManager.fluentcloseImage, 20, 20, TOOLMENU_COLOR1_SHADE, getBackground(), getBackground(), this::dispose);
        closeComp.setBounds(getWidth() - 30, 0, 30, 30);
        closeComp.setArc(0, 0);
        add(closeComp);

        putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);

        scrollPane = new JScrollPane(panel = new JPanel(null)) {
            @Override
            public void paint(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (jdkComps.isEmpty()) {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(TOOLMENU_COLOR2);
                    g.setFont(PX14);
                    drawAtCenter("No JDKs Found!", g, scrollPane);
                } else
                    super.paint(g);
            }
        };
        scrollPane.setBackground(getBackground());
        scrollPane.setBorder(null);
        panel.setBackground(getBackground());
        scrollPane.setBounds(0, 35, getWidth(), getHeight() - 40);
        add(scrollPane);
    }

    public String makeChoice() {
        jdkComps.forEach(panel::remove);
        jdkComps.clear();

        selection = null;

        File jdkRootDir = new File(AppDataManager.getPathToJava());

        File[] F = jdkRootDir.listFiles();
        if (F == null || F.length == 0) {
            setVisible(true);
            return selection;
        }

        int blockY = 5;
        for (File dir : F) {
            if (dir.isDirectory()) {
                if (isValidJDKDir(dir)) {
                    int version = getVersion(dir);
                    TextComp comp = new TextComp(dir.getName(), dir.getAbsolutePath(), c2, getColorShade(version), getTextColor(version), () -> select(dir));
                    comp.setBounds(5, blockY, getWidth() - 10, 25);
                    comp.setFont(PX14);
                    comp.setArc(5, 5);
                    panel.add(comp);
                    jdkComps.add(comp);
                    blockY += 30;
                }
            }
        }

        panel.setPreferredSize(new Dimension(scrollPane.getWidth(), blockY));

        setVisible(true);

        return selection;
    }

    public int getVersion(File jdkDir) {
        return JDKManager.calculateVersion(jdkDir);
    }

    public Color getColorShade(int version) {
        return version <= 8 ? TOOLMENU_COLOR2_SHADE : TOOLMENU_COLOR1_SHADE;
    }

    public Color getTextColor(int version) {
        return version <= 8 ? TOOLMENU_COLOR2 : TOOLMENU_COLOR1;
    }

    public boolean isValidJDKDir(File dir) {
        return JDKManager.isJDK(dir);
    }

    public void select(File jdkDir) {
        selection = jdkDir.getAbsolutePath();
        dispose();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
    }
}
