package uiPool;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ide.Screen;
import ide.utils.UIManager;

public class Notification extends Window {
	private String text = "";
	private Screen screen;
	private static final Font font = new Font("Ubuntu Mono", Font.BOLD, 16);

	public Notification(Screen screen) {
		super(screen);
		this.screen = screen;
		UIManager.setData(this);
		setFont(font);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
			}
		});
		if(((Color)(javax.swing.UIManager.getDefaults().get("Button.background"))).getRed() <= 53) {
			setBackground(contentUI.Click.colorY);
			setForeground(contentUI.Click.colorX);
		}else {
			setBackground(Color.WHITE);
		}
	}

	@Override
	public void paint(Graphics g2D){
		if(g2D == null) return;
		//setting size
		Graphics2D g = (Graphics2D)g2D;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//Drawing Background
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		//Drawing Border
		g.setColor(getForeground());
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		//Drawing Text
		g.setFont(getFont());
		int x = g.getFontMetrics().stringWidth(text);
		g.drawString(text, getWidth() / 2 - x / 2, getFont().getSize());
		if(text.equals("")) setVisible(false);
	}

	public void setText(String text) {
		if(text.equals("")) setVisible(false);
		try {
			setVisible(true);
			this.text = text;
			if(getGraphics() != null) {
				Graphics g2D = getGraphics();
				g2D.setFont(getFont());
				setSize(g2D.getFontMetrics().stringWidth(text) + (getFont().getSize() * 2), getFont().getSize() * 2);
				paint(getGraphics());
				setLocationRelativeTo(screen);
				paint(getGraphics());
				paint(getGraphics());
			}
			repaint();
			repaint();
		}catch(Exception e) {setVisible(false);}
	}
}
