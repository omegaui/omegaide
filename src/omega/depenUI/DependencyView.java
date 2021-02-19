package omega.depenUI;
import omega.Screen;

import java.awt.*;
import javax.swing.*;

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
          omega.utils.UIManager.setData(this);
	}

	private void init(){
		tabPane = new JTabbedPane();
		tabPane.setFont(DependencyPanel.font);
		tabPane.addTab("Libraries", lib = new DependencyPanel("library"));
		tabPane.addTab("Natives", nat = new DependencyPanel("natives"));
		tabPane.addTab("Resources", res = new DependencyPanel("resources"));
          tabPane.setBackground(omega.utils.UIManager.c2);
		add(tabPane, BorderLayout.CENTER);
	}

	@Override
	public void setVisible(boolean value) {
          if(Screen.getFileView().getProjectManager().non_java) return;
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
