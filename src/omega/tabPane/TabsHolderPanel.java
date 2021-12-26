package omega.tabPane;
import omega.utils.IconManager;

import omega.Screen;

import omega.popup.OPopupWindow;

import omega.comp.TextComp;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.LinkedList;

import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class TabsHolderPanel extends JPanel{
	
	private TextComp tabsHolderComp;
	
	private TabPanel tabPanel;
	
	private LinkedList<TabComp> tabComps = new LinkedList<>();

	private OPopupWindow tabSwitchPopup;

	private int blockX = 0;

	private int lastRequiredViewSpace;
	
	public TabsHolderPanel(TabPanel tabPanel){
		super(null);
		this.tabPanel = tabPanel;
		
		setBackground(c2);

		setSize(100, tabHeight);
		setPreferredSize(getSize());
		
		init();
	}

	public void init(){
		tabsHolderComp = new TextComp(IconManager.fluenttabsHolderIcon, 25, 25, TOOLMENU_COLOR1_SHADE, c2, c2, null);
		tabsHolderComp.setArc(5, 5);
		tabsHolderComp.setVisible(false);
		tabsHolderComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				
				LinkedList<TabComp> backgroundTabs = getBackgroundTabs();
				
				int width = 100;
				
				for(TabComp tx : backgroundTabs){
					if(tx.getWidth() > width){
						width = tx.getWidth();
					}
				}
	
				tabSwitchPopup = OPopupWindow.gen("Tab Switcher", Screen.getScreen(), 0, true).width(width + 100);
				for(TabComp tx : backgroundTabs){
					tabSwitchPopup.createItem(tx.getTabData().getName(), tx.getTabData().getImage(), ()->{
						tabPanel.setActiveTab(tx.getTabData());
					});
				}
				tabSwitchPopup.height(backgroundTabs.size() > 7 ? 300 : (backgroundTabs.size() * OPopupWindow.HEIGHT));
				tabSwitchPopup.setLocationRelativeTo(tabPanel);
				tabSwitchPopup.setVisible(true);
			}
		});
		add(tabsHolderComp);
		
		putAnimationLayer(tabsHolderComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(tabsHolderComp, getImageSizeAnimationLayer(20, -5, true), ACTION_MOUSE_PRESSED);
	}
	
	public void addTabHolder(TabData tabData){
		TabComp comp = new TabComp(tabPanel, tabData, this::checkTabView);
		comp.setLocation(blockX, 0);
		add(comp);
		tabComps.add(comp);
		
		blockX += comp.getWidth();
		
		tabData.setTabComp(comp);
		
		if(isTabExceedingView(comp)){
			int requiredViewSpace = comp.getWidth();
			
			removeTabsToGetFreeSpace(requiredViewSpace);

			comp.setLocation(blockX - requiredViewSpace, 0);
		}

		checkTabView();

		repaint();
	}

	public void showTab(TabData tabData){
		if(isTabInBackground(tabData)){
			tabPanel.removeTab(tabData);
			tabPanel.addTab(tabData);
		}
	}

	public void shiftTabs(){
		blockX = 0;
		
		for(TabComp tx : tabComps){
			if(!tx.isInList()){
				tx.setLocation(blockX, 0);
				blockX += tx.getWidth();
				add(tx);
			}
			else{
				remove(tx);
			}
		}
	}

	public TabComp getFirstForegroundTab(){
		for(TabComp tx : tabComps){
			if(!tx.isInList())
				return tx;
		}
		return null;
	}

	public void removeTabsToGetFreeSpace(int requiredSpace){
		this.lastRequiredViewSpace = requiredSpace;
		int freeSpace = 0;
		for(TabComp tx : tabComps){
			if(tx.isInList())
				continue;
			
			freeSpace += tx.getWidth();
			
			tx.setInList(true);
			
			if(freeSpace >= requiredSpace){
				shiftTabs();
				return;
			}
		}
	}

	public boolean isTabExceedingView(TabComp comp){
		return comp.getX() + comp.getWidth() > getWidth() - tabHeight;
	}

	public boolean isBlockXExceedingView(){
		return blockX > getWidth() - tabHeight;
	}

	public boolean isTabInBackground(TabData data){
		for(TabComp tx : tabComps){
			if(tx.getTabData().equals(data))
				return tx.isInList();
		}
		return false;
	}

	public TabComp getTabCompInMainList(TabData data){
		for(TabComp tx : tabComps){
			if(tx.getTabData().equals(data))
				return tx;
		}
		return null;
	}

	public LinkedList<TabComp> getBackgroundTabs(){
		LinkedList<TabComp> tabs = new LinkedList<>();
		tabComps.forEach((tab)->{
			if(tab.isInList()){
				tabs.add(tab);
			}
		});
		return tabs;
	}

	public void checkTabView(){
		if(tabComps.isEmpty())
			return;
		
		blockX = 0;
		
		for(int i = tabComps.size() - 1; i >= 0; i--){
			TabComp tx = tabComps.get(i);
			
			blockX += tx.getWidth();
			
			if(!isBlockXExceedingView()){
				tx.setInList(false);
			}
			else{
				tx.setInList(true);
			}
		}

		shiftTabs();
		
		LinkedList<TabComp> backgroundTabs = getBackgroundTabs();
		
		tabsHolderComp.setVisible(!backgroundTabs.isEmpty());
	}

	public void removeTabHolder(TabData tabData){
		remove(tabData.getTabComp());

		blockX -= tabData.getTabComp().getWidth();

		for(int i = tabComps.indexOf(tabData.getTabComp()) + 1; i < tabComps.size(); i++){
			TabComp comp = tabComps.get(i);
			comp.setLocation(comp.getX() - tabData.getTabComp().getWidth(), comp.getY());
		}
		
		tabComps.remove(tabData.getTabComp());
	}

	public synchronized void triggerAllClose(){
		//Creating a copy of tabComps to prevent concurrent modification exception!
		LinkedList<TabComp> tabs = new LinkedList<>();
		tabComps.forEach(tabs::add);
		
		for(TabComp tx : tabs)
			tx.closeTab();
		
		tabComps.clear();
		tabs.clear();
	}

	@Override
	public void layout(){
		tabsHolderComp.setBounds(getWidth() - 35, getHeight()/2 - 30/2, 30, 30);
		checkTabView();
		super.layout();
	}
}
