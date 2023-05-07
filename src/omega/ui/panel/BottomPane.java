/**
 * The BottomPane
 * Copyright (C) 2021 Omega UI
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package omega.ui.panel;

import omega.Screen;
import omega.io.IconManager;
import omegaui.component.RTextField;
import omegaui.component.TextComp;
import omegaui.listener.BoundsListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class BottomPane extends JPanel {
    private Screen screen;
    public TextComp messageComp;
    public TextComp searchComp;
    public TextComp logComp;
    public TextComp focusComp;
    public RTextField jumpField;

    public LinkedList<BoundsListener> boundListeners = new LinkedList<>();

    private Runnable r = () -> {
    };
    private Runnable logAction = null;

    public BottomPane(Screen screen) {
        super(null);
        this.screen = screen;
        setBackground(back2);
        setPreferredSize(new Dimension(100, 25));
        init();
    }

    public void init() {
        messageComp = new TextComp("Status of any process running will appear here!", TOOLMENU_COLOR1_SHADE, back2, glow, null) {
            @Override
            public void draw(Graphics2D g) {
                if (image != null) {
                    g.drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH), 2, getHeight() / 2 - h / 2, w, h, null);
                }
            }
        };
        messageComp.setFont(PX14);
        messageComp.alignX = 15;
        messageComp.w = 20;
        messageComp.h = 20;
        messageComp.setHighlightColor(TOOLMENU_COLOR1);
        messageComp.setPreferredSize(new Dimension(100, 25));
        messageComp.setArc(0, 0);
        messageComp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1 && e.getClickCount() == 2)
                    r.run();
            }
        });
        add(messageComp);

        searchComp = new TextComp(IconManager.fluentsearchImage, 20, 20, TOOLMENU_COLOR1_SHADE, back1, glow, () -> Screen.getProjectFile().getSearchWindow().setVisible(true));
        searchComp.setArc(0, 0);
        add(searchComp);

        focusComp = new TextComp(IconManager.fluentfocusImage, 20, 20, TOOLMENU_COLOR1_SHADE, back1, glow, this::toggleFocusMode);
        focusComp.setArc(0, 0);
        add(focusComp);

        logComp = new TextComp("Processes", TOOLMENU_COLOR4_SHADE, back1, glow, () -> {
            if (logAction == null)
                screen.getOperationPanel().setVisible(!screen.getOperationPanel().isVisible());
            else
                logAction.run();
        }) {
            @Override
            public void draw(Graphics2D g) {
                if (image != null) {
                    g.drawImage(image.getScaledInstance(w, h, Image.SCALE_SMOOTH), 5, getHeight() / 2 - h / 2, w, h, null);
                }
            }
        };
        logComp.setFont(PX14);
        logComp.setArc(0, 0);
        logComp.image = IconManager.fluentlogImage;
        logComp.w = 20;
        logComp.h = 20;
        logComp.alignX = 30;
        add(logComp);

        jumpField = new RTextField("Goto Line", "", TOOLMENU_COLOR2, back2, glow);
        jumpField.setFont(PX14);
        jumpField.setArc(0, 0);
        jumpField.addActionListener((e) -> {
            if (!jumpField.hasText())
                return;
            String text = jumpField.getText();
            for (char c : text.toCharArray()) {
                if (!Character.isDigit(c))
                    return;
            }
            int line = Integer.parseInt(text);
            String code = Screen.getScreen().getCurrentEditor() != null ? Screen.getScreen().getCurrentEditor().getText() : "";
            if (code.equals(""))
                return;
            int pos = 0;
            for (char c : code.toCharArray()) {
                if (line <= 0)
                    break;
                if (c == '\n')
                    line--;
                pos++;
            }
            Screen.getScreen().getCurrentEditor().setCaretPosition(pos - 1);
        });
        add(jumpField);


        putAnimationLayer(searchComp, getImageSizeAnimationLayer(25, 5, false), ACTION_MOUSE_ENTERED);
        putAnimationLayer(focusComp, getImageSizeAnimationLayer(25, 5, false), ACTION_MOUSE_ENTERED);
    }

    public void toggleFocusMode() {
        screen.setFocusMode(!screen.isFocusMode());
        focusComp.image = screen.isFocusMode() ? IconManager.fluentnormalScreenImage : IconManager.fluentfocusImage;
        focusComp.repaint();
    }

    @Override
    public void layout() {
        messageComp.setBounds(0, 0, getWidth() - 300 - 60 + 70, 25);
        searchComp.setBounds(getWidth() - 300 - 60 + 70, 0, 30, 25);
        focusComp.setBounds(getWidth() - 300 - 30 + 70, 0, 30, 25);
        logComp.setBounds(getWidth() - 300 + 70, 0, 130, 25);
        jumpField.setBounds(getWidth() - 170 + 70, 0, 100, 25);
        boundListeners.forEach(bL -> bL.onLayout(this));
        super.layout();
    }

    public void addBoundsListener(BoundsListener listener) {
        boundListeners.add(listener);
    }

    public boolean removeBoundsListener(BoundsListener listener) {
        return boundListeners.remove(listener);
    }

    public void setShowLogAction(Runnable action) {
        this.logAction = action;
        logComp.setText(action == null ? "Processes" : "Show Logs");
    }

    public void setMessage(String text, BufferedImage image) {
        messageComp.image = image;
        messageComp.alignX = image != null ? 35 : 15;
        messageComp.setText(text);
    }

    public void setMessage(String text, BufferedImage image, String... highlightTexts) {
        messageComp.image = image;
        messageComp.alignX = image != null ? 35 : 15;
        messageComp.addHighlightText(highlightTexts);
        messageComp.setText(text);
    }

    public void setDoubleClickAction(Runnable r) {
        this.r = r;
    }
}

