package uiPool;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JPanel;

import ide.Screen;
import ide.utils.UIManager;

public class Preferences extends JDialog {
	private Screen screen;
	private JPanel switchPane;
	private JPanel cardPane;
	private final LinkedList<Component> comps = new LinkedList<>();
	public static int CARD_WIDTH = 10;
	public Preferences(Screen screen) {
		super(screen, false);
		this.screen = screen;
		UIManager.setData(this);
		setUndecorated(true);
		setSize(600, 450);
		setLocationRelativeTo(null);
		setLayout(null);
		CARD_WIDTH = getWidth() - 200;
		init();
	}
	
	private void init() {
		switchPane = new JPanel();
		switchPane.setLayout(null);
		switchPane.setBounds(0, 0, 200, getHeight());
		UIManager.setData(switchPane);
		Screen.reverseColors(switchPane);
		add(switchPane);
		
		CardLayout cardLayout = new CardLayout();
		cardPane = new JPanel();
		cardPane.setLayout(cardLayout);
		cardPane.setBounds(200, 0, getWidth() - 200, getHeight());
		UIManager.setData(cardPane);
		Screen.reverseColors(cardPane);
		add(cardPane);

		ProjectPanel projPanel = new ProjectPanel();
		addCard(projPanel, "Project Panel");
		
		IDEPanel idePanel = new IDEPanel();
		addCard(idePanel, "IDE Panel");
		
		UIButton proj = new UIButton("Project", ()->{cardLayout.show(cardPane, "Project Panel");});
		proj.setBounds(0, getHeight()/2 - 60, 200, 40);
		addSwitch(proj);
		
		UIButton ide = new UIButton("IDE", ()->{cardLayout.show(cardPane, "IDE Panel");}) ;
		ide.setBounds(0, proj.getY() - 40, 200, 40);
		addSwitch(ide);

		UIButton codeAssist = new UIButton("Code Assist", ()->{}) ;
		codeAssist.setBounds(0, proj.getY() + 40, 200, 40);
		addSwitch(codeAssist);

		UIButton info = new UIButton("Info", ()->{}) ;
		info.setBounds(0, codeAssist.getY() + 40, 200, 40);
		addSwitch(info);
		
		UIButton license = new UIButton("License", ()->{}) ;
		license.setBounds(0, info.getY() + 40, 200, 40);
		addSwitch(license);
	}

	public void addSwitch(Component c) {
		UIManager.setData(c);
		Screen.reverseColors(c);
		switchPane.add(c);
		comps.add(c);
	}

	public void addCard(Component c, String name) {
		UIManager.setData(c);
		Screen.reverseColors(c);
		cardPane.add(c, name);
		comps.add(c);
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		switchPane.repaint();
		cardPane.repaint();
		comps.forEach(b->b.repaint());
	}
}
