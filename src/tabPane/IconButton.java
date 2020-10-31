package tabPane;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class IconButton extends JComponent{
	private ImageIcon icon;
	private Runnable r;
	private volatile boolean enter = false;
	public IconButton(Icon icon) {
		this.icon = (ImageIcon)icon;
		setPreferredSize(new Dimension(16, 16));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				enter = true;
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				enter = false;
				repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(r != null)
					r.run();
			}
		});
	}
	
	public void setAction(Runnable r) {
		this.r = r;
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(icon.getImage(), 0, 0, 16, 16, null);
		if(enter)
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
}