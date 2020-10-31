package ide.utils.systems;

import java.awt.Component;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JDialog;

import ide.Screen;
import ide.utils.UIManager;

public abstract class View extends JDialog {

	public LinkedList<Component> comps = new LinkedList<>();
	private Screen s;
	private Action a = ()->{};
	
	public interface Action {
		
		void perform();
		
	}
	
	public View(String title, Screen window)
	{
		super(window, true);
		s = window;
		setTitle(title);
		setSize(200,200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(window.getIconImage());
		UIManager.setData(this);
	}
	
	public void setAction(Action a)
	{
		this.a = a;
	}
	
	public Screen getScreen()
	{
		return s;
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
			a.perform();
		}
		super.setVisible(value);
	}
}
