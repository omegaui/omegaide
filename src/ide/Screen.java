package ide;
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
import ide.utils.Accessories;
import ide.utils.DataManager;
import ide.utils.Editor;
import ide.utils.RecentsManager;
import ide.utils.ToolMenu;
import ide.utils.UIManager;
import ide.utils.systems.BuildView;
import ide.utils.systems.EditorTools;
import ide.utils.systems.FileView;
import ide.utils.systems.OperationPane;
import ide.utils.systems.ProjectView;
import ide.utils.systems.RunView;
import launcher.Launcher;
import plugin.PluginManager;
import plugin.PluginView;
import plugin.PluginStore;
import snippet.SnippetBase;
import snippet.SnippetView;
import startup.Startup;
import tabPane.IconManager;
import tabPane.TabPanel;
import tree.FileTree;
import ui.View;
import uiPool.Notification;
import update.Updater;

public class Screen extends JFrame {
	private static Accessories accessories;
	public JSplitPane splitPane;
	public JSplitPane compilancePane;
	private OperationPane operationPane;
	private TabPanel tabPanel;
	public EditorTools tools;
	private ToolMenu toolMenu;
	private UIManager uiManager;
	private DataManager dataManager;
	private static RecentsManager recentsManager;
	private static FileView fileView;
	private static BuildView buildView;
	private static RunView runView;
	private static ProjectView projectView;
	private static View settings;
	public static final String VERSION = "v1.1";
	private SplashScreen splash;
	private Robot robot;
	public volatile boolean screenHasProjectView = true;
	private static Notification notif;
	private static ErrorHighlighter errorHighlighter;
	private static BasicHighlight basicHighlight;
	public volatile boolean active = true;
	public static Launcher launcher;
	public static SnippetView snippetView; 
	private static PluginManager pluginManager;
	private static PluginView pluginView;
     private static PluginStore pluginStore;
	private static Updater updater;

	public Screen() {
		String l$F = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
		if(System.getProperty("os.name").contains("indows"))
			l$F = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
			javax.swing.UIManager.setLookAndFeel(l$F);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/UbuntuMono-Bold.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Ubuntu-Bold.ttf")));
		} catch (Exception e1) {
			System.out.println(e1);
		}
		Startup.checkStartup(this);
		UIManager.loadHighlight();
		uiManager = new UIManager(this);
		UIManager.setData(this);
		splash = new SplashScreen();
		splash.setProgress(10, "welcome");
		notif = new Notification(this);
		splash.setProgress(37, "welcome");

		setIconImage(IconManager.getImageIcon("/omega_ide_icon64.png").getImage());
		setTitle("Omega Integrated Development Environment "+VERSION);
		setLayout(new BorderLayout());
		setSize(1000, 650);
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
		SnippetBase.load();
		snippetView = new SnippetView(this);
		updater = new Updater(this);
		errorHighlighter = new ErrorHighlighter();
		basicHighlight = new BasicHighlight();

		operationPane = new OperationPane(this);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		UIManager.setData(splitPane);
		compilancePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		compilancePane.setTopComponent(splitPane);
		compilancePane.setBottomComponent(operationPane);
		compilancePane.setDividerSize(2);
		compilancePane.setDividerLocation(Screen.this.getHeight() - 400);
		add(compilancePane, BorderLayout.CENTER);
		UIManager.setData(compilancePane);

		tabPanel = new TabPanel(this);
		splitPane.setRightComponent(tabPanel);
		splitPane.setDividerSize(2);

		accessories = new Accessories(this);

		toolMenu = new ToolMenu(this);
		add(toolMenu, BorderLayout.NORTH);
		
		recentsManager = new RecentsManager(this);

		splash.setProgress(77, "initializing");

		fileView = new FileView("File", this);
		settings = new View(this);
		buildView = new BuildView("Build", this);
		runView = new RunView("Run", this, true);
		projectView = new ProjectView("Project", this);
		dataManager = new DataManager(this);
		if(((Color)(javax.swing.UIManager.getDefaults().get("Button.background"))).getRed() <= 53)
			DataManager.setEditorColoringScheme("dark");
		else DataManager.setEditorColoringScheme("idea");


		tools = new EditorTools();
		
		splitPane.setLeftComponent(projectView.getProjectView());
		splitPane.setDividerLocation(300);

		splash.setProgress(83, "plugging in");
		
		pluginManager = new PluginManager();
		pluginView = new PluginView(this);
          pluginStore = new PluginStore();

		splash.setProgress(100, "");
		File file = new File(DataManager.getDefaultProjectPath());
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

	public void revoke() {
		boolean wasExtended = false;
		if(getExtendedState() == MAXIMIZED_BOTH) wasExtended = true;
		setSize((int)getSize().width + 1, (int)getSize().height);
		setSize((int)getSize().width - 1, (int)getSize().height);
		if(wasExtended) setExtendedState(MAXIMIZED_BOTH);
	}

	public void setToView() {
		splitPane.setLeftComponent(projectView.getProjectView());
		splitPane.setDividerLocation(300);
	}

	public void setToNull() {
		splitPane.remove(projectView.getProjectView());
	}

	public static void notify(String text) {
		notif.setText(text);
	}

	public static void hideNotif() {
		notif.setVisible(false);
	}

	public static void notify(String text, long time, Runnable task) {
		notif.setText(text);
		new Thread(()->{
			try{Thread.sleep(time);}catch(Exception e) {System.out.println(e.getMessage());}
			notif.setVisible(false);
			if(task != null)
				task.run();
		}).start();
	}

	public static void setStatus(String status, int value) {
		if(value != 100)
			Screen.getScreen().getToolMenu().setMsg(status + " " + value + "%");
		else
			Screen.getScreen().getToolMenu().setMsg(null);
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		for(Component c : getComponents())
			c.repaint();
	}

	public static void reverseColors(Component c) {
		Color l = c.getBackground();
		c.setBackground(c.getForeground());
		c.setForeground(l);
	}

	public void setProject(String projectName)
	{
		setTitle(projectName+" -Omega IDE "+VERSION);
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

	public static void addAndSaveRecents(String path) {
		RecentsManager.add(path);
		recentsManager.saveData();
	}

	public void loadProject(File file)
	{
		fileView.saveAll();
		fileView.setProjectPath(file.getAbsolutePath());
		Screen.hideNotif();
	}

	public boolean isFileOpened(File file)
	{
		for(Editor e : tabPanel.getEditors())
		{
			if(e.currentFile != null) {
				if(e.currentFile.getAbsolutePath().equals(file.getAbsolutePath())){
					return true;
				}
			}
		}
		return false;
	}

	public String getPackName(File file)
	{
		String res = "";
		boolean canRecord = false;
		StringTokenizer tokenizer = new StringTokenizer(file.getAbsolutePath(), "/");
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

	public static RunView getRunView()
	{
		return runView;
	}

	public static ProjectView getProjectView()
	{
		return projectView;
	}

	public static ErrorHighlighter getErrorHighlighter() {
		return errorHighlighter;
	}

	public static BasicHighlight getBasicHighlight() {
		return basicHighlight;
	}

	public static View getSettingsView()
	{
		return settings;
	}

	public static Accessories getAccessories()
	{
		return accessories;
	}

	public ToolMenu getToolMenu() {
		return toolMenu;
	}

	public OperationPane getOperationPanel() {
		return operationPane;
	}

	public void pressKey(int code)
	{
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

	public void saveAllEditors()
	{
		tabPanel.getEditors().forEach(w->w.saveCurrentFile());
	}

	public void loadThemes()
	{
		tabPanel.getEditors().forEach(w->w.loadTheme());
	}

	public Editor getCurrentEditor() {
		return tabPanel.getCurrentEditor();
	}

	public void closeCurrentProject() {
		tabPanel.closeAllTabs();
	}

	public TabPanel getTabPanel() {
		return tabPanel;
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

	public void saveEssential() {
		uiManager.save();
		dataManager.saveData();
		notify("Saving Project");
		saveAllEditors();
		hideNotif();
	}
}
