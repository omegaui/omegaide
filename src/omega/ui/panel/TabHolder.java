/**
 * TabHolder
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

import omega.io.TabData;
import omegaui.component.FlexPanel;

import javax.swing.*;
import java.awt.*;

import static omega.io.UIManager.*;

public class TabHolder extends JPanel {
    private TabData tabData;

    private FlexPanel flexPanel;
    private JPanel panel;

    public TabHolder(TabData tabData) {
        super(null);
        this.tabData = tabData;

        setBackground(c2);

        init();
    }

    public void init() {
        flexPanel = new FlexPanel(null, materialTabHolderColor1, null);
        flexPanel.setArc(5, 5);
        add(flexPanel);

        panel = new JPanel(new BorderLayout());
        panel.setBackground(TOOLMENU_COLOR1_SHADE);
        panel.setBorder(null);
        panel.add(tabData.getComponent(), BorderLayout.CENTER);

        flexPanel.add(panel);
    }

    public JPanel getHolderPanel() {
        return panel;
    }

    @Override
    public void layout() {
        flexPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
        panel.setBounds(5, 5, flexPanel.getWidth() - 10, flexPanel.getHeight() - 10);
        super.layout();
    }

    public TabData getTabData() {
        return tabData;
    }

}
