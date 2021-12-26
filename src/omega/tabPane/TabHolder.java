package omega.tabPane;
import omega.comp.FlexPanel;

import javax.swing.JPanel;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class TabHolder extends JPanel{
	private TabData tabData;

	private FlexPanel flexPanel;
	
	public TabHolder(TabData tabData){
		super(null);
		this.tabData = tabData;

		setBackground(c2);

		init();
	}

	public void init(){
		flexPanel = new FlexPanel(null, TOOLMENU_COLOR1_SHADE, null);
		flexPanel.setArc(5, 5);
		flexPanel.add(tabData.getComponent());
		add(flexPanel);
	}

	@Override
	public void layout(){
		flexPanel.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
		tabData.getComponent().setBounds(5, 5, flexPanel.getWidth() - 10, flexPanel.getHeight() - 10);
		super.layout();
	}

	public TabData getTabData() {
		return tabData;
	}
	
}
