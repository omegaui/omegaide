package uiPool;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JComponent;

public class UIButton extends JComponent {

	private static final LinkedList<UIButton> uiBs = new LinkedList<>();
	private String text = "";
	private volatile boolean enter;
	public volatile boolean click;
	private static final Font f = new Font("Consolas", Font.BOLD, 17);

	public UIButton(String text, Runnable r) {
		this.text = text;
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
				click = true;
				repaint();
				r.run();
			}
		});
		uiBs.add(this);
	}
	
	public UIButton(String text) {
		this.text = text;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				repaint();
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				repaint();
			}
		});
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(f);
	}
	
	@Override
	public void paint(Graphics g2D) {
		Graphics2D g = (Graphics2D)g2D;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(getFont());
		int x = g.getFontMetrics().stringWidth(text);
		x = getWidth()/2 - x/2;
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		g.drawString(text,  x, getFont().getSize());
		if(enter) {
			x = g.getFontMetrics().stringWidth(text+" ");
			x = getWidth()/2 - x/2;
			g.setColor(getForeground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(getBackground());
			g.drawString(text+" ", getFont().getSize(), getFont().getSize());
		}
		if(click) {
			uiBs.forEach(uiB->{
				if(uiB != UIButton.this) {
					uiB.click = false;
					uiB.repaint();
				}
			});
			x = g.getFontMetrics().stringWidth(text);
			x = getWidth()/2 - x/2;
			g.setColor(getForeground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(getBackground());
			g.drawString(text, getFont().getSize(), getFont().getSize());
		}
	}

}
