/*
 * AbstractJumpToDefinitionPanel
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

package omega.instant.support;

import omega.ui.component.Editor;
import omegaui.component.FlexPanel;

import javax.swing.*;
import java.awt.*;

import static omega.io.UIManager.TOOLMENU_COLOR3_SHADE;
import static omega.io.UIManager.c2;

public abstract class AbstractJumpToDefinitionPanel extends JPanel {
    public Editor editor;

    public FlexPanel flexPanel;
    public JPanel panel;
    public JScrollPane scrollPane;

    public AbstractJumpToDefinitionPanel(Editor editor) {
        super(new BorderLayout());
        this.editor = editor;

        setBackground(c2);

        flexPanel = new FlexPanel(null, TOOLMENU_COLOR3_SHADE, null);
        flexPanel.setArc(0, 0);
        add(flexPanel, BorderLayout.CENTER);

        panel = new JPanel(null);
        panel.setBackground(c2);

        flexPanel.add(scrollPane = new JScrollPane(panel));

        scrollPane.setBackground(c2);
    }

    public abstract void reload(String match);

    public abstract boolean canRead(Editor editor);

    public void reload() {
        reload("");
    }

    @Override
    public void layout() {
        flexPanel.setBounds(0, 0, getWidth(), getHeight());
        scrollPane.setBounds(5, 5, flexPanel.getWidth() - 10, flexPanel.getHeight() - 10);
        super.layout();
    }
}
