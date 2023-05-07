/**
 * BuildPanel
 * Copyright (C) 2022 Omega UI
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

import javax.swing.*;
import java.awt.*;

import static omega.io.UIManager.*;

public class BuildPanel extends JPanel {
    private JScrollPane scrollPane;
    private JPanel panel;
    private String hint;
    private int count = 0;

    public BuildPanel(String hint) {
        super(new BorderLayout());
        this.hint = hint;
        super.add(scrollPane = new JScrollPane(panel = new JPanel(null)));
        scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL) {
            @Override
            public void setVisible(boolean value) {
                super.setVisible(false);
            }
        });
        panel.setBackground(c2);
        scrollPane.setBorder(null);
        setVisible(false);
    }

    @Override
    public void paint(Graphics graphics) {
        if (count == 0) {
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(PX14);
            g.setColor(c2);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(TOOLMENU_COLOR4);
            g.drawString(hint, getWidth() / 2 - g.getFontMetrics().stringWidth(hint) / 2, getHeight() / 2 - g.getFontMetrics().getHeight() / 2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
        } else {
            super.paint(graphics);
        }
    }

    public void setPanelPrefSize(Dimension d) {
        panel.setPreferredSize(d);
        scrollPane.repaint();
    }

    @Override
    public Component add(Component c) {
        panel.add(c);
        count++;
        return c;
    }

    @Override
    public void remove(Component c) {
        panel.remove(c);
        count--;
    }
}

