package ide.utils;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;

public class Effects {
	public static void addHoverToolTipEffect(JButton c, String tooltip) {
		c.setText("");
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				c.setText(tooltip);
				c.repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				c.setText("");
				c.repaint();
			}
		});
	}

	public static void addHoverToolTipEffect(JButton c, String tooltip, Icon icon) {
		c.setText("");
		c.setIcon(icon);
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				c.setText(tooltip);
				c.repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				c.setText("");
				c.repaint();
			}
		});
	}

}
