package uiPool;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class UILabel extends JComponent {

	private static final Font f = new Font("Consolas", Font.BOLD, 17);
	private String text;
	
	public UILabel(String text) {
		this.text = text;
	}
	
	public void setText(String text) {
		this.text = text;
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
		g.setColor(Color.RED);
		g.drawString(text, x, getFont().getSize());
	}

}
