package omega.utils.systems;
import omega.instant.support.ArgumentManager;
import omega.launcher.Launcher;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JFileChooser;

import omega.deassembler.Assembly;
import omega.depenUI.DependencyView;
import omega.Screen;
import omega.utils.DataManager;
import omega.utils.DependencyManager;
import omega.utils.ModuleManager;
import omega.utils.ModuleView;
import omega.utils.NativesManager;
import omega.utils.ProjectDataBase;
import omega.utils.ResourceManager;
import omega.utils.UIManager;
import omega.utils.systems.creators.FileCreator;
import omega.search.SearchWindow;

public class FileView extends View {

	private static String projectPath = null;
	private ProjectDataBase projectManager;
	private DependencyManager dependencyManager;
	private NativesManager nativesManager;
	private ResourceManager resourceManager;
	private volatile FileCreator fileCreator;
	private DependencyView dependencyView;
	private ModuleView moduleView;
	private ModuleManager moduleManager;
     private ArgumentManager argumentManager;
	private SearchWindow searchWindow;

	public FileView(String title, Screen window) {
		super(title, window);
		dependencyView = new DependencyView(window);
		moduleView = new ModuleView(window);
		searchWindow = new SearchWindow(window);
		setLayout(new FlowLayout());
		init();
		setSize(getWidth(), getHeight());
	}

	private void init() {
		fileCreator = new FileCreator(getScreen());	
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
		Assembly.deassemble();
		projectManager = new ProjectDataBase();
          getScreen().manageTools(projectManager);
          if(!projectManager.non_java){
               Screen.notify("Reading Dependencies and Resource Roots", 1000, null);
     		dependencyManager = new DependencyManager();
     		nativesManager = new NativesManager();
     		resourceManager = new ResourceManager();
     		moduleManager = new ModuleManager();
          }
          else{
               argumentManager = new ArgumentManager();
          }
		searchWindow.cleanAndLoad(new File(projectPath));
          if(!projectManager.non_java) {
     		if(Screen.getFileView().getProjectManager().jdkPath == null || !new File(Screen.getFileView().getProjectManager().jdkPath).exists()){
     			Screen.notify("No JDK Defined for Project "+Screen.getFileView().getProjectName(), 3000, null);
     		}
     		else {
     			projectManager.readJDK(false);
     		}
     		Screen.hideNotif();
     		try {Screen.getProjectView().reload();}catch(Exception e) {}
          }
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void saveAll() {
		if(projectManager != null){
			projectManager.save();
               if(!projectManager.non_java){
          		if(dependencyManager != null)
          			dependencyManager.saveFile();
          		if(nativesManager != null)
          			nativesManager.saveFile();
          		if(resourceManager != null)
          			resourceManager.saveData();
               }
               else{
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

     public DependencyManager getDependencyManager() {
          return dependencyManager;
     }

     public NativesManager getNativesManager() {
          return nativesManager;
     }

     public ResourceManager getResourceManager() {
          return resourceManager;
     }

     public DependencyView getDependencyView() {
          return dependencyView;
     }

     public ModuleView getModuleView() {
          return moduleView;
     }

     public ModuleManager getModuleManager() {
          return moduleManager;
     }

     public ArgumentManager getArgumentManager() {
          return argumentManager;
     }

     public String getProjectName()
     {
          if(projectPath == null)
               return "";
          return projectPath.substring(projectPath.lastIndexOf(File.separatorChar)+1);
     }

	public static void checkDir(File file)
	{
		if(!file.exists())
		{
			file.mkdir();
		}
	}

}
