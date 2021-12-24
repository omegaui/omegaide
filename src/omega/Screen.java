/**
* The Main Window
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
package omega;

import static java.awt.event.KeyEvent.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JSplitPane;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import omega.comp.Animations;
import omega.gset.Generator;
import omega.highlightUnit.BasicHighlight;
import omega.highlightUnit.ErrorHighlighter;
import omega.instant.support.LanguageTagView;
import omega.instant.support.build.gradle.GradleProcessManager;
import omega.instant.support.universal.UniversalSettingsWizard;
import omega.launcher.Launcher;
import omega.plugin.event.PluginReactionEvent;
import omega.plugin.event.PluginReactionManager;
import omega.plugin.management.PluginManager;
import omega.plugin.management.PluginsView;
import omega.plugin.store.PluginStore;
import omega.snippet.SnippetBase;
import omega.snippet.SnippetView;
import omega.startup.Startup;
import omega.tabPane.TabPanel;
import omega.terminal.TerminalComp;
import omega.utils.AnimationsDialog;
import omega.utils.BottomPane;
import omega.utils.DataManager;
import omega.utils.Editor;
import omega.utils.IconManager;
import omega.utils.ProjectDataBase;
import omega.utils.RecentsManager;
import omega.utils.SideMenu;
import omega.utils.SplitPanel;
import omega.utils.ThemePicker;
import omega.utils.ToolMenu;
import omega.utils.UIManager;
import omega.utils.WorkspaceSelector;
import omega.utils.systems.BuildView;
import omega.utils.systems.FileView;
import omega.utils.systems.OperationPane;
import omega.utils.systems.RunView;

public class Screen extends JFrame {

  public SplitPanel splitPane;
  public SplitPanel compilancePane;
  public SplitPanel rightTabPanelSplitPane;
  public SplitPanel bottomTabPanelSplitPane;

  public Editor focussedEditor;

  public static Launcher launcher;

  public static SnippetView snippetView;

  public static final String VERSION = "v2.2";
  public static String PATH_SEPARATOR = ":";

  public volatile boolean active = true;
  public volatile boolean focusMode = false;

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
  private static UniversalSettingsWizard universalSettings;
  private static PluginManager pluginManager;
  private static PluginStore pluginStore;
  private static PluginsView pluginsView;
  private static PluginReactionManager pluginReactionManager;
  private static TerminalComp terminal;
  private static ThemePicker picker;
  private static AnimationsDialog animationsDialog;

  public Screen() {
    setUndecorated(true);
    try {
      setIconImage(
        javax.imageio.ImageIO.read(
          getClass().getResourceAsStream("/omega_ide_icon500.png")
        )
      );

      Startup.writeUIFiles();
      if (!File.separator.equals("/")) PATH_SEPARATOR = ";";
      dataManager = new DataManager(this);
      Color x = null;
      Color y = null;
      if (UIManager.isDarkMode()) {
        FlatDarkLaf.install();
        x = Color.decode("#D34D42");
        y = Color.decode("#1e1e1e");
        javax.swing.UIManager.put(
          "ToolTip.foreground",
          new ColorUIResource(Color.WHITE)
        );
        javax.swing.UIManager.put("ToolTip.background", new ColorUIResource(y));
        javax.swing.UIManager.put(
          "Button.foreground",
          new ColorUIResource(Color.WHITE)
        );
        javax.swing.UIManager.put("Button.background", new ColorUIResource(y));
        javax.swing.UIManager.put(
          "Label.foreground",
          new ColorUIResource(Color.WHITE)
        );
        javax.swing.UIManager.put("Label.background", new ColorUIResource(y));
      } else {
        FlatLightLaf.install();
        x = UIManager.TOOLMENU_COLOR3;
        y = Color.WHITE;
        javax.swing.UIManager.put(
          "ToolTip.foreground",
          new ColorUIResource(Color.BLACK)
        );
        javax.swing.UIManager.put(
          "ToolTip.background",
          new ColorUIResource(Color.WHITE)
        );
        javax.swing.UIManager.put(
          "Button.foreground",
          new ColorUIResource(Color.BLACK)
        );
        javax.swing.UIManager.put(
          "Button.background",
          new ColorUIResource(Color.WHITE)
        );
        javax.swing.UIManager.put(
          "Label.foreground",
          new ColorUIResource(Color.BLACK)
        );
        javax.swing.UIManager.put(
          "Label.background",
          new ColorUIResource(Color.WHITE)
        );
      }
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(
        Font.createFont(
          Font.TRUETYPE_FONT,
          getClass().getResourceAsStream("/Ubuntu-Bold.ttf")
        )
      );
      ge.registerFont(
        Font.createFont(
          Font.TRUETYPE_FONT,
          getClass().getResourceAsStream("/UbuntuMono-Bold.ttf")
        )
      );

      javax.swing.UIManager.put("ToolTip.font", omega.utils.UIManager.PX14);
      javax.swing.UIManager.put("Button.font", omega.utils.UIManager.PX14);
      javax.swing.UIManager.put("Label.font", omega.utils.UIManager.PX14);
      javax.swing.UIManager.put("ScrollBar.thumb", new ColorUIResource(x));
      javax.swing.UIManager.put("ScrollBar.track", new ColorUIResource(y));
      javax.swing.UIManager.put(
        "ScrollPane.background",
        new ColorUIResource(y)
      );
    } catch (Exception e1) {
      e1.printStackTrace();
    }

    picker = new ThemePicker(this);
    uiManager = new UIManager(this);
    Startup.checkStartup(this);
    UIManager.loadHighlight();
    UIManager.setData(this);

    animationsDialog = new AnimationsDialog(this);

    splash = new SplashScreen();
    splash.setProgress(10, "welcome");
    Generator.init(this);
    splash.setProgress(37, "welcome");

    setLayout(new BorderLayout());
    setSize(1000, 650);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    addWindowListener(
      new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          dispose();
        }
      }
    );

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

    tabPanel = new TabPanel(this);
    rightTabPanel = new TabPanel(this);
    bottomTabPanel = new TabPanel(this);
    rightTabPanel.setAddAction(() -> {
      if (rightTabPanel.tabPane.getTabCount() > 0) {
        rightTabPanel.setVisible(true);
        rightTabPanelSplitPane.setDividerLocation(
          rightTabPanelSplitPane.getWidth() / 2
        );
      }
    });

    rightTabPanel.setRemoveAction(() -> {
      if (rightTabPanel.tabPane.getTabCount() == 0) rightTabPanel.setVisible(
        false
      );
    });

    bottomTabPanel.setAddAction(() -> {
      if (bottomTabPanel.tabPane.getTabCount() > 0) {
        bottomTabPanel.setVisible(true);
        bottomTabPanelSplitPane.setDividerLocation(
          bottomTabPanelSplitPane.getWidth() / 2
        );
      }
    });

    bottomTabPanel.setRemoveAction(() -> {
      if (bottomTabPanel.tabPane.getTabCount() == 0) bottomTabPanel.setVisible(
        false
      );
    });

    rightTabPanel.setVisible(false);
    bottomTabPanel.setVisible(false);
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
    fileView = new FileView("FileManager", this);
    universalSettings = new UniversalSettingsWizard(this);
    buildView = new BuildView("BuildManager", this);
    runView = new RunView("RunManager", this, true);

    splitPane.setDividerLocation(300);
    splitPane.setLeftComponent(fileView.getFileTreePanel());

    splash.setProgress(83, "plugging in");
    pluginManager = new PluginManager();
    pluginStore = new PluginStore(this, pluginManager);
    pluginsView = new PluginsView(this, pluginManager);
    pluginReactionManager = new PluginReactionManager(pluginManager);
    pluginManager.doPluginReactionRegistration();

    getPluginReactionManager()
      .triggerReaction(
        PluginReactionEvent.genNewInstance(
          PluginReactionEvent.EVENT_TYPE_IDE_INITIALIZED,
          this,
          splash
        )
      );

    splash.setProgress(100, "");

    File file = new File(DataManager.getDefaultProjectPath());

    if (
      DataManager.getWorkspace().equals("") ||
      !new File(DataManager.getWorkspace()).exists()
    ) new WorkspaceSelector(this).setVisible(true);

    if (file.exists() && file.isDirectory()) {
      loadProject(file);
      new Thread(() -> {
        while (!isVisible());
        Animations.animateAll(500);
      })
        .start();
    } else {
      launcher = new omega.launcher.Launcher();
      launcher.setVisible(true);
    }
  }

  public void triggerBuild(KeyEvent e) {
    if (toolMenu.buildComp.isClickable()) {
      if (
        GradleProcessManager.isGradleProject()
      ) GradleProcessManager.build(); else Screen
        .getBuildView()
        .compileProject();

      e.consume();
    }
  }

  public void triggerRun(KeyEvent e) {
    if (toolMenu.buildComp.isClickable()) {
      if (
        GradleProcessManager.isGradleProject()
      ) GradleProcessManager.run(); else Screen.getRunView().run();

      e.consume();
    }
  }

  public void triggerInstantRun(KeyEvent e) {
    if (toolMenu.buildComp.isClickable()) {
      Screen.getRunView().instantRun();

      e.consume();
    }
  }

  public void showSearchDialog(KeyEvent e) {
    fileView.getSearchWindow().setVisible(true);

    e.consume();
  }

  @Override
  public void setSize(int w, int h) {
    super.setSize(w, h);
    int arc = 20;
    if (getExtendedState() != NORMAL) arc = 0;
    setShape(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
  }

  public void toggleFileTree() {
    fileView
      .getFileTreePanel()
      .setVisible(!fileView.getFileTreePanel().isVisible());
  }

  public static void notify(String text) {
    getScreen().setTitle(text);
  }

  public static void hideNotif() {
    getScreen().loadTitle(getFileView().getProjectName());
  }

  public static void notify(String text, long time, Runnable task) {
    getScreen().setTitle(text);
    new Thread(() -> {
      try {
        Thread.sleep(time);
      } catch (Exception e) {}
      getScreen().loadTitle(getFileView().getProjectName());
      if (task != null) task.run();
    })
      .start();
  }

  public void manageTools(ProjectDataBase manager) {
    toolMenu.langComp.image =
      LanguageTagView.getRespectiveTagImage(manager.getLanguageTag());
    toolMenu.langComp.repaint();
    toolMenu.structureViewComp.setVisible(!manager.non_java);
    toolMenu.sep4.setVisible(!manager.non_java);
    toolMenu.asteriskComp.setVisible(!manager.non_java);
    toolMenu.contentModeComp.setVisible(!manager.non_java);
    toolMenu.instantRunComp.setVisible(!manager.non_java);
    toolMenu.instantBuildComp.setVisible(!manager.non_java);
    toolMenu.projectPopup.setEnabled("Manage Build-Path", !manager.non_java);
    toolMenu.projectPopup.setEnabled("Add Additional Flags", !manager.non_java);
    toolMenu.toolsPopup.setEnabled("Generate Getter/Setter", !manager.non_java);
    toolMenu.toolsPopup.setEnabled(
      "Override/Implement Methods",
      !manager.non_java
    );
    toolMenu.typeItem.setName(
      fileView.getProjectManager().non_java
        ? "Project Type : Non-Java"
        : "Project Type : Java"
    );
    sideMenu.structureComp.setVisible(!manager.non_java);
    toolMenu.changeLocations(manager.non_java);
    sideMenu.changeLocations(manager.non_java);
  }

  public static void setStatus(String status, int value, BufferedImage image) {
    if (value != 100) Screen
      .getScreen()
      .getBottomPane()
      .setMessage(status, image); else Screen
      .getScreen()
      .getBottomPane()
      .setMessage("Status of any process running will appear here!", image);
  }

  @Override
  public void paint(Graphics g) {
    setSize(getWidth(), getHeight());
    super.paint(g);
    g.dispose();
  }

  public static void reverseColors(Component c) {
    Color l = c.getBackground();
    c.setBackground(c.getForeground());
    c.setForeground(l);
  }

  public void loadTitle(String projectName) {
    setTitle(projectName + " -Omega IDE " + VERSION);
    toolMenu.titleComp.setText(getTitle());
  }

  public void setProject(String projectName) {
    loadTitle(projectName);
  }

  public static void openInDesktop(File file) {
    try {
      new Thread(() -> {
        try {
          java.awt.Desktop.getDesktop().open(file);
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      })
        .start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Editor loadFile(File file) {
    String fn = file.getName();
    if (fn.endsWith(".pdf") || fn.endsWith(".deb")) {
      openInDesktop(file);
      return null;
    }
    if (tabPanel.viewImage(file)) return null;
    if (tabPanel.viewArchive(file)) return null;
    if (isFileOpened(file)) {
      Editor e = getEditor(file);
      tabPanel.setActiveEditor(e);
      e.grabFocus();
      return e;
    }
    new Thread(() -> Screen.addAndSaveRecents(file.getAbsolutePath())).start();
    Editor editor = new Editor(this);
    editor.loadFile(file);
    tabPanel.addTab(
      file.getName(),
      file.getAbsolutePath(),
      editor,
      getPackName(file)
    );
    return editor;
  }

  public Editor loadFileOnRightTabPanel(File file) {
    String fn = file.getName();
    if (fn.endsWith(".pdf") || fn.endsWith(".deb")) {
      openInDesktop(file);
      return null;
    }
    if (rightTabPanel.viewImage(file)) return null;
    if (rightTabPanel.viewArchive(file)) return null;
    if (isFileOpened(file)) return null;
    new Thread(() -> Screen.addAndSaveRecents(file.getAbsolutePath())).start();
    Editor editor = new Editor(this);
    editor.loadFile(file);
    rightTabPanel.addTab(
      file.getName(),
      file.getAbsolutePath(),
      editor,
      getPackName(file)
    );
    return editor;
  }

  public Editor loadFileOnBottomTabPanel(File file) {
    String fn = file.getName();
    if (fn.endsWith(".pdf") || fn.endsWith(".deb")) {
      openInDesktop(file);
      return null;
    }
    if (bottomTabPanel.viewImage(file)) return null;
    if (bottomTabPanel.viewArchive(file)) return null;
    if (isFileOpened(file)) {
      Editor e = getEditor(file);
      tabPanel.setActiveEditor(e);
      e.grabFocus();
      return e;
    }
    new Thread(() -> Screen.addAndSaveRecents(file.getAbsolutePath())).start();
    Editor editor = new Editor(this);
    editor.loadFile(file);
    bottomTabPanel.addTab(
      file.getName(),
      file.getAbsolutePath(),
      editor,
      getPackName(file)
    );
    return editor;
  }

  public static void addAndSaveRecents(String path) {
    RecentsManager.add(path);
    recentsManager.saveData();
  }

  public static void launchNewWindow(String projectPath) {
    IDE.main(null);
  }

  public void loadProject(File file) {
    fileView.saveAll();
    fileView.setProjectPath(file.getAbsolutePath());
    setVisible(true);
  }

  public boolean isFileOpened(File file) {
    LinkedList<Editor> allEditors = new LinkedList<>();
    tabPanel.getEditors().forEach(allEditors::add);
    rightTabPanel.getEditors().forEach(allEditors::add);
    bottomTabPanel.getEditors().forEach(allEditors::add);
    for (Editor e : allEditors) {
      if (e.currentFile != null) {
        if (e.currentFile.getAbsolutePath().equals(file.getAbsolutePath())) {
          allEditors.clear();
          return true;
        }
      }
    }
    return false;
  }

  public Editor getEditor(File file) {
    LinkedList<Editor> allEditors = new LinkedList<>();
    tabPanel.getEditors().forEach(allEditors::add);
    rightTabPanel.getEditors().forEach(allEditors::add);
    bottomTabPanel.getEditors().forEach(allEditors::add);
    for (Editor e : allEditors) {
      if (e.currentFile != null) {
        if (e.currentFile.getAbsolutePath().equals(file.getAbsolutePath())) {
          allEditors.clear();
          return e;
        }
      }
    }
    return null;
  }

  public String getPackName(File file) {
    String res = "";
    boolean canRecord = false;
    StringTokenizer tokenizer = new StringTokenizer(
      file.getAbsolutePath(),
      File.separator
    );
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (canRecord) res += token + "."; else if (
        token.equals(getFileView().getProjectName())
      ) canRecord = true;
    }
    if (!canRecord) return file.getAbsolutePath(); else res =
      res.substring(0, res.length() - 1);
    return res;
  }

  @Override
  public void layout() {
    super.layout();
    try {
      Screen
        .getPluginReactionManager()
        .triggerReaction(
          PluginReactionEvent.genNewInstance(
            PluginReactionEvent.EVENT_TYPE_IDE_DO_LAYOUT,
            this,
            getContentPane()
          )
        );
    } catch (Exception e) {}
  }

  @Override
  public void dispose() {
    active = false;
    Screen.notify("Terminating Running Applications");
    try {
      for (Process p : runView.runningApps) {
        if (p.isAlive()) p.destroyForcibly();
      }
    } catch (Exception e) {}
    Screen.notify("Saving UI and Data");
    pluginManager.save();
    uiManager.save();
    dataManager.saveData();
    SnippetBase.save();
    Screen.notify("Saving Project");
    saveAllEditors();
    try {
      getFileView().getProjectManager().save();
    } catch (Exception e2) {}
    super.dispose();
    getPluginReactionManager()
      .triggerReaction(
        PluginReactionEvent.genNewInstance(
          PluginReactionEvent.EVENT_TYPE_IDE_CLOSING,
          this,
          0
        )
      );
    System.exit(0);
  }

  public static final Screen getScreen() {
    return Screen.getFileView().getScreen();
  }

  public static RecentsManager getRecentsManager() {
    return recentsManager;
  }

  public static FileView getFileView() {
    return fileView;
  }

  public static BuildView getBuildView() {
    return buildView;
  }

  public static RunView getRunView() {
    return runView;
  }

  public static ErrorHighlighter getErrorHighlighter() {
    return errorHighlighter;
  }

  public static BasicHighlight getBasicHighlight() {
    return basicHighlight;
  }

  public static UniversalSettingsWizard getUniversalSettingsView() {
    return universalSettings;
  }

  public static TerminalComp getTerminalComp() {
    return terminal;
  }

  public static void pickTheme(String defaultTheme) {
    picker.lightMode = defaultTheme.equals("light");
    picker.loadImage(defaultTheme + ".png");
    picker.manageTheme();
    picker.setVisible(true);
  }

  public static void showAnimationsDialog() {
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

  public void pressKey(int code) {
    try {
      if (robot == null) robot = new Robot();
      robot.keyPress(code);
    } catch (Exception e) {}
  }

  public void moveTo(int x, int y) {
    try {
      if (robot == null) robot = new Robot();
      robot.mouseMove(x, y);
    } catch (Exception e) {}
  }

  public static UIManager getUIManager() {
    return uiManager;
  }

  public static DataManager getDataManager() {
    return dataManager;
  }

  public void saveAllEditors() {
    tabPanel.getEditors().forEach(w -> w.saveCurrentFile());
    rightTabPanel.getEditors().forEach(w -> w.saveCurrentFile());
    bottomTabPanel.getEditors().forEach(w -> w.saveCurrentFile());
    if (
      DataManager.isSourceDefenderEnabled()
    ) ToolMenu.sourceDefender.backupData();
  }

  public void loadThemes() {
    tabPanel.getEditors().forEach(w -> w.loadTheme());
    rightTabPanel.getEditors().forEach(w -> w.loadTheme());
    bottomTabPanel.getEditors().forEach(w -> w.loadTheme());
  }

  public LinkedList<Editor> getAllEditors() {
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
    closeAllTabs();
  }

  public void closeAllTabs() {
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

  public static PluginStore getPluginStore() {
    return pluginStore;
  }

  public static PluginsView getPluginsView() {
    return pluginsView;
  }

  public static PluginReactionManager getPluginReactionManager() {
    return pluginReactionManager;
  }

  public synchronized boolean isFocusMode() {
    return focusMode;
  }

  public synchronized void setFocusMode(boolean focusMode) {
    this.focusMode = focusMode;
    final int state = getExtendedState();
    Thread tx = new Thread(() -> {
      setStatus("Toggling Focus Mode ...", 0, IconManager.fluentfocusImage);
      if (focusMode) {
        setVisible(false);
        remove(toolMenu);
        layout();
        doLayout();
        setVisible(true);
      } else {
        setVisible(false);
        add(toolMenu, BorderLayout.NORTH);
        layout();
        doLayout();
        setVisible(true);
      }
      doLayout();

      setStatus(null, 100, null);
    });

    tx.start();

    try {
      tx.join();
    } catch (Exception e) {
      e.printStackTrace();
    }
    setExtendedState(state);
  }

  public void saveEssential() {
    uiManager.save();
    dataManager.saveData();
    notify("Saving Project");
    saveAllEditors();
    hideNotif();
  }

  public static boolean onWindows() {
    return File.pathSeparator.equals(";");
  }

  public static synchronized boolean isNotNull(String text) {
    return text != null && !text.equals("");
  }

  public static void main(String[] args) {
    new Screen();
  }
}
