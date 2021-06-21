package omega;
import omega.instant.support.universal.*;
import omega.plugin.*;
import javax.swing.plaf.ColorUIResource;
import javax.imageio.ImageIO;
import omega.utils.BottomPane;
import omega.utils.SideMenu;
import omega.utils.ThemePicker;
import java.awt.event.*;
import omega.utils.IconManager;
import omega.utils.systems.FileView;
import omega.startup.Startup;
import omega.utils.UIManager;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.awt.Desktop;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import omega.utils.ProjectDataBase;
import omega.tree.FileTree;
import omega.utils.WorkspaceSelector;
import omega.snippet.SnippetBase;
import java.awt.BorderLayout;
import omega.gset.Generator;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import java.io.File;
import omega.terminal.TerminalComp;
import omega.plugin.PluginManager;
import omega.utils.systems.ProjectView;
import omega.highlightUnit.ErrorHighlighter;
import omega.utils.RecentsManager;
import omega.highlightUnit.BasicHighlight;
import omega.utils.systems.BuildView;
import omega.utils.systems.RunView;
import omega.utils.DataManager;
import java.awt.Robot;
import omega.utils.ToolMenu;
import omega.tabPane.TabPanel;
import omega.utils.systems.OperationPane;
import omega.snippet.SnippetView;
import omega.launcher.Launcher;
import omega.utils.Editor;
import javax.swing.JSplitPane;
import javax.swing.JFrame;

public class Screen extends JFrame {
     public JSplitPane splitPane;
     public JSplitPane compilancePane;
     public JSplitPane rightTabPanelSplitPane;
     public JSplitPane bottomTabPanelSplitPane;
     public Editor focussedEditor;
     public static Launcher launcher;
     public static SnippetView snippetView; 
     public static final String VERSION = "v1.9";
     public static String PATH_SEPARATOR = ":";
     public volatile boolean active = true;
     public volatile boolean screenHasProjectView = true;

     private SplashScreen splash;
	private OperationPane operationPane;
     private TabPanel tabPanel;
     private TabPanel rightTabPanel;
     private TabPanel bottomTabPanel;
	private ToolMenu toolMenu;
     private SideMenu sideMenu;
     private BottomPane bottomPane;
     private static Robot robot;
	private static UIManager uiManager;
	private static DataManager dataManager;
     private static RunView runView;
     private static FileView fileView;
     private static BuildView buildView;
     private static BasicHighlight basicHighlight;
	private static RecentsManager recentsManager;
     private static ErrorHighlighter errorHighlighter;
	private static ProjectView projectView;
     private static omega.settings.Screen settings;
     private static UniversalSettingsWizard universalSettings;
	private static PluginManager pluginManager;
     private static PluginCenter pluginCenter;
     private static TerminalComp terminal;
     private static ThemePicker picker;

	public Screen() {
		
          setUndecorated(true);
		try {
               Startup.writeUIFiles();
               if(!File.separator.equals("/"))
                    PATH_SEPARATOR = ";";
               dataManager = new DataManager(this);
               Color x = null;
               Color y = null;
               if(UIManager.isDarkMode()) {
                    FlatDarkLaf.install();
                    x = Color.decode("#D34D42");
                    y = Color.decode("#09090B");
                    javax.swing.UIManager.put("ToolTip.foreground", new ColorUIResource(y));
                    javax.swing.UIManager.put("ToolTip.background", new ColorUIResource(x));
                    javax.swing.UIManager.put("Button.foreground", new ColorUIResource(y));
                    javax.swing.UIManager.put("Button.background", new ColorUIResource(x));
               }
               else {
                    FlatLightLaf.install();
                    x = UIManager.TOOLMENU_COLOR3;
                    y = Color.WHITE;
                    javax.swing.UIManager.put("ToolTip.foreground", new ColorUIResource(Color.WHITE));
                    javax.swing.UIManager.put("ToolTip.background", new ColorUIResource(UIManager.TOOLMENU_COLOR2));
                    javax.swing.UIManager.put("Button.foreground", new ColorUIResource(Color.WHITE));
                    javax.swing.UIManager.put("Button.background", new ColorUIResource(UIManager.TOOLMENU_COLOR2));
               }
               javax.swing.UIManager.put("ToolTip.font", omega.settings.Screen.PX14);
               javax.swing.UIManager.put("ScrollBar.thumb", new ColorUIResource(x));
               javax.swing.UIManager.put("ScrollBar.track", new ColorUIResource(y));
               javax.swing.UIManager.put("ScrollPane.background", new ColorUIResource(y));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/UbuntuMono-Bold.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Ubuntu-Bold.ttf")));
		} 
		catch (Exception e1) {
			e1.printStackTrace();
		}
          picker = new ThemePicker(this);
          uiManager = new UIManager(this);
          Startup.checkStartup(this);
          UIManager.loadHighlight();
          UIManager.setData(this);
		splash = new SplashScreen();
		splash.setProgress(10, "welcome");
		splash.setProgress(37, "welcome");

          omega.gset.Generator.init(this);
          try{
          	setIconImage(javax.imageio.ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon500.png")));
          }
          catch(Exception e){
          	e.printStackTrace();
          }
		setTitle("Omega Integrated Development Environment " + VERSION);
		setLayout(new BorderLayout());
		setSize(1000, 650);
          setMinimumSize(getSize());
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		splash.setProgress(60, "initializing");
          init();
	}

	private void init() {
		SnippetBase.load();
		snippetView = new SnippetView(this);
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

          sideMenu = new SideMenu(this);
          add(sideMenu, BorderLayout.WEST);

          bottomPane = new BottomPane(this);
          add(bottomPane, BorderLayout.SOUTH);

		recentsManager = new RecentsManager(this);

		splash.setProgress(77, "initializing");

		fileView = new FileView("File", this);
		settings = new omega.settings.Screen(this);
          universalSettings = new UniversalSettingsWizard(this);
		buildView = new BuildView("Build", this);
		runView = new RunView("Run", this, true);
		projectView = new ProjectView("Project", this);

		splitPane.setLeftComponent(projectView.getProjectView());
		splitPane.setDividerLocation(300);

		splash.setProgress(83, "plugging in");

		pluginManager = new PluginManager();
          pluginCenter = new PluginCenter(this);

		splash.setProgress(100, "");
		File file = new File(DataManager.getDefaultProjectPath());
       
          if(DataManager.getWorkspace().equals("") || !new File(DataManager.getWorkspace()).exists())
               new WorkspaceSelector(this).setVisible(true);
          
		if(file.exists() && file.isDirectory()) {
			loadProject(file);
		}
		else {
			launcher = new omega.launcher.Launcher();
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
				if(Screen.getFileView().getProjectPath() != null){
     				projectView.tree = new FileTree(Screen.getFileView().getProjectPath());
     				projectView.tree.gen(projectView.tree.getRoot());
				}
			}
			projectView.tree.repaint();

			screenHasProjectView = false;
			Screen.getProjectView().organizeProjectViewDefaults();
			doLayout();
			Screen.getProjectView().setVisible(false);
			screenHasProjectView = true;
			Screen.getProjectView().organizeProjectViewDefaults();
			doLayout();
			Screen.getProjectView().setVisible(false);
		}
		super.setVisible(value);
	}

     public void justVisible(boolean value){
     	super.setVisible(value);
     }

	public void setToView() {
		int x = splitPane.getDividerLocation();
		splitPane.setLeftComponent(projectView.getProjectView());
		Screen.getProjectView().getProjectView().setVisible(true);
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
			try{
			     Thread.sleep(time);
			}
			catch(Exception e) {
                    
		     }
			getScreen().loadTitle(getFileView().getProjectName());
			if(task != null)
				task.run();
		}).start();
	}

     public void manageTools(ProjectDataBase manager){
          toolMenu.structureViewComp.setVisible(!manager.non_java);
          toolMenu.sep4.setVisible(!manager.non_java);
          toolMenu.asteriskComp.setVisible(!manager.non_java);
          toolMenu.contentModeComp.setVisible(!manager.non_java);
          toolMenu.typeItem.setName(fileView.getProjectManager().non_java ? "Project Type : Non-Java" : "Project Type : Java");
          sideMenu.structureComp.setVisible(!manager.non_java);

          toolMenu.changeLocations(manager.non_java);
          sideMenu.changeLocations(manager.non_java);
     }

	public static void setStatus(String status, int value) {
		if(value != 100)
			Screen.getScreen().getBottomPane().setMessage(status);
		else
			Screen.getScreen().getBottomPane().setMessage("Status of any process running will appear here!");
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
          toolMenu.titleComp.setText(getTitle());
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
     	IDE.main(null);
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
		while(tokenizer.hasMoreTokens()) {
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

     @Override
     public void dispose(){
     	active = false;
          Screen.notify("Terminating Running Applications");
          try{
               for(Process p : runView.runningApps) {
                    if(p.isAlive())
                         p.destroyForcibly();
               }
          }
          catch(Exception e) {
               
          }
          Screen.notify("Saving UI and Data");
          uiManager.save();
          dataManager.saveData();
          SnippetBase.save();
          Screen.notify("Saving Project");
          saveAllEditors();
          try{
               getFileView().getProjectManager().save();
          }
          catch(Exception e2) {
               
          }
          super.dispose();
          System.exit(0);
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

     public static omega.settings.Screen getSettingsView() {
          return settings;
     }

     public static UniversalSettingsWizard getUniversalSettingsView() {
          return universalSettings;
     }
     
     public static TerminalComp getTerminalComp(){
          return terminal;
     }

     public static void pickTheme(String defaultTheme){
          picker.lightMode = defaultTheme.equals("light");
          picker.loadImage(defaultTheme + ".png");
          picker.manageTheme();
          picker.setVisible(true);
     }

	public ToolMenu getToolMenu() {
		return toolMenu;
	}    

     public SideMenu getSideMenu() {
          return sideMenu;
     }
     
     public BottomPane getBottomPane() {
          return bottomPane;
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

	public void moveTo(int x, int y) {
		try {
			if(robot == null)
				robot = new Robot();
			robot.mouseMove(x, y);
		}catch(Exception e) {}
	}

	public static UIManager getUIManager() {
		return uiManager;
	}

	public static DataManager getDataManager(){
		return dataManager;
	}

	public void saveAllEditors() {
          tabPanel.getEditors().forEach(w->w.saveCurrentFile());
          rightTabPanel.getEditors().forEach(w->w.saveCurrentFile());
          bottomTabPanel.getEditors().forEach(w->w.saveCurrentFile());
          if(DataManager.isSourceDefenderEnabled())
          	ToolMenu.sourceDefender.backupData();
	}

	public void loadThemes(){
          tabPanel.getEditors().forEach(w->w.loadTheme());
          rightTabPanel.getEditors().forEach(w->w.loadTheme());
          bottomTabPanel.getEditors().forEach(w->w.loadTheme());
	}

	public LinkedList<Editor> getAllEditors(){
		LinkedList<Editor> editors = new LinkedList<>();
		tabPanel.getEditors().forEach(editors::add);
		rightTabPanel.getEditors().forEach(editors::add);
		bottomTabPanel.getEditors().forEach(editors::add);
		return editors;
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

	public static PluginManager getPluginManager() {
		return pluginManager;
	}

     public static PluginCenter getPluginCenter(){
          return pluginCenter;
     }

	public void saveEssential() {
		uiManager.save();
		dataManager.saveData();
		notify("Saving Project");
		saveAllEditors();
		hideNotif();
	}

	public static boolean onWindows(){
		return File.pathSeparator.equals(";");
	}
	
	public static void main(String[] args){
		new Screen();
	}
}

