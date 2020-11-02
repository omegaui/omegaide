package uiPool;

import java.awt.Component;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;

import ide.utils.UIManager;

public class IDEPanel extends JPanel {

	private final LinkedList<Component> comps = new LinkedList<>();
	public IDEPanel() {
		setLayout(null);
		UIManager.setData(this);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		comps.forEach(b->b.repaint());
	}
}
