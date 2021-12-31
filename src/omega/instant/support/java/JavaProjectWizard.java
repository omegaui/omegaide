/**
* JavaProjectWizard
* Copyright (C) 2021 Omega UI

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

package omega.instant.support.java;
import omega.Screen;

import omega.ui.dialog.SDKSelector;
import omega.ui.dialog.WorkspaceSelector;
import omega.ui.dialog.FileSelectionDialog;

import omega.instant.support.java.management.JDKManager;

import java.io.File;

import omega.io.IconManager;
import omega.io.DataManager;
import omega.io.ProjectDataBase;

import omegaui.component.TextComp;
import omegaui.component.EdgeComp;
import omegaui.component.NoCaretField;

import javax.swing.JPanel;
import javax.swing.JDialog;

import java.util.LinkedList;

import static omega.io.UIManager.*;
import static omegaui.component.animation.Animations.*;

public class JavaProjectWizard extends JDialog {
	private TextComp titleComp;
	
	private EdgeComp projectSpecsLabel;
	private EdgeComp projectNameLabel;
	private NoCaretField projectNameField;
	private EdgeComp workspaceLabel;
	private TextComp workspaceComp;
	private TextComp browseWorkspaceComp;
	
	private EdgeComp jdkSpecsLabel;
	private EdgeComp jdkRootLabel;
	private TextComp jdkRootComp;
	private TextComp autoDetectComp;
	private TextComp browseJDKRootComp;
	private EdgeComp jdkLabel;
	private TextComp jdkComp;
	private TextComp browseJDKComp;
	
	private TextComp statusComp;
	
	private TextComp createComp;
	private TextComp cancelComp;

	private FileSelectionDialog fileSelectionDialog;
	private SDKSelector sdkSelector;
	
	public JavaProjectWizard(Screen screen){
		super(screen, false);
		setTitle("Java Project Wizard");
		setUndecorated(true);
		setSize(500, 350);
		setLocationRelativeTo(null);
		setResizable(false);
		JPanel panel = new JPanel(null);
		setContentPane(panel);
		panel.setBackground(c2);
		init();
	}
	
	public void init(){
		titleComp = new TextComp("Create a new Java Project", c2, c2, glow, null);
		titleComp.setBounds(0, 0, getWidth(), 25);
		titleComp.setClickable(false);
		titleComp.setArc(0, 0);
		titleComp.setFont(PX14);
		titleComp.attachDragger(this);
		titleComp.setHighlightColor(TOOLMENU_COLOR1);
		titleComp.addHighlightText("Java", "Project");
		add(titleComp);
		
		projectSpecsLabel = new EdgeComp("Project Specifications", back2, TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR1, null);
		projectSpecsLabel.setBounds(getWidth()/2 - 250/2, 30, 250, 30);
		projectSpecsLabel.setFont(PX14);
		projectSpecsLabel.setLookLikeLabel(true);
		add(projectSpecsLabel);
		
		projectNameLabel = new EdgeComp("Project Name", back2, TOOLMENU_GRADIENT, glow, null);
		projectNameLabel.setBounds(2, 70, 120, 25);
		projectNameLabel.setFont(PX14);
		projectNameLabel.setUseFlatLineAtBack(true);
		add(projectNameLabel);
		
		projectNameField = new NoCaretField("", "Start Typing", TOOLMENU_COLOR2, c2, TOOLMENU_COLOR3);
		projectNameField.setBounds(125, 70, getWidth() - 127, 25);
		projectNameField.setFont(PX14);
		add(projectNameField);
		addKeyListener(projectNameField);
		
		workspaceLabel = new EdgeComp("Workspace", back2, TOOLMENU_GRADIENT, glow, null);
		workspaceLabel.setBounds(2, 110, 120, 25);
		workspaceLabel.setFont(PX14);
		workspaceLabel.setUseFlatLineAtBack(true);
		add(workspaceLabel);
		
		workspaceComp = new TextComp("Omega Projects", c2, c2, TOOLMENU_COLOR3, null);
		workspaceComp.setBounds(125, 110, 150, 25);
		workspaceComp.setFont(PX14);
		workspaceComp.setArc(5, 5);
		workspaceComp.setClickable(false);
		add(workspaceComp);
		
		browseWorkspaceComp = new TextComp(IconManager.fluentbrowsefolderImage, 20, 20, "Browse", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, this::selectWorkspace);
		browseWorkspaceComp.setBounds(280, 110, 30, 25);
		browseWorkspaceComp.setFont(PX14);
		browseWorkspaceComp.setArc(5, 5);
		add(browseWorkspaceComp);
		
		jdkSpecsLabel = new EdgeComp("JDK Specifications", back2, TOOLMENU_COLOR1_SHADE, TOOLMENU_COLOR1, null);
		jdkSpecsLabel.setBounds(getWidth()/2 - 250/2, 150, 250, 30);
		jdkSpecsLabel.setFont(PX14);
		jdkSpecsLabel.setLookLikeLabel(true);
		add(jdkSpecsLabel);
		
		jdkRootLabel = new EdgeComp("JDK Root", back2, TOOLMENU_GRADIENT, glow, null);
		jdkRootLabel.setBounds(2, 190, 120, 25);
		jdkRootLabel.setFont(PX14);
		jdkRootLabel.setUseFlatLineAtBack(true);
		add(jdkRootLabel);
		
		jdkRootComp = new TextComp("No JDK Root", c2, c2, TOOLMENU_COLOR3, null);
		jdkRootComp.setBounds(125, 190, 150, 25);
		jdkRootComp.setFont(PX14);
		jdkRootComp.setArc(5, 5);
		jdkRootComp.setClickable(false);
		add(jdkRootComp);
		
		autoDetectComp = new TextComp("Auto Detect", TOOLMENU_GRADIENT, TOOLMENU_COLOR4_SHADE, TOOLMENU_COLOR4, this::autoDetect);
		autoDetectComp.setBounds(280, 190, 100, 25);
		autoDetectComp.setFont(PX14);
		autoDetectComp.setArc(5, 5);
		add(autoDetectComp);
		
		browseJDKRootComp = new TextComp(IconManager.fluentbrowsefolderImage, 20, 20, "Browse", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, this::selectJDKRoot);
		browseJDKRootComp.setBounds(390, 190, 30, 25);
		browseJDKRootComp.setFont(PX14);
		browseJDKRootComp.setArc(5, 5);
		add(browseJDKRootComp);
		
		jdkLabel = new EdgeComp("JDK Version", back2, TOOLMENU_GRADIENT, glow, null);
		jdkLabel.setBounds(2, 230, 120, 25);
		jdkLabel.setFont(PX14);
		jdkLabel.setUseFlatLineAtBack(true);
		add(jdkLabel);
		
		jdkComp = new TextComp("No JDK Selected", c2, c2, TOOLMENU_COLOR3, null);
		jdkComp.setBounds(125, 230, 150, 25);
		jdkComp.setFont(PX14);
		jdkComp.setArc(5, 5);
		jdkComp.setClickable(false);
		add(jdkComp);
		
		browseJDKComp = new TextComp("Select", TOOLMENU_GRADIENT, TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, this::selectJDK);
		browseJDKComp.setBounds(280, 230, 80, 25);
		browseJDKComp.setFont(PX14);
		browseJDKComp.setArc(5, 5);
		add(browseJDKComp);
		
		statusComp = new TextComp("", c2, c2, glow, null);
		statusComp.setBounds(0, getHeight() - 30, getWidth(), 30);
		statusComp.setFont(PX14);
		statusComp.setClickable(false);
		statusComp.setHighlightColor(TOOLMENU_COLOR2);
		statusComp.setArc(0, 0);
		statusComp.alignX = 5;
		add(statusComp);
		
		cancelComp = new TextComp("Cancel", TOOLMENU_COLOR4_SHADE, TOOLMENU_COLOR2_SHADE, TOOLMENU_COLOR2, this::dispose);
		cancelComp.setBounds(getWidth() - 180, getHeight() - 30 - 30, 80, 30);
		cancelComp.setFont(PX14);
		cancelComp.setArc(5, 5);
		add(cancelComp);
		
		createComp = new TextComp("Create", TOOLMENU_GRADIENT, TOOLMENU_COLOR5_SHADE, TOOLMENU_COLOR5, this::create);
		createComp.setBounds(getWidth() - 90, getHeight() - 30 - 30, 80, 30);
		createComp.setFont(PX14);
		createComp.setArc(5, 5);
		add(createComp);
		
		putAnimationLayer(browseWorkspaceComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(browseJDKRootComp, getImageSizeAnimationLayer(20, +5, true), ACTION_MOUSE_ENTERED);
	}
	
	public void setStatus(String text, boolean completed, String... texts){
		if(!completed){
			statusComp.setText(text);
			statusComp.addHighlightText(texts);
		}
		else{
			statusComp.setText("Status of any running operation will appear here");
			statusComp.addHighlightText("running operation");
		}
	}

	public void selectJDKRoot(){
		if(fileSelectionDialog == null)
			fileSelectionDialog = new FileSelectionDialog(Screen.getScreen());
		
		fileSelectionDialog.setTitle("Select folder containing the JDKs");
		LinkedList c;
		LinkedList<File> selections = fileSelectionDialog.selectDirectories();
		if(!selections.isEmpty()){
			DataManager.setPathToJava(selections.get(0).getAbsolutePath());
			jdkRootComp.setText(DataManager.getPathToJava());
		}
	}

	public void selectWorkspace(){
		new WorkspaceSelector(Screen.getScreen()).setVisible(true);
		load();
	}
	
	public void selectJDK(){
		if(sdkSelector == null)
			sdkSelector = new SDKSelector(Screen.getScreen());
		
		sdkSelector.setVisible(true);
		
		String res = sdkSelector.getSelection();
		if(res != null){
			File file = new File(res);
			if(file.exists()){
				jdkComp.setText(file.getName());
				jdkComp.setToolTipText(file.getAbsolutePath());
			}
		}
	}
	
	public void create(){
		String projectName = projectNameField.getText();
		if(!Screen.isNotNull(projectName)){
			setStatus("Please Specify a Project Name", false, "Project Name");
			return;
		}
		String workspace = workspaceComp.getToolTipText();
		if(workspace == null || workspace.equals("")){
			setStatus("Please Specify a Project Workspace", false, "Project Workspace");
			return;
		}
		File root = new File(workspace, projectName);
		if(root.exists()){
			setStatus("Project With this name already exists!", false, "already exists");
			return;
		}
		
		//Creating Project Structure
		root.mkdir();
		new File(root, "bin").mkdir();
		new File(root, "out").mkdir();
		new File(root, "res").mkdir();
		new File(root, "src").mkdir();
		
		ProjectDataBase.genInfo(root.getAbsolutePath(), false);
		
		Screen.getScreen().loadProject(root);
		
		ProjectDataBase dataBase = Screen.getFileView().getProjectManager();
		dataBase.setJDKPath(jdkComp.getToolTipText());
		dataBase.save();
		
		setVisible(false);
	}
	
	public void autoDetect(){
		String osName = System.getProperty("os.name");
		String path = "";
		
		if(osName.contains("nux")){
			path = "/usr/lib/jvm";
			setStatus("Checking " + path + " ... ", false, path);
			if(!containsJDKs(new File(path))){
				path = null;
				setStatus("Cannot Auto-Detect JDKs on your system! Try Browsing.", false, "Cannot", "Try Browsing");
			}
		}
		else if(osName.contains("indows")){
			path = "C:\\Program Files\\Java";
			setStatus("Checking " + path + " ... ", false, path);
			if(!containsJDKs(new File(path))){
				path = "C:\\Program Files (x86)\\Java";
				setStatus("Checking " + path + " ... ", false, path);
				if(!containsJDKs(new File(path))){
					path = null;
					setStatus("Cannot Auto-Detect JDKs on your system! Try Browsing.", false, "Cannot", "Try Browsing");
				}
			}
		}
		
		if(path != null){
			DataManager.setPathToJava(path);
			jdkRootComp.setText(path);
			setStatus("Saved " + path + " as JDK Root", false, path);
		}
	}
	
	public boolean containsJDKs(File jdkDir){
		File[] files = jdkDir.listFiles();
		if(files != null && files.length > 0){
			for(File dir : files){
				if(JDKManager.isJDKPathValid(dir.getAbsolutePath()))
					return true;
			}
		}
		return false;
	}

	public void load(){
		setStatus("", true, "");
		
		String workspace = DataManager.getWorkspace();
		File file = new File(workspace);
		if(workspace != null && file.exists()){
			workspaceComp.setText(file.getName());
			workspaceComp.setToolTipText(file.getAbsolutePath());
		}
		else
			workspaceComp.setText("Specify Workspace");
		
		String jdkRoot = DataManager.getPathToJava();
		file = new File(jdkRoot);
		
		if(jdkRoot != null && file.exists())
			jdkRootComp.setText(file.getAbsolutePath());
		else
			jdkRootComp.setText("Specify JDK Root");
	}
	
	@Override
	public void setVisible(boolean value){
		if(value)
			load();
		super.setVisible(value);
	}
}
