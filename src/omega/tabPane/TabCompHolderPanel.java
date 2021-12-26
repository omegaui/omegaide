package omega.tabPane;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class TabCompHolderPanel extends JPanel{
	
	private TabPanel tabPanel;
	
	private CardLayout cardLayout;
	
	public TabCompHolderPanel(TabPanel tabPanel){
		setLayout(cardLayout = new CardLayout());
		this.tabPanel = tabPanel;

		setBackground(c2);
	}
	
	public void putTab(TabData tabData){
		add(tabData.getUniqueName(), tabData.getTabHolder());
		showTabComponent(tabData);
	}

	public void showTabComponent(TabData tabData){
		cardLayout.show(this, tabData.getUniqueName());
	}

	public void putOffTab(TabData tabData){
		remove(tabData.getTabHolder());
	}
	
	@Override
	public void paint(Graphics graphics){
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g);
		g.dispose();
	}
}
