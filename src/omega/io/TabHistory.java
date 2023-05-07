/**
 * TabHistory
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

package omega.io;

import omega.ui.listener.TabPanelListener;
import omega.ui.panel.TabPanel;

import java.util.LinkedList;

public class TabHistory implements TabPanelListener {
    private TabPanel tabPanel;

    private LinkedList<TabData> tabs = new LinkedList<>();

    public TabHistory(TabPanel tabPanel) {
        this.tabPanel = tabPanel;
        tabPanel.addTabPanelListener(this);
    }

    @Override
    public void tabActivated(TabData tabData) {
        if (tabs.contains(tabData))
            tabs.remove(tabData);
        tabs.add(tabData);
    }

    @Override
    public void tabAdded(TabData tabData) {

    }

    @Override
    public void tabRemoved(TabData tabData) {
        if (!tabs.isEmpty()) {
            tabs.remove(tabData);
            if (tabs.size() > 0) {
                TabData tx = tabs.getLast();
                if (tabPanel.isTabDataAlreadyPresent(tx))
                    tabPanel.setActiveTab(tx);
            }
        }
    }

    @Override
    public void goneEmpty(TabPanel tabPanel) {
        tabs.clear();
    }

    public java.util.LinkedList getActivatedTabs() {
        return tabs;
    }

}
