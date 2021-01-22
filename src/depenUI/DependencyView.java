package depenUI;
/*
    The combined Classpath GUI.
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
