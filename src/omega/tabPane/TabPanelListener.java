package omega.tabPane;
public interface TabPanelListener {
	void tabAdded(TabPanel tabPanel);
	void tabRemoved(TabPanel tabPanel);
	void goneEmpty(TabPanel tabPanel);
}
