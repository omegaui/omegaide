/*
 * ToolMenu
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

package omega.ui.component;
import omega.ui.github.GitHubClientWindow;

import omega.instant.support.java.misc.JDKSelectionDialog;

import omega.instant.support.java.generator.Generator;

import omega.instant.support.java.parser.JavaSyntaxParser;

import omegaui.component.animation.ImageSizeTransitionAnimationLayer;

import omega.io.DataManager;
import omega.io.UIManager;
import omega.io.IconManager;

import omega.instant.support.java.framework.CodeFramework;

import omega.instant.support.LanguageTagView;

import omega.instant.support.build.gradle.GradleModuleWizard;
import omega.instant.support.build.gradle.GradleBuildScriptManager;
import omega.instant.support.build.gradle.GradleProcessManager;

import omega.instant.support.universal.ProcessWizard;
import omega.instant.support.universal.UniversalProjectWizard;

import omega.instant.support.java.JavaProjectWizard;

import omega.ui.dialog.InfoScreen;
import omega.ui.dialog.SourceDefender;
import omega.ui.dialog.StructureView;
import omega.ui.dialog.MadeWithScreen;
import omega.ui.dialog.ProjectDistructionWizard;
import omega.ui.dialog.ConsoleSelector;
import omega.ui.dialog.InstructionWindow;
import omega.ui.dialog.ColorPicker;
import omega.ui.dialog.FontChooser;
import omega.ui.dialog.WorkspaceSelector;
import omega.ui.dialog.FileSelectionDialog;
import omega.ui.dialog.RecentsDialog;

import omegaui.component.TextComp;

import omega.ui.popup.OPopupWindow;
import omega.ui.popup.OPopupItem;
import omega.ui.popup.NotificationPopup;

import omega.plugin.event.PluginReactionEvent;

import omega.IDE;
import omega.Screen;
import omega.IDEUpdater;

import java.net.URL;

import java.io.File;

import java.util.LinkedList;

import javax.imageio.ImageIO;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.image.BufferedImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.Font;
import java.awt.Desktop;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JComponent;

import static omega.io.UIManager.*;
import static omega.io.IconManager.*;
import static omegaui.component.animation.Animations.*;

/*
 * This is not only the tool menu but also the IDE 's custom window decorator
 */

public class ToolMenu extends JPanel {
	private Screen screen;
	
	public OPopupWindow filePopup;
	public OPopupWindow projectPopup;
	public OPopupWindow codePopup;
	public OPopupWindow toolsPopup;
	public OPopupWindow setPopup;
	public OPopupWindow helpPopup;
	
	public static OPopupItem recentsMenu;
	public static OPopupWindow allProjectsPopup;
	public static OPopupItem contentAssistOnItem;
	public static OPopupItem contentAssistModeItem;
	public static OPopupItem autoImportModeItem;
	public static OPopupItem allMenu;
	public static OPopupItem allSettingsItem;
	public static OPopupItem jdkItem;
	public static OPopupItem jdkRootItem;
	public static OPopupItem instantModeItem;
	public static OPopupItem parsingEnabledItem;
	
	public TextComp openProjectComp;
	public TextComp openFileComp;
	public TextComp newProjectComp;
	public TextComp newFileComp;
	
	public TextComp sep0;
	
	public TextComp shellComp;
	public TextComp themeComp;
	public TextComp searchComp;
	
	public TextComp sep1;
	
	public TextComp runComp;
	public TextComp buildComp;
	
	public TextComp sep2;
	
	public TextComp instantRunComp;
	public TextComp instantBuildComp;
	
	public TextComp sep3;
	
	public TextComp structureViewComp;

	public TextComp memoryComp;
	
	public TextComp taskComp;

	//IDE Dialogs
	public static InfoScreen infoScreen;
	public static ToolMenuPathBox pathBox;
	public static SourceDefender sourceDefender;
	public static JavaProjectWizard javaProjectWizard;
	public static ProcessWizard processWizard;
	public static StructureView structureView;
	public static MadeWithScreen madeWithScreen;
	public static GradleModuleWizard gradleModuleWizard;
	public static UniversalProjectWizard universalProjectWizard;
	public static ProjectDistructionWizard projectDistructionWizard;
	public static ConsoleSelector consoleSelector;
	public static GradleBuildScriptManager gradleBuildScriptManager;
	public static InstructionWindow instructionWindow;
	public static ColorPicker colorPicker;
	public static LanguageTagView languageTagView;
	public static RecentsDialog recentsDialog;
	public static GitHubClientWindow githubClientWindow;

	public static NotificationPopup projectTypeNotificationPopup = null;
	
	private int pressX;
	private int pressY;
	
	//The window decoration objects
	public static Color closeWinColor = TOOLMENU_COLOR2;
	public static Color maximizeWinColor = TOOLMENU_COLOR4;
	public static Color minimizeWinColor = TOOLMENU_COLOR3;
	
	public TextComp iconComp;
	public TextComp langComp;
	public TextComp minimizeComp;
	public TextComp maximizeComp;
	public TextComp closeComp;
	
	private BufferedImage image;
	
	private Point lastLocation;
	
	private Dimension lastSize;
	
	public ToolMenu(Screen screen) {
		this.screen = screen;
		if(javaProjectWizard == null){
			infoScreen = new InfoScreen(screen);
			sourceDefender = new SourceDefender(screen);
			javaProjectWizard = new JavaProjectWizard(screen);
			processWizard = new ProcessWizard(screen);
			structureView = new StructureView(screen);
			madeWithScreen = new MadeWithScreen(screen);
			gradleModuleWizard = new GradleModuleWizard(screen);
			universalProjectWizard = new UniversalProjectWizard(screen);
			consoleSelector = new ConsoleSelector(screen);
			gradleBuildScriptManager = new GradleBuildScriptManager(screen);
			projectDistructionWizard = new ProjectDistructionWizard(screen);
			instructionWindow = new InstructionWindow(screen);
			colorPicker = new ColorPicker(screen);
			languageTagView = new LanguageTagView(screen);
			recentsDialog = new RecentsDialog(screen);
			githubClientWindow = new GitHubClientWindow(screen);
			
			projectTypeNotificationPopup = NotificationPopup.create(screen)
			.title("Project Type Management")
			.dialogIcon(IconManager.fluentfolderImage)
			.message("IDE's Restart is Required!", TOOLMENU_COLOR4)
			.shortMessage("Click this to Exit", TOOLMENU_COLOR2)
			.iconButton(IconManager.fluentcloseImage, ()->{
				try{
					for(Process p : Screen.getProjectRunner().runningApps) {
						if(p.isAlive())
							p.destroyForcibly();
					}
				}
				catch(Exception e2) {
					
				}
				Screen.getPluginManager().save();
				Screen.getUIManager().save();
				Screen.getDataManager().saveData();
				Screen.getScreen().saveAllEditors();
				try{
					Screen.getProjectFile().getProjectManager().save();
				}
				catch(Exception e2) {
					
				}
				System.exit(0);
			}, "You need to manually start the IDE again!")
			.build();
		}
		setLayout(null);
		setSize(screen.getWidth(), 90);
		setPreferredSize(getSize());
		UIManager.setData(this);
		setBackground(back2);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(e.getButton() == 1 && e.getClickCount() == 2){
					maximize();
					return;
				}
				pressX = e.getX();
				pressY = e.getY();
			}
		});
		addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(screen.getExtendedState() == Screen.NORMAL)
					screen.setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY);
			}
		});
		init();
	}
	
	private void init() {
		try{
			image = ImageIO.read(getClass().getResourceAsStream("/omega_ide_icon128" + (UIManager.isDarkMode() ? "_dark.png" : ".png")));
		}
		catch(Exception e){
			System.err.println(e);
		}
		iconComp = new TextComp(image, 30, 30, back2, back2, back2, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setClickable(false);
		iconComp.setArc(0, 0);
		add(iconComp);
		
		langComp = new TextComp(IconManager.fluentjavaImage, 25, 25, back3, back2, back2, this::changeLang);
		langComp.setBounds(30, 0, 30, 30);
		langComp.setArc(0, 0);
		add(langComp);
		
		closeComp = new TextComp("", c1, back2, c3, this::disposeAll){
			@Override
			public void draw(Graphics2D g){
				g.setColor(back2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(closeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(back2);
					g.drawString("x", getWidth()/2 - g.getFontMetrics().stringWidth("x")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		add(closeComp);
		
		maximizeComp = new TextComp("", c1, back2, c3, this::maximize){
			@Override
			public void draw(Graphics2D g){
				g.setColor(back2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(maximizeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(back2);
					g.drawString((screen.getExtendedState() == Screen.MAXIMIZED_BOTH) ? "><" : "<>", getWidth()/2 - g.getFontMetrics().stringWidth("<>")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		add(maximizeComp);
		
		minimizeComp = new TextComp("", c1, back2, c3, this::minimize){
			@Override
			public void draw(Graphics2D g){
				g.setColor(back2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(minimizeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(back2);
					g.drawString("-", getWidth()/2 - g.getFontMetrics().stringWidth("-")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		minimizeComp.setArc(0, 0);
		add(minimizeComp);

		memoryComp = new TextComp("", "Memory Usage (Excluding JVM). Click to Run GC.", TOOLMENU_COLOR6_SHADE, TOOLMENU_GRADIENT, glow, ()->{
			System.gc();
			computeMemoryUsage();
		});
		memoryComp.setOnMouseEntered(this::computeMemoryUsage);
		memoryComp.setArc(0, 0);
		memoryComp.setFont(PX14);
		add(memoryComp);
		
		taskComp = new TextComp("", back2, back2, TOOLMENU_COLOR1, null);
		taskComp.setOnMouseEntered(this::computeMemoryUsage);
		taskComp.setArc(0, 0);
		taskComp.setFont(PX14);
		taskComp.setTextAlignment(TextComp.TEXT_ALIGNMENT_RIGHT);
		add(taskComp);

		computeMemoryUsage();
		
		filePopup = OPopupWindow.gen("File Menu", screen, 0, false).width(510);
		initFilePopup();
		Menu fileMenu = new Menu(filePopup, "File");
		fileMenu.setBounds(60, 5, 40, 20);
		addComp(fileMenu);
		
		projectPopup = OPopupWindow.gen("Project Menu", screen, 0, false).width(250);
		initProjectPopup();
		Menu recentsMenu = new Menu(projectPopup, "Project");
		recentsMenu.setBounds(100, 5, 60, 20);
		addComp(recentsMenu);
		
		codePopup = OPopupWindow.gen("Code Menu", screen, 0, false).width(300);
		initCodePopup();
		Menu codeMenu = new Menu(codePopup, "Code");
		codeMenu.setBounds(165, 5, 35, 20);
		addComp(codeMenu);
		
		toolsPopup = OPopupWindow.gen("Tools Menu", screen, 0, false).width(300);
		initToolsPopup();
		Menu toolsMenu = new Menu(toolsPopup, "Tools");
		toolsMenu.setBounds(200, 5, 50, 20);
		addComp(toolsMenu);
		
		setPopup = OPopupWindow.gen("Settings Menu", screen, 0, false).width(250);
		initSetMenu();
		Menu setMenu = new Menu(setPopup, "Settings");
		setMenu.setBounds(252, 5, 60, 20);
		addComp(setMenu);
		
		helpPopup = OPopupWindow.gen("Help Menu", screen, 0, false).width(300);
		initHelpMenu();
		Menu helpMenu = new Menu(helpPopup, "Help");
		helpMenu.setBounds(314, 5, 40, 20);
		addComp(helpMenu);
		
		openProjectComp = new TextComp(fluentfolderImage, 20, 20, "Open Project", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->Screen.getProjectFile().open("Project"));
		openProjectComp.setBounds(2, 33, 24, 24);
		openProjectComp.setFont(PX14);
		openProjectComp.setArcVisible(true, false, true, false);
		addComp(openProjectComp);
		
		openFileComp = new TextComp(fluentfileImage, 20, 20, "Open File", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->Screen.getProjectFile().open("File"));
		openFileComp.setBounds(28, 33, 24, 24);
		openFileComp.setFont(PX14);
		openFileComp.setArcVisible(true, false, true, false);
		addComp(openFileComp);
		
		newProjectComp = new TextComp(fluentnewfolderImage, 20, 20, "Create New Java Project", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->javaProjectWizard.setVisible(true));
		newProjectComp.setBounds(54, 33, 24, 24);
		newProjectComp.setFont(PX14);
		newProjectComp.setArcVisible(true, false, true, false);
		addComp(newProjectComp);
		
		newFileComp = new TextComp(fluentnewfileImage, 20, 20, "Create New File", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->Screen.getProjectFile().getFileCreator().show("Custom File"));
		newFileComp.setBounds(80, 33, 24, 24);
		newFileComp.setFont(PX14);
		newFileComp.setArcVisible(true, false, true, false);
		addComp(newFileComp);
		
		sep0 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep0.setBounds(108, 31, 2, 28);
		addComp(sep0);
		
		OPopupWindow consoleItemWindow = new OPopupWindow("Select Console Type", screen, 0, false).width(270);
		consoleItemWindow
		.createItem("New System Terminal", IconManager.fluentconsoleImage, consoleSelector::launchTerminal)
		.createItem("Faster Terminal", IconManager.fluentconsoleImage, Screen.getTerminalComp()::showTerminal)
		.createItem("Full-Fledge Terminal", IconManager.fluentconsoleImage, Screen.getTerminalComp()::showJetTerminal);
		
		shellComp = new TextComp(fluentconsoleImage, 20, 20, "Launch a terminal", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, null);
		shellComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				consoleItemWindow.setLocation(e.getXOnScreen(), e.getYOnScreen() + 10 + OPopupWindow.HEIGHT);
				consoleItemWindow.setVisible(true);
			}
		});
		shellComp.put("Popup", consoleItemWindow);
		shellComp.setFont(PX16);
		shellComp.setBounds(114, 33, 24, 24);
		shellComp.setArcVisible(false, true, false, true);
		add(shellComp);
		
		themeComp = new TextComp(IconManager.fluentchangethemeImage, 20, 20, "Change Theme", TOOLMENU_COLOR6_SHADE, back3, TOOLMENU_COLOR6,
		()->{
			Screen.pickTheme(DataManager.getTheme());
			if(!themeComp.getName().equals(DataManager.getTheme())){
				NotificationPopup.create(screen)
				.size(300, 120)
				.title("Theme Manager")
				.dialogIcon(IconManager.fluentupdateImage)
				.message("IDE's Restart is Required!", TOOLMENU_COLOR4)
				.shortMessage("Click this to Restart", TOOLMENU_COLOR2)
				.iconButton(IconManager.fluentcloseImage, IDE::restart, "")
				.build()
				.locateOnBottomLeft()
				.showIt();
			}
			themeComp.setName(DataManager.getTheme());
		});
		themeComp.setName(DataManager.getTheme());
		themeComp.setBounds(140, 33, 24, 24);
		add(themeComp);

		searchComp = new TextComp(fluentsearchImage, 20, 20, "Search and Open File", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->Screen.getProjectFile().getSearchWindow().setVisible(true));
		searchComp.setBounds(166, 33, 24, 24);
		searchComp.setArcVisible(true, false, true, false);
		addComp(searchComp);

		sep1 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep1.setBounds(194, 31, 2, 28);
		addComp(sep1);
		
		runComp = new TextComp(fluentrunImage, 20, 20, "Run Project, Right Click to launch without build! (Not for Gradle)", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->{
			if(runComp.isClickable() && buildComp.isClickable()){
				if(GradleProcessManager.isGradleProject())
					GradleProcessManager.run();
				else
					Screen.getProjectRunner().run();
			}
		});
		runComp.setBounds(200, 33, 24, 24);
		runComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(e.getButton() == 3){
					if(GradleProcessManager.isGradleProject())
						return;
					if(runComp.isClickable() && buildComp.isClickable())
						Screen.getProjectRunner().justRun();
				}
			}
		});
		runComp.setArcVisible(false, true, false, true);
		add(runComp);
		
		buildComp = new TextComp(fluentbuildImage, 20, 20, "Build Project", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->{
			if(runComp.isClickable() && buildComp.isClickable()){
				if(GradleProcessManager.isGradleProject())
					GradleProcessManager.build();
				else
					Screen.getProjectBuilder().compileProject();
			}
		});
		buildComp.setBounds(226, 33, 24, 24);
		buildComp.setArcVisible(true, false, true, false);
		add(buildComp);
		
		sep2 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep2.setBounds(254, 31, 2, 28);
		addComp(sep2);
		
		instantRunComp = new TextComp(fluentrocketImage, 20, 20, "Instant Run(Java Only), Uses System Default JDK for Building Project", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->{
			Screen.getProjectRunner().instantRun();
		});
		instantRunComp.setBounds(260, 33, 24, 24);
		instantRunComp.setArcVisible(false, true, false, true);
		add(instantRunComp);
		
		instantBuildComp = new TextComp(fluentrocketbuildImage, 20, 20, "Instant Build(Java Only), Uses System Default JDK for Building Project", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->{
			Screen.getProjectRunner().instantBuild();
		});
		instantBuildComp.setBounds(286, 33, 24, 24);
		instantBuildComp.setArcVisible(true, false, true, false);
		add(instantBuildComp);

		sep3 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep3.setBounds(314, 31, 2, 28);
		addComp(sep3);
		
		structureViewComp = new TextComp(fluentstructureImage, 20, 20, "Class Disassembler(integrated)", TOOLMENU_COLOR1_SHADE, back3, TOOLMENU_COLOR1, ()->structureView.setVisible(true));
		structureViewComp.setBounds(320, 33, 24, 24);
		addComp(structureViewComp);
		
		pathBox = new ToolMenuPathBox();
		add(pathBox);
		
		reshapeComp();
		
		putAnimationLayer(openProjectComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(openFileComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(newProjectComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(newFileComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		
		putAnimationLayer(shellComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(themeComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(searchComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		
		putAnimationLayer(runComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(buildComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		
		putAnimationLayer(instantRunComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(instantBuildComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		
		putAnimationLayer(structureViewComp, getImageSizeAnimationLayer(25, 3, true), ACTION_MOUSE_ENTERED);
		
		putAnimationLayer(langComp, getImageSizeAnimationLayer(25, -5, true), ACTION_MOUSE_ENTERED);
		
		ImageSizeTransitionAnimationLayer layer = (ImageSizeTransitionAnimationLayer)getImageSizeAnimationLayer(25, -5, true);
		languageTagView.prepareLayer(layer, iconComp, -5, true);
		
		putAnimationLayer(iconComp, layer, ACTION_MOUSE_ENTERED);
		
		putComp(iconComp, layer);
	}
	
	public void minimize(){
		screen.setState(Screen.ICONIFIED);
		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_MINIMIZED, this, true));
	}
	
	public void maximize(){
		screen.setExtendedState((screen.getExtendedState() == Screen.NORMAL) ? Screen.MAXIMIZED_BOTH : Screen.NORMAL);
		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance((screen.getExtendedState() == Screen.MAXIMIZED_BOTH) ? PluginReactionEvent.EVENT_TYPE_IDE_MAXIMIZED : PluginReactionEvent.EVENT_TYPE_IDE_RESTORED, this, ((screen.getExtendedState() == Screen.MAXIMIZED_BOTH))));
	}
	
	public void disposeAll(){
		screen.dispose();
	}
	 
	public void computeMemoryUsage(){
		long ram = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		ram = (long)(ram / 1000000);
		memoryComp.setText(ram + " MB");
	}
	
	public void setTask(String task) {
		setMsg(task);
	}
	
	public static ToolMenuPathBox getPathBox() {
		return pathBox;
	}
	
	public void setMsg(String msg) {
		if(Screen.isNotNull(msg))
			taskComp.setText(msg);
		else
			taskComp.setText("");
	}
	
	public void reshapeComp() {
		//Resizing PathBox
		pathBox.setBounds(0, 60, getWidth(), 25);

		//Resizing Memory Comp
		memoryComp.setBounds(getWidth() - (30 * 3) - 65, 0, 60, 30);
		
		//Resizing Task Comp
		taskComp.setBounds(getWidth() - 200, 33, 200, 24);
		
		//Window Decorations
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		maximizeComp.setBounds(getWidth() - (30 * 2), 0, 30, 30);
		minimizeComp.setBounds(getWidth() - (30 * 3), 0, 30, 30);
	}
	
	public void changeLocations(boolean non_java){
		reloadItems(non_java);
		repaint();
	}
	
	public void reloadItems(boolean non_java){
		jdkItem.setEnabled(!non_java);
		jdkRootItem.setToolTipText(DataManager.getPathToJava());
		allSettingsItem.setEnabled(non_java);
		instantModeItem.setEnabled(!non_java);
		parsingEnabledItem.setEnabled(!non_java);
		contentAssistModeItem.setEnabled(!non_java);
		sep2.setVisible(!non_java);
		instantRunComp.setVisible(!non_java);
		instantBuildComp.setVisible(!non_java);
		sep3.setVisible(!non_java);
		structureViewComp.setVisible(!non_java);
		
		if(Screen.getProjectFile().getJDKManager() != null)
			jdkItem.setName("Project JDK : Java " + Screen.getProjectFile().getJDKManager().getVersionAsInt());
		else
			jdkItem.setName("Project JDK : None");
		
		if(DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_SPEED))
			instantModeItem.setName("Instant Mode : Speed");
		else if(DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_ACCURACY))
			instantModeItem.setName("Instant Mode : Accuracy");
		
		parsingEnabledItem.setName("Parsing Enabled : " + DataManager.isParsingEnabled());
	}
	
	public void changeLang(){
		languageTagView.setVisible(true);
		langComp.image = LanguageTagView.getRespectiveTagImage(Screen.getProjectFile().getProjectManager().getLanguageTag());
		langComp.repaint();
	}
	
	public void setProjectType(boolean non_java){
		Screen.getProjectFile().getProjectManager().setLanguageTag(LanguageTagView.LANGUAGE_TAG_ANY);
		Screen.getScreen().manageTools(Screen.getProjectFile().getProjectManager());
		Screen.getProjectFile().getProjectManager().save();
		projectTypeNotificationPopup.
		locateOnBottomLeft()
		.showIt();
	}

	public static void showNonJavaSettings(){
		if(GradleProcessManager.isGradleProject()) {
			Screen.getScreen().loadFile(new File(Screen.getProjectFile().getProjectPath(), "settings.gradle"));
			return;
		}
		if(Screen.getProjectFile().getProjectManager().isLanguageTagNonJava())
			Screen.getUniversalSettingsView().setVisible(true);
	}

	private void initSetMenu() {
		FontChooser fontC = new FontChooser(screen);
		setPopup.createItem("Change Editor Font", IconManager.settingsImage, ()->{
			Font font = fontC.chooseFont(new Font(UIManager.fontName, UIManager.fontState, UIManager.fontSize));
			UIManager.setEditorFontData(font.getSize(), font.getName(), font.getStyle());
			screen.getUIManager().save();
			screen.loadThemes();
		})
		.createItem("Change Terminal Font", IconManager.settingsImage, ()->{
			Font font = fontC.chooseFont(new Font(UIManager.terminalFontName, UIManager.terminalFontState, UIManager.terminalFontSize));
			UIManager.setTerminalFontData(font.getSize(), font.getName(), font.getStyle());
			screen.getUIManager().save();
		})
		.createItem("Change Content Assist Font", IconManager.settingsImage, ()->{
			Font font = fontC.chooseFont(DataManager.getHintFont());
			DataManager.setHintFont(font);
		})
		.createItem("Change Workspace", IconManager.settingsImage, ()->new WorkspaceSelector(screen).setVisible(true))
		.createItem("Animations", IconManager.settingsImage, ()->{
			Screen.showAnimationsDialog();
		})
		.createItem("Set System Terminal", IconManager.fluentconsoleImage, ()->{
			consoleSelector.setVisible(true);
		})
		.createItem("Set Gradle Script", IconManager.fluentgradleImage, ()->{
			gradleBuildScriptManager.setVisible(true);
		});
		FileSelectionDialog fs = new FileSelectionDialog(screen);
		
		setPopup.createItem("Set Background Illustration", IconManager.fluenticons8Logo, ()->{
			fs.setTitle("Select an image(Should be 456x456 px)");
			fs.setFileExtensions(".png", ".jpg");
			LinkedList<File> files = fs.selectFiles();
			if(!files.isEmpty()){
				DataManager.setBackgroundIllustrationPath(files.get(0).getAbsolutePath());
				Screen.getScreen().getTabPanel().loadIllustration();
			}
		});
		
		JDKSelectionDialog jdkSelectionDialog = new JDKSelectionDialog(screen);
		
		jdkItem = new OPopupItem(setPopup, "Project JDK : None", IconManager.fluentsourceImage, ()->{
			String sel = jdkSelectionDialog.makeChoice();
			if(Screen.isNotNull(sel)) {
				Screen.getProjectFile().getProjectManager().setJDKPath(sel);
			}
		});
		
		jdkRootItem = new OPopupItem(setPopup, "Set JDK Root", IconManager.fluentsourceImage, ()->{
			fs.setTitle("Select JDK Root");
			LinkedList<File> files = fs.selectDirectories();
			if(!files.isEmpty()){
				DataManager.setPathToJava(files.get(0).getAbsolutePath());
				jdkRootItem.setToolTipText(DataManager.getPathToJava());
			}
		});
		
		String text = "";
		if(DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_SPEED))
			text = "Instant Mode : Speed";
		else if(DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_ACCURACY))
			text = "Instant Mode : Accuracy";
		
		instantModeItem = new OPopupItem(setPopup, text, IconManager.fluentrocketImage, ()->{
			DataManager.setInstantMode((DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_SPEED)) ? DataManager.INSTANT_MODE_ACCURACY : DataManager.INSTANT_MODE_SPEED);
			
			if(DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_SPEED))
				instantModeItem.setName("Instant Mode : Speed");
			else if(DataManager.getInstantMode().equals(DataManager.INSTANT_MODE_ACCURACY))
				instantModeItem.setName("Instant Mode : Accuracy");
		});
		
		parsingEnabledItem = new OPopupItem(setPopup, "Parsing Enabled : " + DataManager.isParsingEnabled(), IconManager.fluentsourceImage, ()->{
			DataManager.setParsingEnabled(!DataManager.isParsingEnabled());
			parsingEnabledItem.setName("Parsing Enabled : " + DataManager.isParsingEnabled());
			if(!DataManager.isParsingEnabled()){
				JavaSyntaxParser.resetHighlights();
			}
		});
		
		allSettingsItem = new OPopupItem(setPopup, "Settings (Non-Java)", IconManager.settingsImage, ()->showNonJavaSettings());
		
		setPopup.addItem(jdkItem);
		setPopup.addItem(jdkRootItem);
		setPopup.addItem(parsingEnabledItem);
		setPopup.addItem(instantModeItem);
		setPopup.addItem(allSettingsItem);
	}
	
	private void initHelpMenu() {
		helpPopup.createItem("Stucked? See Tutorial Videos", IconManager.fluentyoutubeImage, ()->{
			try{
				java.awt.Desktop.getDesktop().browse(new java.net.URL("https://www.youtube.com/channel/UCpuQLV8MfuHaWHYSq-PRFXg").toURI());
			}
			catch(Exception e){
				System.err.println(e);
			}
		})
		.createItem("Plugin Store", IconManager.fluentpluginImage, ()->Screen.getPluginStore().setVisible(true))
		.createItem("Plugin Manager", IconManager.fluentmanageImage, ()->Screen.getPluginsView().setVisible(true))
		.createItem("Check for Update", IconManager.ideImage64, ()->{
			new Thread(IDEUpdater::checkForUpdate).start();
		})
		.createItem("Instructions", IconManager.fluentinfoImage, ()->{
			instructionWindow.setVisible(true);
		})
		.createItem("Made With", IconManager.fluentinfoImage, ()->{
			madeWithScreen.setVisible(true);
		})
		.createItem("About", IconManager.fluentinfoImage, ()->{
			infoScreen.setVisible(true);
		});
	}
	
	private void initToolsPopup() {
		toolsPopup
		.createItem("GitHub Client", IconManager.fluentgithubIcon, ()->githubClientWindow.setDirectory(new File(Screen.getProjectFile().getProjectPath())))
		.createItem("Source Defender", IconManager.fluentsourceImage, ()->sourceDefender.setVisible(true))
		.createItem("Process Wizard", IconManager.fluentbuildImage, ()->processWizard.setVisible(true))
		.createItem("Snippet Manager", IconManager.buildImage, ()->Screen.snippetView.setVisible(true))
		.createItem("Color Picker", IconManager.fluentcolorwheelImage, ()->colorPicker.pickColor());
	}

	private void initCodePopup(){
		contentAssistOnItem = new OPopupItem(codePopup, DataManager.isContentAssistRealTime() ? "Content Assist is ON" : "Content Assist is Stopped", IconManager.fluentsourceImage, ()->{
			DataManager.setContentAssistRealTime(!DataManager.isContentAssistRealTime());
			contentAssistOnItem.setName(DataManager.isContentAssistRealTime() ? "Content Assist is ON" : "Content Assist is Stopped");
		});
		
		contentAssistModeItem = new OPopupItem(codePopup, DataManager.isContentModeJava() ? "Content Assist Mode : Java" : "Content Assist Mode : Tokenizer", IconManager.fluentsourceImage, ()->{
			DataManager.setContentModeJava(!DataManager.isContentModeJava());
			contentAssistModeItem.setName(DataManager.isContentModeJava() ? "Content Assist Mode : Java" : "Content Assist Mode : Tokenizer");
		});
		
		autoImportModeItem = new OPopupItem(codePopup, DataManager.isUsingStarImports() ? "Using Asterisk Imports" : "Using Named Imports", IconManager.fluentsourceImage, ()->{
			DataManager.setUseStarImports(!DataManager.isUsingStarImports());
			autoImportModeItem.setName(DataManager.isUsingStarImports() ? "Using Asterisk Imports" : "Using Named Imports");
		});

		codePopup.addItem(contentAssistOnItem);
		codePopup.addItem(contentAssistModeItem);
		codePopup.addItem(autoImportModeItem);
		
		codePopup
		.createItem("Generate Getter/Setter", IconManager.buildImage, ()->Generator.gsView.genView(screen.getCurrentEditor()))
		.createItem("Override/Implement Methods", IconManager.buildImage, ()->Generator.overView.genView(screen.getCurrentEditor()));
	}
	
	private void initProjectPopup() {
		JFileChooser fileC = new JFileChooser();
		projectPopup.createItem("Manage Build-Path", IconManager.fluentbuildpathIcon, ()->Screen.getProjectFile().getDependencyView().setVisible(true))
		.createItem("Add Additional Flags", IconManager.fluentbuildpathIcon, ()->{
			Screen.getProjectFile().getExtendedDependencyView().setVisible(true);
		})
		.createItem("Refresh", IconManager.fluentrefreshIcon, ()->Screen.getProjectFile().getFileTreePanel().refresh())
		.createItem("Initialize Gradle", IconManager.fluentgradleImage, GradleProcessManager::init)
		.createItem("Create Gradle Module", IconManager.fluentgradleImage, ()->ToolMenu.gradleModuleWizard.setVisible(true))
		.createItem("Delete Project", IconManager.fluentdemonImage, ()->projectDistructionWizard.setVisible(true));
	}
	
	private void initFilePopup() {
		//New Menu Items
		filePopup.createItem("Open File", IconManager.fileImage, ()->Screen.getProjectFile().open("File"))
		.createItem("Open Project", IconManager.projectImage, ()->Screen.getProjectFile().open("Project"))
		.createItem("New Project (Java)", IconManager.projectImage, ()->javaProjectWizard.setVisible(true))
		.createItem("New Project (non-java project)", IconManager.projectImage, ()->universalProjectWizard.setVisible(true));
		
		recentsMenu = new OPopupItem(filePopup, "Recent Files / Projects", IconManager.fluentsearchImage, ()->{
			recentsDialog.setVisible(true);
		});
		filePopup.addItem(recentsMenu);
		
		allProjectsPopup = OPopupWindow.gen("All Projects Menu", screen, 0, true).width(350).height(250);
		allMenu = new OPopupItem(allProjectsPopup, "All Projects", IconManager.projectImage, ()->{
			allProjectsPopup.setLocationRelativeTo(null);
			allProjectsPopup.setVisible(true);
		});
		
		File home = new File(DataManager.getWorkspace());
		if(home.exists()){
			allProjectsPopup.trash();
			File[] files = home.listFiles();
			for(int i = 0; i < files.length; i++){
				for(int j = 0; j < files.length - i - 1; j++){
					if(files[j].getName().compareTo(files[j + 1].getName()) > 0){
						File f = files[j];
						files[j] = files[j + 1];
						files[j + 1] = f;
					}
				}
			}
			
			for(File fileZ : files){
				if(fileZ.isDirectory()){
					allProjectsPopup.createItem(fileZ.getName(), IconManager.projectImage, ()->{
						Screen.getScreen().getToolMenu().projectPopup.setVisible(false);
						screen.loadProject(fileZ);
					});
				}
			}
		}
		
		filePopup.addItem(allMenu);
		filePopup.createItem("Close Project", IconManager.projectImage, ()->Screen.getProjectFile().closeProject())
		.createItem("Save All Editors", IconManager.fluentsaveImage, ()->screen.saveAllEditors())
		.createItem("Restart", IconManager.fluentcloseImage, IDE::restart)
		.createItem("Exit", IconManager.closeImage, IDE::exit);
	}
	
	private void addComp(Component c) {
		add(c);
	}
	
	public void deleteDir(File file) throws Exception {
		if (file.isDirectory()) {
			if (file.list().length == 0){
				deleteEmptyDir(file);
			}
			else{
				File files[] = file.listFiles();
				for (File fileDelete : files)
					deleteDir(fileDelete);
				
				if (file.list().length == 0)
					deleteEmptyDir(file);
			}
		}
		else
			deleteEmptyDir(file);
	}
	
	private void deleteEmptyDir(File file) {
		file.delete();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		reshapeComp();
	}
	
	public class Menu extends JComponent {
		
		private String text;
		
		private volatile boolean enter;
		
		public Menu(OPopupWindow popup, String text) {
			this.text = text;
			UIManager.setData(this);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					enter = true;
					repaint();
				}
				@Override
				public void mouseExited(MouseEvent e) {
					enter = false;
					repaint();
				}
				@Override
				public void mousePressed(MouseEvent e) {
					popup.setLocation(getX() + screen.getX(), getY() + getHeight() + 15 + getHeight() + screen.getY());
					popup.setVisible(true);
				}
			});
		}
		
		@Override
		public void setFont(Font f) {
			super.setFont(PX14);
			setSize(100, ToolMenu.this.getHeight());
			setPreferredSize(getSize());
		}
		
		@Override
		public void paint(Graphics g2D) {
			Graphics2D g = (Graphics2D)g2D;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(back2);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(TOOLMENU_COLOR2);
			g.setFont(getFont());
			int x = g.getFontMetrics().stringWidth(text);
			int cx = x;
			x = getWidth()/2 - x/2;
			if(enter) {
				g.setColor(TOOLMENU_COLOR6);
				g.fillRect(x, getHeight() - 3, cx, 2);
			}
			g.setFont(getFont());
			g.drawString(text, x, getFont().getSize());
		}
	}
}

