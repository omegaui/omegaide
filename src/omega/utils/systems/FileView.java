package omega.utils.systems;
import omega.utils.BuildPathManager;
import omega.jdk.JDKManager;
import omega.instant.support.ArgumentManager;
import omega.launcher.Launcher;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JFileChooser;

import omega.deassembler.Assembly;
import omega.Screen;
import omega.utils.DataManager;
import omega.utils.ProjectDataBase;
import omega.utils.UIManager;
import omega.utils.systems.creators.FileCreator;
import omega.search.SearchWindow;

public class FileView extends View {

	private static String projectPath = null;
	private ProjectDataBase projectManager;
     private ArgumentManager argumentManager;
	private FileCreator fileCreator;
     private BuildPathManager dependencyView;
	private SearchWindow searchWindow;
     private JDKManager jdkManager;

	public FileView(String title, Screen window) {
		super(title, window);
		dependencyView = new BuildPathManager(window);
		searchWindow = new SearchWindow(window);
		setLayout(new FlowLayout());
		init();
		setSize(getWidth(), getHeight());
	}

	private void init() {
		fileCreator = new FileCreator(getScreen());
	}

	public void readJDK(){
          int version = 0;
          if(jdkManager != null)
               version = jdkManager.getVersionAsInt();
          int versionThis = JDKManager.calculateVersion(new File(projectManager.jdkPath));
          if(version != versionThis)
               Assembly.deassemble();
          jdkManager = new JDKManager(new File(projectManager.jdkPath));
          jdkManager.readSources(projectPath);
          readDependencies();
	}

     public void readDependencies(){
     	if(jdkManager == null) return;
          projectManager.jars.forEach(path->{
               jdkManager.readJar(path, false);
          });
          projectManager.modules.forEach(path->{
               jdkManager.readJar(path, true);
          });
     }
	
	public void setProjectPath(String path) {
		if(projectPath != null) {
			if(projectPath.equals(path))
				return;
		}
		new Thread(()->Screen.addAndSaveRecents(path)).start();
		projectPath = path;
		if(Screen.launcher != null)
			Screen.launcher.setVisible(false);
		DataManager.setDefaultProjectPath(projectPath);
		Screen.notify("Loading Project \"" + getProjectName() + "\"");
		getScreen().setProject(getProjectName());
		try {
			Screen.getProjectView().getProjectView().setVisible(true);
			if(getScreen().screenHasProjectView) {
				Screen.getProjectView().organizeProjectViewDefaults();
				getScreen().setVisible(true);
			}
		}catch(Exception ex) {System.out.println(ex);}
		getScreen().getTabPanel().closeAllTabs();
		projectManager = new ProjectDataBase();
          getScreen().manageTools(projectManager);
          if(projectManager.non_java){
               argumentManager = new ArgumentManager();
          }
		searchWindow.cleanAndLoad(new File(projectPath));
          if(!projectManager.non_java) {
     		if(Screen.getFileView().getProjectManager().jdkPath == null || !new File(Screen.getFileView().getProjectManager().jdkPath).exists())
     			Screen.notify("No JDK Defined for Project " + Screen.getFileView().getProjectName(), 3000, null);
     		else {
     			readJDK();
     		}
                    
     		Screen.hideNotif();
     		try {
     		     Screen.getProjectView().reload();
		     }
		     catch(Exception e) {
                    
	          }
          }
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void saveAll() {
		if(projectManager != null){
			projectManager.save();
               if(projectManager.non_java){
                    argumentManager.save();
               }
		}
	}

	public boolean open(String type) {
		JFileChooser ch = new JFileChooser();
		UIManager.setData(ch);
		if(type.equals("Project")) {
			ch.setDialogTitle("Open Project -Select/Create an empty folder to create a new poject");
			ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			ch.setMultiSelectionEnabled(false);
			int res = ch.showOpenDialog(getScreen());
			if(res == JFileChooser.APPROVE_OPTION) {
				if(Screen.launcher != null)
					Screen.launcher.setVisible(false);
				Screen.getScreen().setVisible(true);
				saveAll();
				getScreen().closeCurrentProject();
				setProjectPath(ch.getSelectedFile().getPath());
				return true;
			}
		}
		else {
			ch.setDialogTitle("Open File");
			ch.setMultiSelectionEnabled(true);
			ch.setCurrentDirectory(new File(projectPath));
			ch.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int res = ch.showOpenDialog(getScreen());
			if(res == JFileChooser.APPROVE_OPTION) {
				for(File file : ch.getSelectedFiles())
					getScreen().loadFile(file);
			}
		}
		return false;
	}

	public void closeProject() {
		saveAll();
		getScreen().saveEssential();
		getScreen().setVisible(false);
		DataManager.setDefaultProjectPath("");
		if(Screen.launcher == null)
			Screen.launcher = new omega.launcher.Launcher();
		Screen.launcher.setVisible(true);
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

     public ArgumentManager getArgumentManager() {
          return argumentManager;
     }

     public JDKManager getJDKManager() {
          return jdkManager;
     }
     
     public String getProjectName() {
          if(projectPath == null)
               return "";
          return projectPath.substring(projectPath.lastIndexOf(File.separatorChar)+1);
     }

	public static void checkDir(File file) {
		if(!file.exists()) {
			file.mkdir();
		}
	}

}
