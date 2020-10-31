package ide.utils;

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
