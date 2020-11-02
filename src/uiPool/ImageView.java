package uiPool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class ImageView extends JComponent {

	private String text;
	private volatile boolean enter;

	public ImageView(String text, Runnable r0, Runnable r1) {
		this.text = text;
		setName(text);
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
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 3) {
					r1.run();
				}
				else {
					r0.run();
				}
			}
		});
	}
	
	public ImageView(String text, Runnable r0) {
		this.text = text;
		setName(text);
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
			public void mouseClicked(MouseEvent e) {
				r0.run();
			}
		});
	}
	
	@Override
	public void paint(Graphics g2D) {
		Graphics2D g = (Graphics2D)g2D;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(getFont());
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.drawString(text, g.getFont().getSize(), g.getFont().getSize());
		if(enter) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(getBackground());
			g.drawString(text, g.getFont().getSize(), g.getFont().getSize());
		}
	}

}
