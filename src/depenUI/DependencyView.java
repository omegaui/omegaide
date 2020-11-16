package depenUI;
import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import ide.Screen;
public class DependencyView extends JDialog{
	private JTabbedPane tabPane;
	private DependencyPanel lib;
	private DependencyPanel nat;
	private DependencyPanel res;
	public DependencyView(JFrame frame){
		super(frame, true);
		setTitle("Manage Project Dependencies");
		setSize(600, 500);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		init();
		ide.utils.UIManager.setData(this);
	}

	private void init(){
		tabPane = new JTabbedPane();
		tabPane.setFont(DependencyPanel.font);
		tabPane.addTab("Libraries", lib = new DependencyPanel("library"));
		tabPane.addTab("Natives", nat = new DependencyPanel("natives"));
		tabPane.addTab("Resources", res = new DependencyPanel("resources"));
		add(tabPane, BorderLayout.CENTER);
		ide.utils.UIManager.setData(tabPane);
	}

	@Override
	public void setVisible(boolean value) {
		if(value) {
			lib.read();
			res.read();
			nat.read();
		}
		else {
			Screen.getScreen().tools.initTools();
		}
		super.setVisible(value);
	}
}
