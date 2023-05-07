/*
 * DynamicListMaker
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

package omega.instant.support.universal;

import omega.Screen;
import omega.io.IconManager;
import omega.ui.panel.TabPanel;
import omegaui.component.TextComp;

import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class DynamicListMaker extends JDialog {

    public TextComp titleComp;
    public TextComp addComp;
    public TextComp closeComp;

    public TabPanel listTabPanel;

    public int listCount = 0;

    public LinkedList<DynamicListPanel> listPanels = new LinkedList<>();

    public DynamicListMaker() {
        super(Screen.getScreen(), true);
        setUndecorated(true);
        setSize(500, 250);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(null);
        panel.setBackground(back2);
        setContentPane(panel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        init();
    }

    public void init() {
        titleComp = new TextComp("Add List Makers", back2, back2, glow, null);
        titleComp.setBounds(30, 0, getWidth() - 60, 30);
        titleComp.setFont(PX14);
        titleComp.setClickable(false);
        titleComp.setArc(0, 0);
        titleComp.attachDragger(this);
        add(titleComp);

        addComp = new TextComp(IconManager.fluentaddlinkImage, 20, 20, back2, back2, back2, this::create);
        addComp.setBounds(0, 0, 30, 30);
        addComp.setArc(0, 0);
        add(addComp);

        closeComp = new TextComp(IconManager.fluentcloseImage, 20, 20, back2, back2, back2, this::dispose);
        closeComp.setBounds(getWidth() - 30, 0, 30, 30);
        closeComp.setArc(0, 0);
        add(closeComp);

        listTabPanel = new TabPanel(TabPanel.TAB_LOCATION_TOP);
        listTabPanel.setBounds(0, 30, getWidth(), getHeight() - 30);
        add(listTabPanel);

        putAnimationLayer(closeComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
        putAnimationLayer(addComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
    }

    public void create() {
        listCount++;
        DynamicListPanel panel = new DynamicListPanel();
        listTabPanel.addTab("List " + listCount, "dynamic-list-panel-" + listCount, "Click x to delete this list!", IconManager.fluentbuildImage, panel, () -> panel.setEnabled(false));
        if (listCount == 1) {
            listTabPanel.setVisible(false);
            listTabPanel.setVisible(true);
        }
        listPanels.add(panel);
    }

    public void create(DynamicListPanel panel) {
        listCount++;
        listTabPanel.addTab("List " + listCount, "dynamic-list-panel-" + listCount, "Click x to delete this list!", IconManager.fluentbuildImage, panel, () -> panel.setEnabled(false));
        if (listCount == 1) {
            listTabPanel.setVisible(false);
            listTabPanel.setVisible(true);
        }
        listPanels.add(panel);
    }

    @Override
    public void dispose() {
        loadAllToArgsManager();
        super.dispose();
        listTabPanel.closeAllTabs();
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            Screen.getProjectFile().getArgumentManager().units.forEach(this::create);
        }
        super.setVisible(value);
    }

    public void loadAllToArgsManager() {
        if (Screen.getProjectFile().getArgumentManager() == null) {
            if (Screen.getProjectFile().getProjectManager().isLanguageTagNonJava())
                System.err.println("This is practically impossible but the Current Project has a language tag non-java but its argument manager is null somehow????!");
            return;
        }
        Screen.getProjectFile().getArgumentManager().units.clear();
        listPanels.forEach(list -> {
            if (list.validateListPanel())
                Screen.getProjectFile().getArgumentManager().units.add(list);
        });
        listPanels.clear();
    }
}
