package ide.utils.systems;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import ide.Screen;
import ide.utils.UIManager;
import tabPane.CloseButton;
import tabPane.IconManager;
import tabPane.TabPaneUI;

public class OperationPane extends JPanel{

	private Screen screen;
	private JTabbedPane tabPane;
	private static LinkedList<String> names = new LinkedList<>();
	
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
				}, "", IconManager.javaIcon, null));
		try {
			tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
		}catch(Exception e) {}
		setVisible(true);
		setVisible(false);
		setVisible(true);
	}
	
	public void addTab(String name, Component c, Runnable r, JPopupMenu popup) {
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
				}, "", IconManager.javaIcon, popup));

		try {
			tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
		}catch(Exception e) {}
		setVisible(true);
		setVisible(false);
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
		try {
			if(value) {
				screen.getToolMenu().operateItem.setText("Hide Operation Panel");
				screen.getToolMenu().operateItem.setIcon(IconManager.hide);
				screen.getToolMenu().oPHidden = false;
				screen.compilancePane.setDividerLocation(screen.getHeight() - 400);
				setPreferredSize(new Dimension(screen.getWidth(), 450));
			}
			else {
				screen.getToolMenu().operateItem.setText("Show Operation Panel");
				screen.getToolMenu().operateItem.setIcon(IconManager.show);
				screen.getToolMenu().oPHidden = true;
			}
		}catch(Exception e) {}
		super.setVisible(value);
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

}
