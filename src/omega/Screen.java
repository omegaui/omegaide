/*
 * The Main Window
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

package omega;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import omega.instant.support.LanguageTagView;
import omega.instant.support.build.gradle.GradleProcessManager;
import omega.instant.support.java.generator.Generator;
import omega.instant.support.java.highlighter.ErrorHighlighter;
import omega.instant.support.universal.UniversalSettingsWizard;
import omega.io.UIManager;
import omega.io.*;
import omega.plugin.event.PluginReactionEvent;
import omega.plugin.event.PluginReactionManager;
import omega.plugin.management.PluginManager;
import omega.plugin.management.PluginsView;
import omega.plugin.store.PluginStore;
import omega.ui.component.Editor;
import omega.ui.component.FileTreeBranch;
import omega.ui.component.TerminalComp;
import omega.ui.component.ToolMenu;
import omega.ui.dialog.*;
import omega.ui.panel.*;
import omegaui.component.UBRFrame;
import omegaui.listener.KeyStrokeListener;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.Objects;
import java.util.StringTokenizer;

import static java.awt.event.KeyEvent.*;
public class Screen extends UBRFrame {

	public KeyStrokeListener ideWideKeyListener;
	
	public SplitPanel splitPane;
	public SplitPanel compilancePane;
	public SplitPanel rightTabPanelSplitPane;
	public SplitPanel bottomTabPanelSplitPane;

	public Editor focussedEditor;

	public static Launcher launcher;

	public static SnippetView snippetView;

	public static final String VERSION = "v2.3";
	public static String PATH_SEPARATOR = ":";

	public volatile boolean active = true;
	public volatile boolean focusMode = false;

	private final SplashScreen splash;
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
	private static ProjectFile projectFile;
	private static ProjectRunner projectRunner;
	private static ProjectBuilder projectBuilder;
	private static RecentsManager recentsManager;
	private static ErrorHighlighter errorHighlighter;
	private static UniversalSettingsWizard universalSettings;
	private static PluginManager pluginManager;
	private static PluginStore pluginStore;
	private static PluginsView pluginsView;
	private static PluginReactionManager pluginReactionManager;
	private static TerminalComp terminal;
	private static ThemePicker picker;
	private static AnimationsDialog animationsDialog;

	public Screen() {
		try {
			setIconImage(javax.imageio.ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/omega_ide_icon500.png"))));

			Startup.writeUIFiles();
			if(!File.separator.equals("/"))
				PATH_SEPARATOR = ";";
			dataManager = new DataManager(this);
			Color x;
			Color y;
			if(UIManager.isDarkMode()) {
				FlatDarkLaf.install();
				x = Color.decode("#24d673");
				y = Color.decode("#2A2A2A");
				javax.swing.UIManager.put("ToolTip.foreground", new ColorUIResource(Color.WHITE));
				javax.swing.UIManager.put("ToolTip.background", new ColorUIResource(y));
				javax.swing.UIManager.put("Button.foreground", new ColorUIResource(Color.WHITE));
				javax.swing.UIManager.put("Button.background", new ColorUIResource(y));
				javax.swing.UIManager.put("Label.foreground", new ColorUIResource(Color.WHITE));
				javax.swing.UIManager.put("Label.background", new ColorUIResource(y));
			}
			else {
				FlatLightLaf.install();
				x = UIManager.TOOLMENU_COLOR6;
				y = Color.WHITE;
				javax.swing.UIManager.put("ToolTip.foreground", new ColorUIResource(Color.BLACK));
				javax.swing.UIManager.put("ToolTip.background", new ColorUIResource(UIManager.back2));
				javax.swing.UIManager.put("Button.foreground", new ColorUIResource(Color.BLACK));
				javax.swing.UIManager.put("Button.background", new ColorUIResource(UIManager.back2));
				javax.swing.UIManager.put("Label.foreground", new ColorUIResource(Color.BLACK));
				javax.swing.UIManager.put("Label.background", new ColorUIResource(UIManager.back2));
			}
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getResourceAsStream("/Ubuntu-Bold.ttf"))));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getResourceAsStream("/UbuntuMono-Bold.ttf"))));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getResourceAsStream("/JetBrainsMono-Regular.ttf"))));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getResourceAsStream("/JetBrainsMono-Bold.ttf"))));

			javax.swing.UIManager.put("ToolTip.font", omega.io.UIManager.PX14);
			javax.swing.UIManager.put("Button.font", omega.io.UIManager.PX14);
			javax.swing.UIManager.put("Label.font", omega.io.UIManager.PX14);
			javax.swing.UIManager.put("ScrollBar.thumb", new ColorUIResource(x));
			javax.swing.UIManager.put("ScrollBar.track", new ColorUIResource(y));
			javax.swing.UIManager.put("ScrollPane.background", new ColorUIResource(y));
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}

		picker = new ThemePicker(this);
		uiManager = new UIManager(this);
		Startup.checkStartup(this);
		UIManager.loadHighlight();
		UIManager.setData(this);

		setBorderColor(UIManager.back3);

		animationsDialog = new AnimationsDialog(this);

		splash = new SplashScreen();
		splash.setProgress(10, "welcome");
		Generator.init(this);
		splash.setProgress(37, "welcome");

		setLayout(new BorderLayout());
		setSize(UIManager.getMainWindowWidth(), UIManager.getMainWindowHeight());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println(getBounds());
				dispose();
			}
		});
		
		splash.setProgress(60, "initializing");

		ideWideKeyListener = new KeyStrokeListener(this);
		
		KeyEventDispatcher dispatcher = e -> {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				ideWideKeyListener.keyPressed(e);
			}
			else if (e.getID() == KeyEvent.KEY_RELEASED) {
				ideWideKeyListener.keyReleased(e);
			}
			else if (e.getID() == KeyEvent.KEY_TYPED) {
				ideWideKeyListener.keyTyped(e);
			}
			return false;
		};
		
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(dispatcher);
		
		initKeyStrokes();
		init();
	}

	public void initKeyStrokes(){
		ideWideKeyListener.putKeyStroke(this::showFileWizard, VK_CONTROL, VK_T).setStopKeys(VK_SHIFT).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::triggerBuild, VK_CONTROL, VK_B).setStopKeys(VK_SHIFT);
		ideWideKeyListener.putKeyStroke(this::triggerRun, VK_CONTROL, VK_SHIFT, VK_R);
		ideWideKeyListener.putKeyStroke(this::triggerInstantRun, VK_CONTROL, VK_SHIFT, VK_F1);
		ideWideKeyListener.putKeyStroke(this::showSearchDialog, VK_CONTROL, VK_SHIFT, VK_P).useAutoReset();
		ideWideKeyListener.putKeyStroke((e)->ToolMenu.recentsDialog.setVisible(true), VK_CONTROL, VK_SHIFT, VK_M).setStopKeys(VK_ALT).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::showOpenProjectDialog, VK_CONTROL, VK_O).setStopKeys(VK_SHIFT, VK_ALT).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::showOpenGitProjectDialog, VK_CONTROL, VK_G).setStopKeys(VK_SHIFT, VK_ALT).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::showOpenFileDialog, VK_CONTROL, VK_ALT, VK_O).setStopKeys(VK_SHIFT).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::showNewProjectDialog, VK_CONTROL, VK_N).setStopKeys(VK_SHIFT).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::showNewUniversalProjectDialog, VK_CONTROL, VK_SHIFT, VK_N).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::showNewTerminal, VK_ALT, VK_SHIFT, VK_T).setStopKeys(VK_CONTROL).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::toggleProcessPanel, VK_ALT, VK_P).setStopKeys(VK_SHIFT).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::refreshFileTree, VK_ALT, VK_R).setStopKeys(VK_SHIFT).useAutoReset();
		ideWideKeyListener.putKeyStroke(this::showSettings, VK_CONTROL, VK_ALT, VK_S).setStopKeys(VK_SHIFT).useAutoReset();
		ideWideKeyListener.putKeyStroke((e)->saveAllEditors(), VK_CONTROL, VK_SHIFT, VK_S).setStopKeys(VK_ALT).useAutoReset();

		// Binding Alt + <Context Menu Character>
		ideWideKeyListener.putKeyStroke((e)->toolMenu.showFilePopup(), VK_ALT, VK_F).setStopKeys(VK_SHIFT, VK_CONTROL).useAutoReset();
		ideWideKeyListener.putKeyStroke((e)->toolMenu.showProjectPopup(), VK_ALT, VK_SHIFT, VK_P).setStopKeys(VK_CONTROL).useAutoReset();
		ideWideKeyListener.putKeyStroke((e)->toolMenu.showCodePopup(), VK_ALT, VK_C).setStopKeys(VK_SHIFT, VK_CONTROL).useAutoReset();
		ideWideKeyListener.putKeyStroke((e)->toolMenu.showToolsPopup(), VK_ALT, VK_T).setStopKeys(VK_SHIFT, VK_CONTROL).useAutoReset();
		ideWideKeyListener.putKeyStroke((e)->toolMenu.showSettingsPopup(), VK_ALT, VK_S).setStopKeys(VK_SHIFT, VK_CONTROL).useAutoReset();
		ideWideKeyListener.putKeyStroke((e)->toolMenu.showHelpPopup(), VK_ALT, VK_H).setStopKeys(VK_SHIFT, VK_CONTROL).useAutoReset();
	}

	public void showFileWizard(KeyEvent e){
		projectFile.getFileCreator().show();
		e.consume();
	}
	
	public void triggerBuild(KeyEvent e){
		if(toolMenu.buildComp.isClickable()){
			if(GradleProcessManager.isGradleProject())
				GradleProcessManager.build();
			else
				projectBuilder.compileProject();

			e.consume();
		}
	}

	public void triggerRun(KeyEvent e){
		if(toolMenu.buildComp.isClickable()){
			if(GradleProcessManager.isGradleProject())
				GradleProcessManager.run();
			else
				projectRunner.run();

			e.consume();
		}
	}

	public void triggerInstantRun(KeyEvent e){
		if(toolMenu.buildComp.isClickable()){
			projectRunner.instantRun();

			e.consume();
		}
	}

	public void showSearchDialog(KeyEvent e){
		projectFile.getSearchWindow().setVisible(true);

		e.consume();
	}

	public void showOpenProjectDialog(KeyEvent e){
		projectFile.open("Project");

		e.consume();
	}

	public void showOpenGitProjectDialog(KeyEvent e){
		e.consume();
		
		ToolMenu.gitProjectWizard.setVisible(true);
	}

	public void showOpenFileDialog(KeyEvent e){
		projectFile.open("File");

		e.consume();
	}

	public void showNewProjectDialog(KeyEvent e){
		ToolMenu.javaProjectWizard.setVisible(true);

		e.consume();
	}

	public void showNewUniversalProjectDialog(KeyEvent e){
		ToolMenu.universalProjectWizard.setVisible(true);

		e.consume();
	}
	
	public void showNewTerminal(KeyEvent e){
		terminal.showJetTerminal();

		e.consume();
	}
	
	public void toggleProcessPanel(KeyEvent e){
		operationPane.setVisible(!operationPane.isVisible());

		e.consume();
	}

	public void showSettings(KeyEvent e){
		sideMenu.showSettings();
		
		e.consume();
	}
	
	public void refreshFileTree(KeyEvent e){
		projectFile.getFileTreePanel().refresh();

		e.consume();
	}

	private void init() {
		SnippetBase.load();
		snippetView = new SnippetView(this);
		errorHighlighter = new ErrorHighlighter();
		operationPane = new OperationPane(this);
		terminal = new TerminalComp();

		rightTabPanelSplitPane = new SplitPanel(JSplitPane.HORIZONTAL_SPLIT);
		bottomTabPanelSplitPane = new SplitPanel(JSplitPane.VERTICAL_SPLIT);
		splitPane = new SplitPanel(JSplitPane.HORIZONTAL_SPLIT);

		rightTabPanelSplitPane.setBorder(null);
		bottomTabPanelSplitPane.setBorder(null);
		splitPane.setBorder(null);

		UIManager.setData(splitPane);
		UIManager.setData(rightTabPanelSplitPane);
		UIManager.setData(bottomTabPanelSplitPane);

		compilancePane = new SplitPanel(JSplitPane.VERTICAL_SPLIT);
		compilancePane.setTopComponent(splitPane);
		compilancePane.setBottomComponent(operationPane);
		compilancePane.setDividerLocation(Screen.this.getHeight() - 400);
		add(compilancePane, BorderLayout.CENTER);
		UIManager.setData(compilancePane);

		tabPanel = new TabPanel(TabPanel.TAB_LOCATION_TOP);
		rightTabPanel = new TabPanel(TabPanel.TAB_LOCATION_TOP);
		bottomTabPanel = new TabPanel(TabPanel.TAB_LOCATION_TOP);

		rightTabPanel.setHideOnEmpty(true);
		bottomTabPanel.setHideOnEmpty(true);

		splitPane.setRightComponent(bottomTabPanelSplitPane);

		rightTabPanelSplitPane.setLeftComponent(tabPanel);
		rightTabPanelSplitPane.setRightComponent(rightTabPanel);

		bottomTabPanelSplitPane.setTopComponent(rightTabPanelSplitPane);
		bottomTabPanelSplitPane.setBottomComponent(bottomTabPanel);

		toolMenu = new ToolMenu(this);
		add(toolMenu, BorderLayout.NORTH);

		sideMenu = new SideMenu(this);
		add(sideMenu, BorderLayout.WEST);

		bottomPane = new BottomPane(this);
		add(bottomPane, BorderLayout.SOUTH);

		recentsManager = new RecentsManager(this);

		splash.setProgress(77, "initializing");

		projectFile = new ProjectFile(this);

		universalSettings = new UniversalSettingsWizard(this);

		projectBuilder = new ProjectBuilder(this);

		projectRunner = new ProjectRunner(this);

		splitPane.setDividerLocation(300);
		splitPane.setLeftComponent(projectFile.getFileTreePanel());

		splash.setProgress(83, "plugging in");

		pluginManager = new PluginManager();
		pluginManager.load();
		pluginStore = new PluginStore(this, pluginManager);
		pluginsView = new PluginsView(this, pluginManager);
		pluginReactionManager = new PluginReactionManager(pluginManager);
		pluginManager.doPluginReactionRegistration();

		getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_INITIALIZED, this, splash));

		splash.setProgress(100, "");

		File file = new File(DataManager.getDefaultProjectPath());

		if(DataManager.getWorkspace().equals("") || !new File(DataManager.getWorkspace()).exists())
			new WorkspaceSelector(this).setVisible(true);

		if(file.exists() && file.isDirectory()) {
			loadProject(file);
		}
		else {
			launcher = new Launcher();
			launcher.setVisible(true);
		}
	}

	public void setLeftComponent(Component c){
		if(splitPane.getLeftComponent() != null){
			splitPane.getLeftComponent().setVisible(false);
		}

		splitPane.setLeftComponent(c);
		c.setVisible(true);
	}

	public void setLeftComponentDoNotTouchVisibility(Component c){
		if(splitPane.getLeftComponent() != null){
			splitPane.getLeftComponent().setVisible(false);
		}

		splitPane.setLeftComponent(c);
	}

	public void toggleLeftComponent(Component c){
		if(splitPane.getLeftComponent() != c){
			setLeftComponent(c);
		}
		else{
			c.setVisible(!c.isVisible());
		}
	}

	public void toggleFileTree(){
		toggleLeftComponent(projectFile.getFileTreePanel());
	}

	public void manageTools(ProjectDataBase manager){
		ToolMenu.pathBox.prepareEdges();
		toolMenu.langComp.image = LanguageTagView.getRespectiveTagImage(manager.getLanguageTag());
		toolMenu.langComp.repaint();
		toolMenu.projectPopup.setEnabled("Manage Build-Path", !manager.isLanguageTagNonJava());
		toolMenu.projectPopup.setEnabled("Add Additional Flags", !manager.isLanguageTagNonJava());
		toolMenu.toolsPopup.setEnabled("Generate Getter/Setter", !manager.isLanguageTagNonJava());
		toolMenu.toolsPopup.setEnabled("Override/Implement Methods", !manager.isLanguageTagNonJava());
		toolMenu.changeLocations(manager.isLanguageTagNonJava());
		sideMenu.resize(manager.isLanguageTagNonJava());
	}

	public static void setStatus(String status, int value, BufferedImage image) {
		if(value != 100)
			Screen.getScreen().getBottomPane().setMessage(status, image);
		else
			Screen.getScreen().getBottomPane().setMessage("Status of any running process will appear here!", image, "running process");
	}

	public static void setStatus(String status, int value, BufferedImage image, String... highlightTexts) {
		if(value != 100)
			Screen.getScreen().getBottomPane().setMessage(status, image, highlightTexts);
		else
			Screen.getScreen().getBottomPane().setMessage("Status of any running process will appear here!", image, "running process");
	}

	@Override
	public void setSize(int width, int height){
		super.setSize(width, height);
		try{
			Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_DO_LAYOUT, this, getContentPane()));
		}
		catch(Exception ignored){

		}
	}

// This was required to overcome high CPU usage
//	@Override
//	public void layout(){
//		super.layout();
//		try{
//			Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_DO_LAYOUT, this, getContentPane()));
//		}
//		catch(Exception e){
//
//		}
//	}

//	@Override
//	public void paint(Graphics g) {
//		try{
//			Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_DO_LAYOUT, g, getContentPane()));
//		}
//		catch(Exception e){
//
//		}
//		super.paint(g);
//		g.dispose();
//	}


	public void loadTitle(String projectName) {
		setTitle(projectName + " -Omega IDE " + VERSION);
	}

	public void setProject(String projectName) {
		loadTitle(projectName);
	}

	public static void openInDesktop(File file) {
		try {
			new Thread(()->{
				try {
					java.awt.Desktop.getDesktop().open(file);
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}).start();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void openInTerminal(File file) {
		try {
			new Thread(()->{
				try {
					if(file.isDirectory())
						terminal.showJetTerminal(file);
					else
						ToolMenu.processWizard.launch(file);
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}).start();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Editor loadFile(File file) {
		String fn = file.getName();
		if(fn.endsWith(".pdf") || fn.endsWith(".deb")){
			openInDesktop(file);
			return null;
		}
		if(tabPanel.viewImage(file)) return null;
		if(tabPanel.viewArchive(file)) return null;
		if(isFileOpened(file)){
			Editor e = getEditor(file);
			tabPanel.showTab(e.getAttachment());
			rightTabPanel.showTab(e.getAttachment());
			bottomTabPanel.showTab(e.getAttachment());
			e.grabFocus();
			return e;
		}
		new Thread(()->Screen.addAndSaveRecents(file.getAbsolutePath())).start();
		Editor editor = new Editor(this);
		editor.loadFile(file);
		editor.setTabHolderPanel(tabPanel.addTab(file.getName(), file.getAbsolutePath(), getPackName(file), FileTreeBranch.getPreferredImageForFile(file), editor.getAttachment(), FileTreeBranch.getPreferredColorForFile(file), editor::closeFile, PopupManager.createMenu(editor)));
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
		if(isFileOpened(file)){
			Editor e = getEditor(file);
			tabPanel.showTab(e.getAttachment());
			rightTabPanel.showTab(e.getAttachment());
			bottomTabPanel.showTab(e.getAttachment());
			e.grabFocus();
			return e;
		}
		new Thread(()->Screen.addAndSaveRecents(file.getAbsolutePath())).start();
		Editor editor = new Editor(this);
		editor.loadFile(file);
		editor.setTabHolderPanel(rightTabPanel.addTab(file.getName(), file.getAbsolutePath(), getPackName(file), FileTreeBranch.getPreferredImageForFile(file), editor.getAttachment(), FileTreeBranch.getPreferredColorForFile(file), editor::closeFile, PopupManager.createMenu(editor)));
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
		if(isFileOpened(file)){
			Editor e = getEditor(file);
			tabPanel.showTab(e.getAttachment());
			rightTabPanel.showTab(e.getAttachment());
			bottomTabPanel.showTab(e.getAttachment());
			e.grabFocus();
			return e;
		}
		new Thread(()->Screen.addAndSaveRecents(file.getAbsolutePath())).start();
		Editor editor = new Editor(this);
		editor.loadFile(file);
		editor.setTabHolderPanel(bottomTabPanel.addTab(file.getName(), file.getAbsolutePath(), getPackName(file), FileTreeBranch.getPreferredImageForFile(file), editor.getAttachment(), FileTreeBranch.getPreferredColorForFile(file), editor::closeFile, PopupManager.createMenu(editor)));
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
		setVisible(true);
		
		projectFile.saveAll();
		
		projectFile.setProjectPath(file.getAbsolutePath());
	}

	public boolean isFileOpened(File file) {
		LinkedList<Editor> allEditors = getAllEditors();
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

	public Editor getEditor(File file) {
		LinkedList<Editor> allEditors = getAllEditors();
		for(Editor e : allEditors) {
			if(e.currentFile != null) {
				if(e.currentFile.getAbsolutePath().equals(file.getAbsolutePath())){
					allEditors.clear();
					return e;
				}
			}
		}
		return null;
	}

	public static String getPackName(File file) {
		StringBuilder res = new StringBuilder();
		boolean canRecord = false;
		StringTokenizer tokenizer = new StringTokenizer(file.getAbsolutePath(), File.separator);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(canRecord)
				res.append(token).append(File.separator);
			else if(token.equals(projectFile.getProjectName()))
				canRecord = true;
		}
		if(!canRecord)
			return file.getAbsolutePath();
		else if(!res.toString().trim().equals(""))
			res = new StringBuilder(res.substring(0, res.length() - 1));
		return res.toString();
	}

	public void justDispose(){
		super.dispose();
	}

	@Override
	public void dispose(){
		active = false;
		try{
			for(Process p : projectRunner.runningApps) {
				if(p.isAlive())
					p.destroyForcibly();
			}
		}
		catch(Exception ignored) {

		}
		saveAllBookmarks();
		try{
			projectFile.getProjectManager().save();
		}
		catch(Exception e2) {
			e2.printStackTrace();
		}
		saveAllEditors();
		pluginManager.save();
		uiManager.save();
		dataManager.saveData();
		if(projectFile.getProjectManager().isLanguageTagNonJava()){
			if(projectFile.getArgumentManager() != null)
				projectFile.getArgumentManager().save();
			else{
				System.out.println("This project doesn't contains any arguments to be saved! ... skipping ArgumentManager.save()");
			}
		}
		SnippetBase.save();
		getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_CLOSING, this, 0));
		super.dispose();
		System.exit(0);
	}

	public static Screen getScreen() {
		return Screen.getProjectFile().getScreen();
	}

	public static RecentsManager getRecentsManager() {
		return recentsManager;
	}

	public static ProjectFile getProjectFile() {
		return projectFile;
	}

	public static ProjectRunner getProjectRunner() {
		return projectRunner;
	}

	public static ProjectBuilder getProjectBuilder() {
		return projectBuilder;
	}

	public static ErrorHighlighter getErrorHighlighter() {
		return errorHighlighter;
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

	public static void showAnimationsDialog(){
		animationsDialog.setVisible(true);
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
		}
		catch(Exception ignored) {

		}
	}

	public void moveTo(int x, int y) {
		try {
			if(robot == null)
				robot = new Robot();
			robot.mouseMove(x, y);
		}
		catch(Exception ignored) {

		}
	}

	public Robot getRobot(){
		try{
			if(robot == null)
				robot = new Robot();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return robot;
	}

	public static UIManager getUIManager() {
		return uiManager;
	}

	public static DataManager getDataManager(){
		return dataManager;
	}

	public void saveAllBookmarks() {
		tabPanel.getEditors().forEach(BookmarksManager::saveBookmarks);
		rightTabPanel.getEditors().forEach(BookmarksManager::saveBookmarks);
		bottomTabPanel.getEditors().forEach(BookmarksManager::saveBookmarks);
	}

	public void saveAllEditors() {
		if(DataManager.isSourceDefenderEnabled())
			ToolMenu.sourceDefender.backupData();
		tabPanel.getEditors().forEach(Editor::saveCurrentFile);
		rightTabPanel.getEditors().forEach(Editor::saveCurrentFile);
		bottomTabPanel.getEditors().forEach(Editor::saveCurrentFile);
	}

	public void loadThemes(){
		tabPanel.getEditors().forEach(Editor::loadTheme);
		rightTabPanel.getEditors().forEach(Editor::loadTheme);
		bottomTabPanel.getEditors().forEach(Editor::loadTheme);
	}

	public LinkedList<Editor> getAllEditors(){
		LinkedList<Editor> editors = new LinkedList<>();
		editors.addAll(tabPanel.getEditors());
		editors.addAll(rightTabPanel.getEditors());
		editors.addAll(bottomTabPanel.getEditors());
		return editors;
	}

	public Editor getCurrentEditor() {
		return focussedEditor;
	}

	public void closeCurrentProject() {
		closeAllTabs();
	}

	public void closeAllTabs(){
		tabPanel.closeAllTabs();
		rightTabPanel.closeAllTabs();
		bottomTabPanel.closeAllTabs();
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

	public static PluginStore getPluginStore(){
		return pluginStore;
	}

	public static PluginsView getPluginsView(){
		return pluginsView;
	}

	public static PluginReactionManager getPluginReactionManager(){
		return pluginReactionManager;
	}

	public synchronized boolean isFocusMode() {
		return focusMode;
	}

	public synchronized void setFocusMode(boolean focusMode) {
		this.focusMode = focusMode;
		final int state = getExtendedState();
		Thread tx = new Thread(()->{
			setStatus("Toggling Focus Mode ...", 0, IconManager.fluentfocusImage);
			if(focusMode){
				setVisible(false);
				remove(toolMenu);
				doLayout();
				setVisible(true);
			}
			else{
				setVisible(false);
				add(toolMenu, BorderLayout.NORTH);
				doLayout();
				setVisible(true);
			}
			doLayout();

			setStatus(null, 100, null);
		});

		tx.start();

		try{
			tx.join();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		setExtendedState(state);
	}

	public void saveEssential() {
		uiManager.save();
		dataManager.saveData();
		saveAllEditors();
	}

	public static boolean onWindows(){
		return File.pathSeparator.equals(";");
	}

	public synchronized static boolean isNotNull(String text){
		return text != null && !text.isBlank();
	}

	public KeyStrokeListener getIdeWideKeyListener() {
		return ideWideKeyListener;
	}
	
}
