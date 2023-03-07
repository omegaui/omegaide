/*
 * UniversalProjectWizard
 * Copyright (C) 2022 Omega UI

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package omega.instant.support.universal;
import omega.io.ProjectDataBase;
import omega.io.AppDataManager;

import omega.ui.dialog.WorkspaceSelector;

import omegaui.component.TextComp;
import omegaui.component.NoCaretField;

import omega.Screen;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import static omega.io.UIManager.*;

public class UniversalProjectWizard extends JDialog{
	
	private TextComp titleComp;
	private TextComp projectNameLabel;
	
	private NoCaretField nameField;
	
	private TextComp projectWorkspaceLabel;
	private TextComp projectWorkspaceComp;
	private TextComp hintLabel;
	private TextComp closeComp;
	private TextComp createComp;

	private String hint1 = "Project structure can be created in the IDE";

	public UniversalProjectWizard(JFrame frame){
		super(frame, false);
		setTitle("Universal Project Wizard");
		setUndecorated(true);
		JPanel panel = new JPanel(null);
		panel.setBackground(c2);
		setContentPane(panel);
		setBackground(c2);
		setSize(500, 235);
		setLocationRelativeTo(null);
		init();
	}

	public void init(){
		titleComp = new TextComp("Universal Project Wizard", TOOLMENU_GRADIENT, TOOLMENU_GRADIENT, glow, null);
		titleComp.setBounds(0, 0, getWidth(), 30);
		titleComp.setFont(PX14);
		titleComp.setArc(0, 0);
		titleComp.setClickable(false);
		titleComp.attachDragger(this);
		add(titleComp);

		projectNameLabel = new TextComp("Project Name", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		projectNameLabel.setBounds(10, 50, 150, 25);
		projectNameLabel.setFont(PX14);
		projectNameLabel.setClickable(false);
		add(projectNameLabel);

		nameField = new NoCaretField("", "Start Typing", TOOLMENU_COLOR3, c2, TOOLMENU_COLOR2);
		nameField.setBounds(170, 50, getWidth() - 190, 25);
		nameField.setFont(PX14);
		add(nameField);
		addKeyListener(nameField);

		projectWorkspaceLabel = new TextComp("Workspace", TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		projectWorkspaceLabel.setBounds(10, 100, 150, 25);
		projectWorkspaceLabel.setFont(PX14);
		projectWorkspaceLabel.setClickable(false);
		add(projectWorkspaceLabel);

		projectWorkspaceComp = new TextComp("Select Workspace", TOOLMENU_COLOR1_SHADE, c2, TOOLMENU_COLOR1, ()->{
			new WorkspaceSelector(Screen.getScreen()).setVisible(true);
			load();
		});
		projectWorkspaceComp.setBounds(170, 100, getWidth() - 190, 25);
		projectWorkspaceComp.setFont(PX14);
		add(projectWorkspaceComp);

		hintLabel = new TextComp(hint1, TOOLMENU_COLOR3_SHADE, c2, TOOLMENU_COLOR3, null);
		hintLabel.setBounds(10, 150, getWidth() - 20, 30);
		hintLabel.setClickable(false);
		hintLabel.setFont(PX14);
		add(hintLabel);

		closeComp = new TextComp("Close",  TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, this::dispose);
		closeComp.setBounds(getWidth() - 20 - 160, 200, 80, 30);
		closeComp.setFont(PX14);
		closeComp.setArc(5, 5);
		add(closeComp);

		createComp = new TextComp("Create",  TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR5_SHADE, TOOLMENU_COLOR5, this::create);
		createComp.setBounds(getWidth() - 10 - 80, 200, 80, 30);
		createComp.setFont(PX14);
		createComp.setArc(5, 5);
		add(createComp);
	}

	public void create(){
		String projectName = nameField.getText();
		if(projectName == null || projectName.equals("")){
			hintLabel.setText("Please Specify a Project Name");
			return;
		}
		String workspace = projectWorkspaceComp.getToolTipText();
		if(workspace == null || workspace.equals("")){
			hintLabel.setText("Please Specify a Project Workspace");
			return;
		}
		File root = new File(workspace, projectName);
		if(root.exists()){
			hintLabel.setText("Project With this name already exists!");
			return;
		}
		root.mkdir();
		ProjectDataBase.genInfo(root.getAbsolutePath(), true);
		setVisible(false);
		Screen.getScreen().loadProject(root);
	}

	public void load(){
		String workspace = AppDataManager.getWorkspace();
		File file = new File(workspace);
		if(workspace != null && file.exists()){
			projectWorkspaceComp.setText(file.getName());
			projectWorkspaceComp.setToolTipText(file.getAbsolutePath());
		}
		else
			projectWorkspaceComp.setText("Select Workspace");
	}

	@Override
	public void setVisible(boolean value){
		if(value)
			load();
		super.setVisible(value);
	}
}

