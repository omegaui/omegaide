/**
 * RunPanel
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
import omega.io.ShellTokenMaker;
import omega.io.UIManager;
import omega.ui.component.Editor;
import omegaui.component.FlexPanel;
import omegaui.component.TextComp;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class RunPanel extends JPanel {
    public FlexPanel actionPanel;
    public TextComp runComp;
    public TextComp clearComp;
    public TextComp killComp;

    public FlexPanel runTextAreaPanel;
    public JScrollPane scrollPane;
    public RunTextArea runTextArea;

    public boolean logMode = false;

    public Process process;
    public PrintWriter writer;

    public RunPanel() {
        super(null);
        setBackground(c2);
        init();
    }

    public void init() {
        actionPanel = new FlexPanel(null, back1, null);
        actionPanel.setArc(10, 10);
        add(actionPanel);

        runComp = new TextComp(IconManager.fluentrunImage, 20, 20, "Re-Run", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, null);
        actionPanel.add(runComp);

        clearComp = new TextComp(IconManager.fluentclearImage, 20, 20, "Clear Terminal", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::clearTerminal);
        actionPanel.add(clearComp);

        killComp = new TextComp(IconManager.fluentcloseImage, 15, 15, "Kill Process", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, this::killProcess);
        actionPanel.add(killComp);

        runTextAreaPanel = new FlexPanel(null, back1, null);
        runTextAreaPanel.setArc(10, 10);
        scrollPane = new JScrollPane(runTextArea = new RunTextArea());
        runTextAreaPanel.add(scrollPane);
        add(runTextAreaPanel);

        putAnimationLayer(runComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(clearComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(killComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
    }

    public void launchAsTerminal(Runnable r, BufferedImage image, String toolTip) {
        runComp.setRunnable(r);
        runComp.image = image;
        runComp.setToolTipText(toolTip);
        repaint();
    }

    public void clearTerminal() {
        runTextArea.setText("");
    }

    public void killProcess() {
        if (process != null && process.isAlive()) {
            try {
                process.destroyForcibly();
                writer.close();
            } catch (Exception e) {

            }
        }
    }

    public void setProcess(Process process) {
        this.process = process;
        if (!logMode)
            writer = new PrintWriter(process.getOutputStream());
    }

    public void setLogMode(boolean logMode) {
        this.logMode = logMode;
        runTextArea.removeKeyListener(runTextArea.getKeyListeners()[0]);
        actionPanel.setVisible(true);
    }

    public void print(String text) {
        runTextArea.append(text + "\n");
        layout();
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    public void printText(String text) {
        print(text);
    }

    public void relocate() {
        if (!logMode) {
            actionPanel.setBounds(5, 5, 30, getHeight() - 10);
            runComp.setBounds(3, 5, 25, 25);
            clearComp.setBounds(3, 32, 25, 25);
            killComp.setBounds(3, 60, 25, 25);
        }
        if (logMode)
            runTextAreaPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
        else
            runTextAreaPanel.setBounds(40, 5, getWidth() - 50, getHeight() - 10);
        scrollPane.setBounds(5, 5, runTextAreaPanel.getWidth() - 10, runTextAreaPanel.getHeight() - 10);
    }

    @Override
    public void layout() {
        relocate();
        super.layout();
    }

    public class RunTextArea extends RSyntaxTextArea {
        public static volatile boolean ctrl;
        public static volatile boolean l;

        public RunTextArea() {
            Editor.getTheme().apply(this);
            ShellTokenMaker.apply(this);
            setFont(new Font(UIManager.terminalFontName, UIManager.terminalFontState, UIManager.terminalFontSize));
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code == KeyEvent.VK_CONTROL)
                        ctrl = true;
                    else if (code == KeyEvent.VK_L)
                        l = true;

                    performShortcuts(e);
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code == KeyEvent.VK_CONTROL)
                        ctrl = false;
                    else if (code == KeyEvent.VK_L)
                        l = false;
                }
            });
        }

        public void performShortcuts(KeyEvent e) {
            if (process == null)
                return;
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (writer == null || !process.isAlive())
                    e.consume();

                String text = getText();
                text = text.substring(0, getCaretPosition());
                text = text.substring(text.lastIndexOf('\n') + 1);
                if (Screen.onWindows())
                    append("\n");
                writer.println(text);
                writer.flush();
            }
            if (ctrl && l) {
                clearTerminal();
                ctrl = false;
                l = false;
                e.consume();
            }
        }
    }
}

