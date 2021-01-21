package tabPane;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class TabPaneUI extends BasicTabbedPaneUI {
	@Override
	protected int calculateTabHeight(int arg0, int arg1, int arg2) {
		return 25;
	}
}
