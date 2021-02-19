package omega.utils.systems;
import omega.popup.*;
import omega.tabPane.CloseButton;
import omega.tabPane.TabPaneUI;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import omega.Screen;
import omega.utils.UIManager;

public class OperationPane extends JPanel{

	private Screen screen;
	private JTabbedPane tabPane;
	private static LinkedList<String> names = new LinkedList<>();
    
     private static final String TITLE = "Process Panel";
     private static final String HINT = "There is no process running";
	
	public OperationPane(Screen screen) {
		this.screen = screen;
		setLayout(new BorderLayout());
		tabPane = new JTabbedPane(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabPane.setUI(new TabPaneUI());
		add(tabPane, BorderLayout.CENTER);
		setVisible(false);
		UIManager.setData(tabPane);
		UIManager.setData(this);
	}

	public void addTab(String name, Component c, Runnable r) {
		if(names.indexOf(name) >= 0) {
			removeTab(name);
		}
		names.add(name);
		tabPane.addTab(name, c);
		tabPane.setTabComponentAt(tabPane.indexOfTab(name), CloseButton.create(c,
				name, ()->{
					r.run();
					removeTab(name);
				}, ()->{
					tabPane.setSelectedIndex(names.indexOf(name));
				}, "", null));
          if(tabPane.getTabCount() != 0)
               tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
          setVisible(true);
	}
	
	public void addTab(String name, Component c, Runnable r, OPopupWindow popup) {
		if(names.indexOf(name) >= 0) {
			removeTab(name);
		}
		names.add(name);
		tabPane.addTab(name, c);
		tabPane.setTabComponentAt(tabPane.indexOfTab(name), CloseButton.create(c,
				name, ()->{
					r.run();
					removeTab(name);
				}, ()->{
					tabPane.setSelectedIndex(names.indexOf(name));
				}, "", popup));

		try {
			tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
		}catch(Exception e) {}
          setVisible(true);
	}
	
	public static int count(String name) {
		int c = -1;
		for(String var0 : names){
			if(var0.contains(name))
				c++;
		}
		return c;
	}
	
	@Override
	public void setVisible(boolean value) {
		if(tabPane.getTabCount() == 0)
			super.setVisible(false);
		super.setVisible(value);
          try{
               screen.getToolMenu().oPHidden = value;
               if(value) {
                    setPreferredSize(new Dimension(screen.getWidth(), getHeight() > 450 ? getHeight() : 450));
                    int y = screen.getHeight() - 400;
                    screen.compilancePane.setDividerLocation(y);
               }
               omega.Screen.getScreen().getToolMenu().operateComp.repaint();
          }
          catch(Exception e) {}
	}
	
	public void removeTab(String name) {
		if(names.indexOf(name) < 0) return;
		try {
		tabPane.removeTabAt(tabPane.indexOfTab(name));
		}catch(Exception e) {}
		names.remove(name);
		if(names.isEmpty())
			setVisible(false);
	}

     @Override
     public void paint(Graphics graphics){
          if(tabPane.getTabCount() == 0){
           Graphics2D g = (Graphics2D)graphics;
               g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               g.setColor(omega.utils.UIManager.c2);
               g.fillRect(0, 0, getWidth(), getHeight());
               g.setColor(omega.utils.UIManager.c3);
               g.setFont(omega.settings.Screen.PX28);
               FontMetrics f = g.getFontMetrics();
               g.drawString(TITLE, getWidth()/2 - f.stringWidth(TITLE)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 1);
               g.drawString(HINT, getWidth()/2 - f.stringWidth(HINT)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 10 + f.getHeight());
               g.dispose();
          }
          else super.paint(graphics);
     }

}
