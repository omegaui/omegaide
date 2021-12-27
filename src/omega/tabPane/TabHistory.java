package omega.tabPane;
import java.util.LinkedList;
public class TabHistory implements TabPanelListener{
	private TabPanel tabPanel;
	
	private LinkedList<TabData> tabs = new LinkedList<>();

	public TabHistory(TabPanel tabPanel){
		this.tabPanel = tabPanel;
		tabPanel.addTabPanelListener(this);
	}

	@Override
	public void tabActivated(TabData tabData){
		if(tabs.contains(tabData))
			tabs.remove(tabData);
		tabs.add(tabData);
	}
	
	@Override
	public void tabAdded(TabData tabData) {
		
	}
	
	@Override
	public void tabRemoved(TabData tabData) {
		if(!tabs.isEmpty()){
			tabs.remove(tabData);
			for(int i = tabs.size() - 1; i >= 0; i--){
				tabData = tabs.get(i);
				if(tabPanel.isTabDataAlreadyPresent(tabData)){
					tabPanel.setActiveTab(tabData);
				}
			}
		}
	}
	
	@Override
	public void goneEmpty(TabPanel tabPanel) {
		tabs.clear();
	}

	public java.util.LinkedList getActivatedTabs() {
		return tabs;
	}
	
}
