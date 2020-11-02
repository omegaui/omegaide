package uiPool;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JTextField;

import ide.Screen;
import ide.utils.UIManager;

public class ProjectPanel extends JPanel {

	private final LinkedList<Component> comps = new LinkedList<>();
	private static final Font f = new Font("Consolas", Font.BOLD, 15);
	public ProjectPanel() {
		setLayout(null);
		UIManager.setData(this);
		init();
	}
	
	private void init() {
		JTextField mainClassField = new JTextField("Main Class -> "+Screen.getRunView().mainClass);
		mainClassField.setBorder(null);
		mainClassField.setEditable(false);
		mainClassField.setBounds(0, 0, Preferences.CARD_WIDTH, 40);
		addComp(mainClassField);
	}
	
	private void addComp(Component c) {
		UIManager.setData(c);
		c.setFont(f);
		comps.add(c);
		add(c);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		comps.forEach(b->b.repaint());
	}
}
