package ide.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JFrame;

import ide.Screen;

public abstract class StateWindow extends JFrame {

	private Screen screen;
	public LinkedList<Component> comps = new LinkedList<>();
	
	public StateWindow(String title, Screen window) {
		setTitle(title);
		this.screen = window;
		setSize(200,200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setIconImage(window.getIconImage());
		UIManager.setData(this);
	}

	public Screen getScreen() {
		return screen;
	}

	@Override
	public void paint(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(), getHeight());
		try {comps.forEach(c->c.repaint());}catch(Exception e) {}
	}
	
	
	@Override
	public void setVisible(boolean value)
	{
		if(value) {
			comps.forEach(c->{
				UIManager.setData(c);
				c.repaint();
			});
		}
		try {
		super.setVisible(value);
		}catch(Exception e) {}
	}

}
