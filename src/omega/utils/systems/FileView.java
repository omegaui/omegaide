/**
* FileView
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

package omega.utils.systems;
import omega.tree.FileTreePanel;

import omega.plugin.event.PluginReactionEvent;

import omega.Screen;

import omega.launcher.Launcher;

import omega.instant.support.java.JavaSyntaxParser;

import java.util.LinkedList;

import omega.deassembler.Assembly;

import java.io.File;

import java.awt.FlowLayout;

import omega.jdk.JDKManager;

import omega.search.SearchWindow;

import omega.utils.systems.creators.FileCreator;
import omega.utils.systems.creators.FileOperationManager;

import omega.instant.support.ArgumentManager;

import omega.utils.ProjectDataBase;
import omega.utils.BuildPathManager;
import omega.utils.ExtendedBuildPathManager;
import omega.utils.DataManager;
import omega.utils.FileSelectionDialog;
import omega.utils.Editor;
public class FileView extends View {
	
	private static String projectPath = null;

	private FileTreePanel fileTreePanel;
	
	private ProjectDataBase projectManager;
	private ArgumentManager argumentManager;
	private FileCreator fileCreator;
	private BuildPathManager dependencyView;
	private ExtendedBuildPathManager extendedDependencyView;
	private FileOperationManager fileOperationManager;
	private SearchWindow searchWindow;
	private JDKManager jdkManager;
	
	public FileView(String title, Screen window) {
		super(title, window);
		setLayout(new FlowLayout());
		init();
		setSize(getWidth(), getHeight());
	}
	
	private void init() {

		fileTreePanel = new FileTreePanel();
		
		fileOperationManager = new FileOperationManager(getScreen());
		dependencyView = new BuildPathManager(getScreen());
		extendedDependencyView = new ExtendedBuildPathManager(getScreen());
		searchWindow = new SearchWindow(getScreen());
		fileCreator = new FileCreator(getScreen());
	}
	
	public void readJDK(){
		if(projectManager.jdkPath == null)
			return;
		
		int version = 0;
		if(jdkManager != null)
			version = jdkManager.getVersionAsInt();
		
		int versionThis = JDKManager.calculateVersion(new File(projectManager.jdkPath));
		if(version != versionThis)
			Assembly.deassemble();
		
		if(jdkManager != null)
			jdkManager.clear();
		
		jdkManager = new JDKManager(new File(projectManager.jdkPath));
		jdkManager.readSources(projectPath);
		readDependencies();
	}
	
	public void readDependencies(){
		if(jdkManager == null)
			return;
		LinkedList<String> paths = new LinkedList<>();
		projectManager.jars.forEach(path->{
			jdkManager.readJar(path, false);
			paths.add(path);
		});
		projectManager.modules.forEach(path->{
			jdkManager.readJar(path, true);
			paths.add(path);
		});
		jdkManager.prepareDependencyLoader(paths);
		jdkManager.readResources(projectPath, projectManager.resourceRoots);
		paths.clear();
	}
	
	public void setProjectPath(String path) {
		if(!new File(path).exists())
			return;
		
		if(projectPath != null && projectPath.equals(path))
			return;

		projectPath = path;
		
		new Thread(()->{
			fileTreePanel.init(new File(path));
			Screen.addAndSaveRecents(path);
			searchWindow.cleanAndLoad(new File(projectPath));
		}).start();
		
		if(Screen.launcher != null)
			Screen.launcher.dispose();
		
		DataManager.setDefaultProjectPath(projectPath);
		Screen.notify("Loading Project \"" + getProjectName() + "\"");
		getScreen().setProject(getProjectName());
		
		getScreen().getTabPanel().closeAllTabs();
		
		projectManager = new ProjectDataBase();
		
		getScreen().manageTools(projectManager);
		
		if(projectManager.non_java)
			argumentManager = new ArgumentManager();
		
		if(!projectManager.non_java) {
			if(Screen.getFileView().getProjectManager().jdkPath == null || !new File(Screen.getFileView().getProjectManager().jdkPath).exists())
				Screen.notify("No JDK Defined for Project " + Screen.getFileView().getProjectName(), 3000, null);
			else
				readJDK();
			
			Screen.hideNotif();
			
			try {
				new Thread(()->{
					try{
						Editor.deleteDir(JavaSyntaxParser.BUILDSPACE_DIR);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}).start();
			}
			catch(Exception e) {
				
			}
		}
		getScreen().getToolMenu().reloadItems(projectManager.non_java);
		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_PROJECT_CHANGED, this, projectPath));
	}
	
	public String getProjectPath() {
		return projectPath;
	}
	
	public void saveAll() {
		if(projectManager != null) {
			projectManager.save();
			if(projectManager.non_java){
				argumentManager.save();
			}
		}
	}
	
	public boolean open(String type) {
		FileSelectionDialog fs = new FileSelectionDialog(getScreen());
		if(type.equals("Project")) {
			fs.setTitle("Select a project directory");
			LinkedList<File> files = fs.selectDirectories();
			if(!files.isEmpty()) {
				if(Screen.launcher != null)
					Screen.launcher.dispose();
				Screen.getScreen().setVisible(true);
				saveAll();
				getScreen().closeCurrentProject();
				setProjectPath(files.get(0).getAbsolutePath());
				return true;
			}
		}
		else {
			fs.setTitle("Open File");
			fs.setCurrentDirectory(new File(projectPath));
			LinkedList<File> files = fs.selectFiles();
			if(!files.isEmpty()) {
				for(File file : files)
					getScreen().loadFile(file);
			}
		}
		return false;
	}
	
	public void closeProject() {
		saveAll();
		getScreen().saveEssential();
		getScreen().setVisible(false);
		projectPath = null;
		DataManager.setDefaultProjectPath("");
		if(Screen.launcher == null)
			Screen.launcher = new omega.launcher.Launcher();
		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_PROJECT_CLOSED, this, projectPath));
		Screen.launcher.setVisible(true);
	}

	public FileTreePanel getFileTreePanel() {
		return fileTreePanel;
	}
	
	public SearchWindow getSearchWindow() {
		return searchWindow;
	}
	
	public FileCreator getFileCreator() {
		return fileCreator;
	}
	
	public ProjectDataBase getProjectManager(){
		return projectManager;
	}
	
	public BuildPathManager getDependencyView() {
		return dependencyView;
	}
	
	public ExtendedBuildPathManager getExtendedDependencyView() {
		return extendedDependencyView;
	}
	
	public ArgumentManager getArgumentManager() {
		return argumentManager;
	}
	
	public FileOperationManager getFileOperationManager(){
		return fileOperationManager;
	}
	
	public JDKManager getJDKManager() {
		return jdkManager;
	}
	
	public String getProjectName() {
		if(projectPath == null)
			return "";
		return projectPath.substring(projectPath.lastIndexOf(File.separatorChar) + 1);
	}
	
	public static void checkDir(File file) {
		if(!file.exists()) {
			file.mkdir();
		}
	}
}

