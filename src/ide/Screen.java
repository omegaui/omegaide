package ide;
/*
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
import java.util.LinkedList;
import creator.Settings;
import gset.Generator;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import ide.utils.systems.creators.ChoiceDialog;
import ide.utils.WorkspaceSelector;
import terminal.TerminalComp;
import java.awt.Desktop;
import Omega.IDE;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import highlightUnit.BasicHighlight;
import highlightUnit.ErrorHighlighter;
import ide.utils.DataManager;
import ide.utils.Editor;
import ide.utils.RecentsManager;
import ide.utils.ToolMenu;
import ide.utils.UIManager;
import ide.utils.ProjectDataBase;
import ide.utils.systems.BuildView;
import ide.utils.systems.EditorTools;
import ide.utils.systems.FileView;
import ide.utils.systems.OperationPane;
import ide.utils.systems.ProjectView;
import ide.utils.systems.RunView;
import launcher.Launcher;
import plugin.PluginManager;

import plugin.PluginStore;
import plugin.PluginView;
import snippet.SnippetBase;
import snippet.SnippetView;
import startup.Startup;
import tabPane.IconManager;
import tabPane.TabPanel;
import tree.FileTree;
import update.Updater;

public class Screen extends JFrame {
     public EditorTools tools;
     public JSplitPane splitPane;
     public JSplitPane compilancePane;
     public JSplitPane rightTabPanelSplitPane;
     public JSplitPane bottomTabPanelSplitPane;
     public Editor focussedEditor;
     public static Launcher launcher;
     public static SnippetView snippetView; 
     public static final String VERSION = "v1.7";
     public static String PATH_SEPARATOR = ":";
     public volatile boolean active = true;
     public volatile boolean screenHasProjectView = true;

     private SplashScreen splash;
	private OperationPane operationPane;
     private TabPanel tabPanel;
     private TabPanel rightTabPanel;
     private TabPanel bottomTabPanel;
	private ToolMenu toolMenu;
     private static Robot robot;
	private static UIManager uiManager;
	private static DataManager dataManager;
     private static RunView runView;
     private static FileView fileView;
     private static BuildView buildView;
     private static BasicHighlight basicHighlight;
	private static RecentsManager recentsManager;
     private static ChoiceDialog choiceDialog;
     private static ErrorHighlighter errorHighlighter;
	private static ProjectView projectView;
     private static settings.Screen settings;
     private static Settings universalSettings;
	private static PluginManager pluginManager;
	private static PluginView pluginView;
	private static PluginStore pluginStore;
	private static Updater updater;
     private static TerminalComp terminal;

	public Screen() {
		try {
               Startup.writeUIFiles();
               if(!File.separator.equals("/"))
                    PATH_SEPARATOR = ";";
               dataManager = new DataManager(this);
               if(UIManager.isDarkMode())
                    FlatDarkLaf.install();
               else
                    FlatLightLaf.install();
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/UbuntuMono-Bold.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Ubuntu-Bold.ttf")));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
          UIManager.loadHighlight();
          uiManager = new UIManager(this);
		UIManager.setData(this);
          Startup.checkStartup(this);
		splash = new SplashScreen();
		splash.setProgress(10, "welcome");
		splash.setProgress(37, "welcome");
      
          gset.Generator.init(this);

		setIconImage(IconManager.getImageIcon("/omega_ide_icon64.png").getImage());
		setTitle("Omega Integrated Development Environment " + VERSION);
		setLayout(new BorderLayout());
		setSize(1000, 650);
          setMinimumSize(getSize());
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				active = false;
				Screen.notify("Terminating Running Applications");
				try{
					for(Process p : runView.runningApps) {
						if(p.isAlive())
							p.destroyForcibly();
					}
				}catch(Exception e2) {}
				Screen.notify("Saving UI and Data");
				uiManager.save();
				dataManager.saveData();
				SnippetBase.save();
				Screen.notify("Saving Project");
				saveAllEditors();
				try{getFileView().getProjectManager().save();}catch(Exception e2) {}
			}
		});
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		splash.setProgress(60, "initializing");
          init();
	}

	private void init() {
          choiceDialog = new ChoiceDialog(this);
		SnippetBase.load();
		snippetView = new SnippetView(this);
		updater = new Updater(this);
		errorHighlighter = new ErrorHighlighter();
		basicHighlight = new BasicHighlight();

		operationPane = new OperationPane(this);

          terminal = new TerminalComp();

          rightTabPanelSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
          bottomTabPanelSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
         
		UIManager.setData(splitPane);
          UIManager.setData(rightTabPanelSplitPane);
          UIManager.setData(bottomTabPanelSplitPane);
      
		compilancePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		compilancePane.setTopComponent(splitPane);
		compilancePane.setBottomComponent(operationPane);
		compilancePane.setDividerSize(2);
		compilancePane.setDividerLocation(Screen.this.getHeight() - 400);
		add(compilancePane, BorderLayout.CENTER);
		UIManager.setData(compilancePane);

		tabPanel = new TabPanel(this);
          rightTabPanel = new TabPanel(this);
          bottomTabPanel = new TabPanel(this);

          rightTabPanel.setAddAction(()->{
               if(rightTabPanel.tabPane.getTabCount() > 0){
                    rightTabPanel.setVisible(true);
                    rightTabPanelSplitPane.setDividerLocation(rightTabPanelSplitPane.getWidth()/2);
               }
          });
          
          rightTabPanel.setRemoveAction(()->{
               if(rightTabPanel.tabPane.getTabCount() == 0)
                    rightTabPanel.setVisible(false);
          });
          
          bottomTabPanel.setAddAction(()->{
               if(bottomTabPanel.tabPane.getTabCount() > 0){
                    bottomTabPanel.setVisible(true);
                    bottomTabPanelSplitPane.setDividerLocation(bottomTabPanelSplitPane.getWidth()/2);
               }
          });
          
          bottomTabPanel.setRemoveAction(()->{
               if(bottomTabPanel.tabPane.getTabCount() == 0)
                    bottomTabPanel.setVisible(false);
          });
          
          rightTabPanel.setVisible(false);
          bottomTabPanel.setVisible(false);

		splitPane.setRightComponent(bottomTabPanelSplitPane);
          rightTabPanelSplitPane.setLeftComponent(tabPanel);
          rightTabPanelSplitPane.setRightComponent(rightTabPanel);
          bottomTabPanelSplitPane.setTopComponent(rightTabPanelSplitPane);
          bottomTabPanelSplitPane.setBottomComponent(bottomTabPanel);
       
		splitPane.setDividerSize(2);
          rightTabPanelSplitPane.setDividerSize(2);
          bottomTabPanelSplitPane.setDividerSize(2);

		toolMenu = new ToolMenu(this);
		add(toolMenu, BorderLayout.NORTH);

		recentsManager = new RecentsManager(this);

		splash.setProgress(77, "initializing");

		fileView = new FileView("File", this);
		settings = new settings.Screen(this);
          universalSettings = new Settings(this);
		buildView = new BuildView("Build", this);
		runView = new RunView("Run", this, true);
		projectView = new ProjectView("Project", this);

		tools = new EditorTools();

		splitPane.setLeftComponent(projectView.getProjectView());
		splitPane.setDividerLocation(300);

		splash.setProgress(83, "plugging in");

		pluginManager = new PluginManager();
		pluginView = new PluginView(this);
		pluginStore = new PluginStore();

		splash.setProgress(100, "");
		File file = new File(DataManager.getDefaultProjectPath());
       
          if(DataManager.getProjectsHome().equals("") || !new File(DataManager.getProjectsHome()).exists())
               new WorkspaceSelector(this).setVisible(true);
          
		if(file.exists() && file.isDirectory()) {
			loadProject(file);
		}
		else {
			launcher = new launcher.Launcher();
			launcher.setVisible(true);
		}
	}

	@Override
	public void setVisible(boolean value) {
		if(value & screenHasProjectView) {
			splitPane.setDividerLocation(300);
			compilancePane.setDividerLocation(Screen.this.getHeight() - 400);
			projectView.organizeProjectViewDefaults();
			if(projectView.tree.getRoot() == null || !projectView.tree.getRoot().getAbsolutePath().equals(Screen.getFileView().getProjectPath())) {
				projectView.tree = new FileTree(Screen.getFileView().getProjectPath());
				projectView.tree.gen(projectView.tree.getRoot());
			}
			projectView.tree.repaint();

			screenHasProjectView = false;
			Screen.getProjectView().organizeProjectViewDefaults();
			doLayout();
			revoke();
			Screen.getProjectView().setVisible(false);
			screenHasProjectView = true;
			Screen.getProjectView().organizeProjectViewDefaults();
			doLayout();
			revoke();
			Screen.getProjectView().setVisible(false);
		}
		super.setVisible(value);
	}

     public void justVisible(boolean value){
     	super.setVisible(value);
     }

	public void revoke() {
		boolean wasExtended = false;
		if(getExtendedState() == MAXIMIZED_BOTH) wasExtended = true;
		setSize((int)getSize().width + 1, (int)getSize().height);
		setSize((int)getSize().width - 1, (int)getSize().height);
		if(wasExtended) setExtendedState(MAXIMIZED_BOTH);
	}

	public void setToView() {
		int x = splitPane.getDividerLocation();
		splitPane.setLeftComponent(projectView.getProjectView());
		splitPane.setDividerLocation(x <= 300 ? 300 : x);
	}

	public void setToNull() {
		splitPane.remove(projectView.getProjectView());
	}

	public static void notify(String text) {
		getScreen().setTitle(text);
	}

	public static void hideNotif() {
		getScreen().loadTitle(getFileView().getProjectName());
	}

	public static void notify(String text, long time, Runnable task) {
		getScreen().setTitle(text);
		new Thread(()->{
			try{Thread.sleep(time);}catch(Exception e) {System.out.println(e.getMessage());}
			getScreen().loadTitle(getFileView().getProjectName());
			if(task != null)
				task.run();
		}).start();
	}

     public void manageTools(ProjectDataBase manager){
          toolMenu.structureViewComp.setVisible(!manager.non_java);
          toolMenu.asteriskComp.setVisible(!manager.non_java);
          toolMenu.contentModeComp.setVisible(!manager.non_java);
          toolMenu.typeItem.setName(fileView.getProjectManager().non_java ? "Project Type : Non-Java" : "Project Type : Java");
     }

	public static void setStatus(String status, int value) {
		if(value != 100)
			Screen.getScreen().getToolMenu().setMsg(status + " " + value + "%");
		else
			Screen.getScreen().getToolMenu().setMsg(null);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		for(Component c : getComponents())
			c.repaint();
	}

	public static void reverseColors(Component c) {
		Color l = c.getBackground();
		c.setBackground(c.getForeground());
		c.setForeground(l);
	}

	public void loadTitle(String projectName) {
		setTitle(projectName+" -Omega IDE "+VERSION);
	}
	
	public void setProject(String projectName) {
		loadTitle(projectName);
		splitPane.setDividerLocation(300);
	}

	public static void openInDesktop(File file) {
		try {
			new Thread(()->{
				try {
					java.awt.Desktop.getDesktop().open(file);
				}
				catch(Exception ex){System.out.println(ex);}
			}).start();
		}catch(Exception e) {System.out.println(e);}
	}

     public Editor loadFile(File file) {
          String fn = file.getName();
          if(fn.endsWith(".pdf") || fn.endsWith(".deb")){
               openInDesktop(file);
               return null;
          }
          if(tabPanel.viewImage(file)) return null;
          if(tabPanel.viewArchive(file)) return null;
          if(isFileOpened(file))
               return null;
          new Thread(()->Screen.addAndSaveRecents(file.getAbsolutePath())).start();
          Editor editor = new Editor(this);
          editor.loadFile(file);
          tabPanel.addTab(file.getName(), editor, getPackName(file));
          return editor;
     }

     public Editor loadFileOnRightTabPanel(File file) {
          String fn = file.getName();
          if(fn.endsWith(".pdf") || fn.endsWith(".deb")){
               openInDesktop(file);
               return null;
          }
          if(rightTabPanel.viewImage(file)) return null;
          if(rightTabPanel.viewArchive(file)) return null;
          if(isFileOpened(file))
               return null;
          new Thread(()->Screen.addAndSaveRecents(file.getAbsolutePath())).start();
          Editor editor = new Editor(this);
          editor.loadFile(file);
          rightTabPanel.addTab(file.getName(), editor, getPackName(file));
          return editor;
     }

     public Editor loadFileOnBottomTabPanel(File file) {
          String fn = file.getName();
          if(fn.endsWith(".pdf") || fn.endsWith(".deb")){
               openInDesktop(file);
               return null;
          }
          if(bottomTabPanel.viewImage(file)) return null;
          if(bottomTabPanel.viewArchive(file)) return null;
          if(isFileOpened(file))
               return null;
          new Thread(()->Screen.addAndSaveRecents(file.getAbsolutePath())).start();
          Editor editor = new Editor(this);
          editor.loadFile(file);
          bottomTabPanel.addTab(file.getName(), editor, getPackName(file));
          return editor;
     }

	public static void addAndSaveRecents(String path) {
		RecentsManager.add(path);
		recentsManager.saveData();
	}

     public static void launchNewWindow(String projectPath){
     	Omega.IDE.main(null);
     }

	public void loadProject(File file) {
		fileView.saveAll();
		fileView.setProjectPath(file.getAbsolutePath());
		Screen.hideNotif();
	}

	public boolean isFileOpened(File file) {
          LinkedList<Editor> allEditors = new LinkedList<>();
          tabPanel.getEditors().forEach(allEditors::add);
          rightTabPanel.getEditors().forEach(allEditors::add);
          bottomTabPanel.getEditors().forEach(allEditors::add);
		for(Editor e : allEditors) {
			if(e.currentFile != null) {
				if(e.currentFile.getAbsolutePath().equals(file.getAbsolutePath())){
                         allEditors.clear();
					return true;
				}
			}
		}
		return false;
	}

	public String getPackName(File file) {
		String res = "";
		boolean canRecord = false;
		StringTokenizer tokenizer = new StringTokenizer(file.getAbsolutePath(), File.separator);
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			if(canRecord)
				res += token + ".";
			else if(token.equals(getFileView().getProjectName()))
				canRecord = true;
		}
		if(!canRecord)
			return file.getAbsolutePath();
		else
			res = res.substring(0,res.length() - 1);
		return res;
	}

	public static final Screen getScreen() {
		return Screen.getFileView().getScreen();
	}

	public static RecentsManager getRecentsManager() {
		return recentsManager;
	}

	public static FileView getFileView()
	{
		return fileView;
	}

	public static BuildView getBuildView()
	{
		return buildView;
	}

	public static RunView getRunView() {
		return runView;
	}

	public static ProjectView getProjectView() {
		return projectView;
	}

	public static ErrorHighlighter getErrorHighlighter() {
		return errorHighlighter;
	}

	public static BasicHighlight getBasicHighlight() {
		return basicHighlight;
	}

     public static settings.Screen getSettingsView() {
          return settings;
     }

     public static Settings getUniversalSettingsView() {
          return universalSettings;
     }
     
     public static TerminalComp getTerminalComp(){
          return terminal;
     }

	public ToolMenu getToolMenu() {
		return toolMenu;
	}    

	public OperationPane getOperationPanel() {
		return operationPane;
	}

	public void pressKey(int code){
		try {
			if(robot == null)
				robot = new Robot();
			robot.keyPress(code);
		}catch(Exception e) {}
	}

	public void moveTo(int x, int y)
	{
		try {
			if(robot == null)
				robot = new Robot();
			robot.mouseMove(x, y);
		}catch(Exception e) {}
	}

	public UIManager getUIManager() {
		return uiManager;
	}

	public DataManager getDataManager(){
		return dataManager;
	}

	public void saveAllEditors() {
          tabPanel.getEditors().forEach(w->w.saveCurrentFile());
          rightTabPanel.getEditors().forEach(w->w.saveCurrentFile());
          bottomTabPanel.getEditors().forEach(w->w.saveCurrentFile());
	}

	public void loadThemes(){
          tabPanel.getEditors().forEach(w->w.loadTheme());
          rightTabPanel.getEditors().forEach(w->w.loadTheme());
          bottomTabPanel.getEditors().forEach(w->w.loadTheme());
	}

	public Editor getCurrentEditor() {
		return focussedEditor;
	}

	public void closeCurrentProject() {
		tabPanel.closeAllTabs();
	}

	public TabPanel getTabPanel() {
		return tabPanel;
	}

     public TabPanel getRightTabPanel() {
          return rightTabPanel;
     }
     
     public TabPanel getBottomTabPanel() {
          return bottomTabPanel;
     }
     
	public static void updateIDE() {
		updater.setVisible(true);
	}

	public static PluginManager getPluginManager() {
		return pluginManager;
	}

	public static PluginView getPluginView() {
		return pluginView;
	}

	public static PluginStore getPluginStore() {
		return pluginStore;
	}

     public static ChoiceDialog getChoiceDialog() {
          return choiceDialog;
     }

	public void saveEssential() {
		uiManager.save();
		dataManager.saveData();
		notify("Saving Project");
		saveAllEditors();
		hideNotif();
	}
}
