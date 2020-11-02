package uiPool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;

public class CloseButton extends JComponent {

	private volatile boolean enter = false;
	private JDialog d;
	
	public CloseButton(Runnable r) {
		addMouseListener(new MouseAdapter(){
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
			public void mouseClicked(MouseEvent e) {
				r.run();
			}
		});
	}
	
	@Override
	public void paint(Graphics g2D) {
		Graphics2D g = (Graphics2D)g2D;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(getFont());
		g.setColor(Color.WHITE);
		if(d != null)
			g.drawString("X", 15, 35);
		else
			g.drawString("X", getFont().getSize(), getFont().getSize());
		if(enter) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(getBackground());
			if(d != null)
				g.drawString("X", 15, 35);
			else
				g.drawString("X", getFont().getSize(), getFont().getSize());
		}
	}

}
