/*
 * TabComp
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

package omega.ui.component;

import omega.Screen;
import omega.io.TabData;
import omega.ui.panel.JetRunPanel;
import omega.ui.panel.RunPanel;
import omega.ui.panel.TabPanel;
import omegaui.component.TextComp;
import omegaui.listener.KeyStrokeListener;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static java.awt.event.KeyEvent.*;
import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class TabComp extends JComponent implements FocusListener {
    public TabPanel tabPanel;
    public TabData tabData;

    public TextComp iconComp;
    public TextComp nameComp;
    public TextComp closeComp;

    public Runnable removeAction;

    public volatile boolean focussed = false;
    public volatile boolean inList = false;

    public static int tabIndex = 0;

    public TabComp(TabPanel tabPanel, TabData tabData, Runnable removeAction) {
        this.tabData = tabData;
        this.tabPanel = tabPanel;
        this.removeAction = removeAction;

        setSize(27 + computeWidth(tabData.getName(), UBUNTU_PX14) + 4 + 18, tabHeight);
        setBackground(back1);

        registerListeners();

        init();
    }

    public void registerListeners() {
        KeyStrokeListener keyStrokelistener = new KeyStrokeListener(this);
        keyStrokelistener.putKeyStroke((e) -> showTab(0), VK_ALT, VK_1).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showTab(1), VK_ALT, VK_2).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showTab(2), VK_ALT, VK_3).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showTab(3), VK_ALT, VK_4).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showTab(4), VK_ALT, VK_5).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showTab(5), VK_ALT, VK_6).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showTab(6), VK_ALT, VK_7).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showTab(7), VK_ALT, VK_8).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showTab(8), VK_ALT, VK_9).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showNextTab(e), VK_ALT, VK_RIGHT).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> showPreviousTab(e), VK_ALT, VK_LEFT).useAutoReset();
        keyStrokelistener.putKeyStroke((e) -> closeTab(), VK_CONTROL, VK_F4).useAutoReset();
        addKeyListener(keyStrokelistener);

        tabData.getComponent().addFocusListener(this);
        tabData.getComponent().addKeyListener(keyStrokelistener);

        if (tabData.getComponent() instanceof RTextScrollPane scrollPane) {
            scrollPane.getViewport().getView().addFocusListener(this);
            scrollPane.getViewport().getView().addKeyListener(keyStrokelistener);
        } else if (tabData.getComponent() instanceof JScrollPane scrollPane) {
            scrollPane.getViewport().getView().addFocusListener(this);
            scrollPane.getViewport().getView().addKeyListener(keyStrokelistener);
        } else if (tabData.getComponent() instanceof JetRunPanel runPanel) {
            runPanel.terminalPanel.addFocusListener(this);
            runPanel.terminalPanel.addKeyListener(keyStrokelistener);
        } else if (tabData.getComponent() instanceof RunPanel runPanel) {
            runPanel.runTextArea.addFocusListener(this);
            runPanel.runTextArea.addKeyListener(keyStrokelistener);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showTab();
            }
        });
    }

    public void init() {
        iconComp = new TextComp(tabData.getImage(), 20, 20, getBackground(), getBackground(), getBackground(), null);
        iconComp.setBounds(2, getHeight() / 2 - 25 / 2, 25, 25);
        iconComp.setArc(0, 0);
        iconComp.setShowHandCursorOnMouseHover(true);
        add(iconComp);

        if (tabData.getPopup() != null) {
            tabData.getPopup().invokeOnMouseLeftPress(iconComp, () -> {
            });
            putAnimationLayer(iconComp, getImageSizeAnimationLayer(20, 5, true), ACTION_MOUSE_ENTERED);
        }

        nameComp = new TextComp(tabData.getName(), tabData.getTooltip(), getBackground(), getBackground(), tabData.getTabTextColor(), this::showTab);
        nameComp.setBounds(27, getHeight() / 2 - 25 / 2, computeWidth(tabData.getName(), UBUNTU_PX14) + 4, 25);
        nameComp.setFont(UBUNTU_PX14);
        nameComp.setArc(5, 5);
        add(nameComp);

        closeComp = new TextComp("x", TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR4_SHADE, TOOLMENU_COLOR4, this::closeTab);
        closeComp.setBounds(getWidth() - 17, getHeight() / 2 - 15 / 2, 15, 15);
        closeComp.setFont(PX12);
        closeComp.setArc(4, 4);
        closeComp.setShowHandCursorOnMouseHover(true);
        add(closeComp);

        setSize(iconComp.getWidth() + nameComp.getWidth() + 2 + iconComp.getWidth(), tabHeight);
        setPreferredSize(getSize());
    }

    public void showNextTab(KeyEvent e) {
        tabIndex++;
        if (tabIndex >= tabPanel.getTabs().size()) {
            tabIndex = 0;
        }
        showTab(tabIndex);
        if (e != null) {
            e.consume();
        }
    }

    public void showPreviousTab(KeyEvent e) {
        tabIndex--;
        if (tabIndex < 0) {
            tabIndex = tabPanel.getTabs().size() - 1;
        }
        showTab(tabIndex);
        if (e != null) {
            e.consume();
        }
    }

    public void showTab(int index) {
        TabData tabData = tabPanel.getTabDataAt(index);
        if (tabData == null) {
            return;
        }
        tabData.getTabComp().showTab();
    }

    public void showTab() {
        tabPanel.setActiveTab(tabData);
        tabData.getComponent().grabFocus();

        if (tabData.getComponent() instanceof RTextScrollPane scrollPane) {
            try {
                ((JComponent) scrollPane.getViewport().getView()).grabFocus();
            } catch (Exception e) {
            }
        } else if (tabData.getComponent() instanceof JScrollPane scrollPane) {
            try {
                ((JComponent) scrollPane.getViewport().getView()).grabFocus();
            } catch (Exception e) {
            }
        } else if (tabData.getComponent() instanceof JetRunPanel runPanel) {
            runPanel.terminalPanel.grabFocus();
        } else if (tabData.getComponent() instanceof RunPanel runPanel) {
            runPanel.runTextArea.grabFocus();
        }
    }

    public void closeTab() {
        tabData.getOnClose().run();
        tabPanel.removeTab(tabData);
        removeAction.run();
        if (!tabPanel.getTabHistory().getActivatedTabs().isEmpty()) {
            TabData nextTabData = (TabData) tabPanel.getTabHistory().getActivatedTabs().getLast();
            if (nextTabData != null) {
                showTab(tabPanel.getIndexOf(nextTabData.getComponent()));
            }
        }
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (focussed)
            g.setPaint(new GradientPaint(0, 0, isDarkMode() ? TOOLMENU_COLOR6 : TOOLMENU_COLOR1, getWidth(), getHeight(), isDarkMode() ? TOOLMENU_COLOR5 : TOOLMENU_COLOR2));
        else
            g.setPaint(new GradientPaint(0, 0, back3, getWidth(), getHeight(), back1));
        g.fillRect(0, getHeight() - 2, getWidth(), 2);
        g.fillRect(0, 0, 2, getHeight());
        g.fillRect(getWidth() - 2, 0, 2, getHeight());
        g.fillRect(0, 0, getWidth(), 2);
        super.paint(g);
    }

    @Override
    public void focusLost(FocusEvent e) {
        focussed = false;
        ToolMenu.pathBox.setPath(null);
        repaint();
    }

    @Override
    public void focusGained(FocusEvent e) {
        focussed = true;

        String path = Screen.getProjectFile().getProjectPath() + File.separator;

        if (tabData.getComponent() instanceof RTextScrollPane scrollPane) {
            if (scrollPane.getViewport().getView() instanceof Editor) {
                path = ((Editor) scrollPane.getViewport().getView()).currentFile.getPath();
            }
        } else {
            path += tabData.getName();
        }

        ToolMenu.pathBox.setPath(path);
        repaint();
    }

    public boolean isInList() {
        return inList;
    }

    public void setInList(boolean inList) {
        this.inList = inList;
    }

    public TabData getTabData() {
        return tabData;
    }


}
