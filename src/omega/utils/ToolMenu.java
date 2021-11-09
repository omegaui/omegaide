/**
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
package omega.utils;
import omega.update.IDEUpdater;

import omega.plugin.event.PluginReactionEvent;

import omega.instant.support.LanguageTagView;

import omega.Screen;

import omega.gset.Generator;

import java.net.URL;

import java.io.File;

import java.util.LinkedList;

import omega.framework.CodeFramework;

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

import omega.launcher.InstructionWindow;

import omega.instant.support.build.gradle.GradleModuleWizard;
import omega.instant.support.build.gradle.GradleBuildScriptManager;
import omega.instant.support.build.gradle.GradleProcessManager;

import omega.instant.support.universal.ProcessWizard;
import omega.instant.support.universal.UniversalProjectWizard;

import omega.instant.support.java.ProjectWizard;
import omega.instant.support.java.JavaSyntaxParser;

import omega.comp.TextComp;

import omega.popup.OPopupWindow;
import omega.popup.OPopupItem;
import omega.popup.NotificationPopup;

import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JComponent;
import static omega.utils.UIManager.*;
import static omega.utils.IconManager.*;
import static omega.comp.Animations.*;
/**
* This is not only the tool menu but also the IDE 's custom window decorator
*/
public class ToolMenu extends JPanel {
	private Screen screen;
	
	public OPopupWindow filePopup;
	public OPopupWindow projectPopup;
	public OPopupWindow toolsPopup;
	public OPopupWindow setPopup;
	public OPopupWindow helpPopup;
	
	public static OPopupWindow recentFilePopup;
	public static OPopupItem fileMenu;
	public static OPopupWindow recentProjectPopup;
	public static OPopupItem projectMenu;
	public static OPopupWindow allProjectsPopup;
	public static OPopupItem allMenu;
	public static OPopupItem typeItem;
	public static OPopupItem allSettingsItem;
	public static OPopupItem jdkItem;
	public static OPopupItem jdkRootItem;
	public static OPopupItem instantModeItem;
	public static OPopupItem parsingEnabledItem;
	
	public TextComp openProjectComp;
	public TextComp openFileComp;
	public TextComp newProjectComp;
	public TextComp sep0;
	public TextComp instantRunComp;
	public TextComp runComp;
	public TextComp instantBuildComp;
	public TextComp buildComp;
	public TextComp sep1;
	public TextComp newFileComp;
	public TextComp contentComp;
	public TextComp contentModeComp;
	public TextComp asteriskComp;
	public TextComp structureComp;
	public TextComp operateComp;
	public TextComp sep3;
	public TextComp shellComp;
	public TextComp searchComp;
	public TextComp sep4;
	public TextComp structureViewComp;
	public TextComp sep5;
	public TextComp themeComp;
	
	public LabelMenu taskMenu;
	
	public static InfoScreen infoScreen;
	public static ToolMenuPathBox pathBox;
	public static SourceDefender sourceDefender;
	public static ProjectWizard projectWizard;
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
	
	private int pressX;
	private int pressY;
	
	public volatile boolean hidden = false;
	public volatile boolean oPHidden = true;
	//The window decoration objects
	
	public static Color closeWinColor = TOOLMENU_COLOR2;
	public static Color maximizeWinColor = TOOLMENU_COLOR4;
	public static Color minimizeWinColor = TOOLMENU_COLOR3;
	
	public TextComp iconComp;
	public TextComp langComp;
	public TextComp titleComp;
	public TextComp minimizeComp;
	public TextComp maximizeComp;
	public TextComp closeComp;
	
	private BufferedImage image;
	
	private Point lastLocation;
	
	private Dimension lastSize;
	
	private boolean maximized = false;
	public ToolMenu(Screen screen) {
		this.screen = screen;
		if(projectWizard == null){
			infoScreen = new InfoScreen(screen);
			sourceDefender = new SourceDefender(screen);
			projectWizard = new ProjectWizard(screen);
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
		}
		setLayout(null);
		setSize(screen.getWidth(), 120);
		setPreferredSize(getSize());
		UIManager.setData(this);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				pressX = e.getX();
				pressY = e.getY();
			}
		});
		addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(!maximized)
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
		iconComp = new TextComp(image, 30, 30, c2, c2, c2, null);
		iconComp.setBounds(0, 0, 30, 30);
		iconComp.setClickable(false);
		iconComp.setArc(0, 0);
		add(iconComp);
		
		langComp = new TextComp(IconManager.fluentjavaImage, 25, 25, back2, c2, c2, this::changeLang);
		langComp.setBounds(30, 0, 30, 30);
		langComp.setArc(0, 0);
		add(langComp);
		
		titleComp = new TextComp("Omega IDE", c2, c2, glow, null);
		titleComp.setClickable(false);
		titleComp.setFont(PX16);
		titleComp.setArc(0, 0);
		titleComp.addMouseListener(new MouseAdapter(){
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
		titleComp.addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				if(!maximized)
					screen.setLocation(e.getXOnScreen() - pressX - 60, e.getYOnScreen() - pressY);
			}
		});
		add(titleComp);
		
		closeComp = new TextComp("", c1, c2, c3, this::disposeAll){
			@Override
			public void draw(Graphics2D g){
				g.setColor(c2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(closeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(c2);
					g.drawString("x", getWidth()/2 - g.getFontMetrics().stringWidth("x")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		add(closeComp);
		
		maximizeComp = new TextComp("", c1, c2, c3, this::maximize){
			@Override
			public void draw(Graphics2D g){
				g.setColor(c2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(maximizeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(c2);
					g.drawString(maximized ? "><" : "<>", getWidth()/2 - g.getFontMetrics().stringWidth("<>")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		add(maximizeComp);
		
		minimizeComp = new TextComp("", c1, c2, c3, this::minimize){
			@Override
			public void draw(Graphics2D g){
				g.setColor(c2);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(minimizeWinColor);
				g.fillRoundRect(getWidth()/2 - 10, getHeight()/2 - 10, 20, 20, 10, 10);
				if(isMouseEntered()){
					g.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 5, 5);
					g.setFont(PX14);
					g.setColor(c2);
					g.drawString("-", getWidth()/2 - g.getFontMetrics().stringWidth("-")/2,
					getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
				}
			}
		};
		minimizeComp.setArc(0, 0);
		add(minimizeComp);
		
		filePopup = OPopupWindow.gen("File Menu", screen, 0, false).width(510);
		initFilePopup();
		Menu fileMenu = new Menu(filePopup, "File");
		fileMenu.setBounds(0, 30, 60, 20);
		addComp(fileMenu);
		
		projectPopup = OPopupWindow.gen("Project Menu", screen, 0, false).width(250);
		initProjectPopup();
		Menu projectMenu = new Menu(projectPopup, "Project");
		projectMenu.setBounds(60, 30, 60, 20);
		addComp(projectMenu);
		toolsPopup = OPopupWindow.gen("Tools Menu", screen, 0, false).width(300);
		
		initToolMenu();
		Menu toolsMenu = new Menu(toolsPopup, "Tools");
		toolsMenu.setBounds(120, 30, 60, 20);
		addComp(toolsMenu);
		
		setPopup = OPopupWindow.gen("Settings Menu", screen, 0, false).width(250);
		initSetMenu();
		Menu setMenu = new Menu(setPopup, "Settings");
		setMenu.setBounds(180, 30, 60, 20);
		addComp(setMenu);
		
		helpPopup = OPopupWindow.gen("Help Menu", screen, 0, false).width(300);
		initHelpMenu();
		Menu helpMenu = new Menu(helpPopup, "Help");
		helpMenu.setBounds(240, 30, 60, 20);
		addComp(helpMenu);
		
		taskMenu = new LabelMenu("Click to Run Garbage Collector", ()->{
			if(CodeFramework.resolving) return;
			setTask("Running Java Garbage Collector");
			taskMenu.repaint();
			System.gc();
			if(!CodeFramework.resolving) {
				long ram = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				ram = (long)(ram / 1000000);
				setTask("Using " + (ram) + " MB of Physical Memory Excluding JVM");
			}
			}, ()->{
			if(!CodeFramework.resolving) {
				long ram = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				ram = (long)(ram / 1000000);
				setTask("Using " + (ram) + " MB of Physical Memory Excluding JVM");
			}
		});
		taskMenu.setFont(PX14);
		setTask("Hover to see Memory Statistics");
		
		openProjectComp = new TextComp(fluentfolderImage, 20, 20, "Open Project", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->Screen.getFileView().open("Project"));
		openProjectComp.setBounds(0, 55, 30, 30);
		openProjectComp.setFont(omega.utils.UIManager.PX14);
		openProjectComp.setArcVisible(true, false, true, false);
		addComp(openProjectComp);
		
		openFileComp = new TextComp(fluentfileImage, 20, 20, "Open File", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->Screen.getFileView().open("File"));
		openFileComp.setBounds(32, 55, 30, 30);
		openFileComp.setFont(omega.utils.UIManager.PX14);
		openFileComp.setArcVisible(true, false, true, false);
		addComp(openFileComp);
		
		newProjectComp = new TextComp(fluentnewfolderImage, 20, 20, "Create New Project", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->projectWizard.setVisible(true));
		newProjectComp.setBounds(64, 55, 30, 30);
		newProjectComp.setFont(omega.utils.UIManager.PX14);
		newProjectComp.setArcVisible(true, false, true, false);
		addComp(newProjectComp);
		
		newFileComp = new TextComp(fluentnewfileImage, 20, 20, "Create New File", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->Screen.getFileView().getFileCreator().show("Custom File"));
		newFileComp.setBounds(96, 55, 30, 30);
		newFileComp.setFont(PX14);
		newFileComp.setArcVisible(true, false, true, false);
		addComp(newFileComp);
		
		sep0 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep0.setBounds(130, 50, 2, 40);
		addComp(sep0);
		
		runComp = new TextComp(fluentrunImage, 20, 20, "Run Project, Right Click to launch without build! (Not for Gradle)", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
			if(runComp.isClickable() && buildComp.isClickable()){
				if(GradleProcessManager.isGradleProject())
					GradleProcessManager.run();
				else
					Screen.getRunView().run();
			}
		});
		runComp.setBounds(140, 55, 30, 30);
		runComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(e.getButton() == 3){
					if(runComp.isClickable() && buildComp.isClickable())
						Screen.getRunView().justRun();
				}
			}
		});
		runComp.setArcVisible(true, false, true, false);
		add(runComp);
		
		instantRunComp = new TextComp(fluentrocketImage, 20, 20, "Instant Run(Java Only), Uses System Default JDK for Building Project", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
			Screen.getRunView().instantRun();
		});
		instantRunComp.setBounds(172, 55, 30, 30);
		instantRunComp.setArcVisible(true, false, true, false);
		add(instantRunComp);
		
		buildComp = new TextComp(fluentbuildImage, 20, 20, "Time to Build", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
			if(runComp.isClickable() && buildComp.isClickable()){
				if(GradleProcessManager.isGradleProject())
					GradleProcessManager.build();
				else
					Screen.getBuildView().compileProject();
			}
		});
		buildComp.setBounds(204, 55, 30, 30);
		buildComp.setFont(omega.utils.UIManager.PX18);
		buildComp.setArcVisible(false, true, false, true);
		add(buildComp);
		
		instantBuildComp = new TextComp(fluentrocketbuildImage, 20, 20, "Instant Build(Java Only), Uses System Default JDK for Building Project", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
			Screen.getRunView().instantBuild();
		});
		instantBuildComp.setBounds(236, 55, 30, 30);
		instantBuildComp.setArcVisible(false, true, false, true);
		add(instantBuildComp);
		
		sep1 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep1.setBounds(272, 50, 2, 40);
		addComp(sep1);
		
		contentComp = new TextComp("", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->{
			DataManager.setContentAssistRealTime(!DataManager.isContentAssistRealTime());
			contentComp.setToolTipText(DataManager.isContentAssistRealTime() ? "Content Assist is ON" : "Content Assist is Stopped");
			contentComp.repaint();
			}){
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillOval(7, 7, 16, 16);
				if(DataManager.isContentAssistRealTime()) {
					g.setColor(color2);
					g.fillOval(10, 10, 10, 10);
					g.setColor(color1);
					g.fillOval(10, 10, 10, 10);
				}
			}
		};
		contentComp.setBounds(282, 55, 30, 30);
		contentComp.setToolTipText(DataManager.isContentAssistRealTime() ? "Content Assist is ON" : "Content Assist is Stopped");
		contentComp.setArcVisible(false, true, false, true);
		addComp(contentComp);
		
		contentModeComp = new TextComp("", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->{
			DataManager.setContentModeJava(!DataManager.isContentModeJava());
			contentModeComp.setToolTipText(DataManager.isContentModeJava() ? "Content Assist Mode : Java" : "Content Assist Mode : Tokenizer");
			contentModeComp.repaint();
			}){
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillOval(7, 7, 16, 16);
				if(DataManager.isContentModeJava()) {
					g.setColor(color2);
					g.fillOval(10, 10, 10, 10);
					g.setColor(color1);
					g.fillOval(10, 10, 10, 10);
				}
			}
		};
		contentModeComp.setBounds(314, 55, 30, 30);
		contentModeComp.setToolTipText(DataManager.isContentModeJava() ? "Content Assist Mode : Java" : "Content Assist Mode : Tokenizer");
		contentModeComp.setArcVisible(false, true, false, true);
		addComp(contentModeComp);
		
		asteriskComp = new TextComp("", TOOLMENU_COLOR2_SHADE, back2, TOOLMENU_COLOR2, ()->{
			DataManager.setUseStarImports(!DataManager.isUsingStarImports());
			asteriskComp.setToolTipText(DataManager.isUsingStarImports() ? "Using Asterisk Imports" : "Using Named Imports");
			asteriskComp.repaint();
			}){
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillOval(7, 7, 16, 16);
				if(!DataManager.isUsingStarImports()) {
					g.setColor(color2);
					g.fillOval(10, 10, 10, 10);
					g.setColor(color1);
					g.fillOval(10, 10, 10, 10);
				}
			}
		};
		asteriskComp.setBounds(346, 55, 30, 30);
		asteriskComp.setToolTipText(DataManager.isUsingStarImports() ? "Using Asterisk Imports" : "Using Named Imports");
		addComp(asteriskComp);
		
		structureComp = new TextComp("", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, ()->{
			hidden = !hidden;
			screen.screenHasProjectView = !screen.screenHasProjectView;
			Screen.getProjectView().organizeProjectViewDefaults();
			screen.doLayout();
			Screen.getProjectView().setVisible(false);
			structureComp.setToolTipText(hidden ? "Project Structure Hidden" : "Project Structure Visible");
			structureComp.repaint();
			}){
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillOval(7, 7, 16, 16);
				if(hidden) {
					g.setColor(color2);
					g.fillOval(10, 10, 10, 10);
					g.setColor(color1);
					g.fillOval(10, 10, 10, 10);
				}
			}
		};
		structureComp.setBounds(378, 55, 30, 30);
		structureComp.setToolTipText(hidden ? "Project Structure Hidden" : "Project Structure Visible");
		structureComp.setArcVisible(true, false, true, false);
		addComp(structureComp);
		
		operateComp = new TextComp("", TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3, ()->{
			if(!oPHidden) {
				screen.getOperationPanel().setVisible(false);
				oPHidden = true;
			}
			else {
				screen.getOperationPanel().setVisible(true);
				oPHidden = false;
			}
			operateComp.setToolTipText(oPHidden ? "Operation Panel Hidden" : "Operation Panel Visible");
			operateComp.repaint();
			}){
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillOval(7, 7, 16, 16);
				if(oPHidden) {
					g.setColor(color2);
					g.fillOval(10, 10, 10, 10);
					g.setColor(color1);
					g.fillOval(10, 10, 10, 10);
				}
			}
		};
		operateComp.setBounds(410, 55, 30, 30);
		operateComp.setToolTipText(oPHidden ? "Operation Panel Hidden" : "Operation Panel Visible");
		operateComp.setArcVisible(true, false, true, false);
		addComp(operateComp);
		
		searchComp = new TextComp(fluentsearchImage, 25, 25, "Search and Open File", TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->Screen.getFileView().getSearchWindow().setVisible(true));
		addComp(searchComp);
		
		sep4 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		addComp(sep4);
		
		structureViewComp = new TextComp(fluentstructureImage, 25, 25, TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, ()->structureView.setVisible(true));
		structureViewComp.setFont(omega.utils.UIManager.PX18);
		structureViewComp.setToolTipText("Lets see that class");
		addComp(structureViewComp);
		
		sep3 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep3.setBounds(444, 50, 2, 40);
		addComp(sep3);
		
		OPopupWindow consoleItemWindow = new OPopupWindow("Select Console Type", screen, 0, false).width(200);
		consoleItemWindow
		.createItem("New System Terminal", IconManager.fluentconsoleImage, consoleSelector::launchTerminal)
		.createItem("Integrated Terminal", IconManager.fluentconsoleImage, Screen.getTerminalComp()::showTerminal);
		
		shellComp = new TextComp(fluentconsoleImage, 25, 25, TOOLMENU_COLOR1_SHADE, back2, TOOLMENU_COLOR1, null);
		shellComp.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				consoleItemWindow.setLocation(e.getLocationOnScreen());
				consoleItemWindow.setVisible(true);
			}
		});
		shellComp.setFont(omega.utils.UIManager.PX16);
		shellComp.setBounds(454, 55, 60, 30);
		shellComp.setArcVisible(false, true, false, true);
		add(shellComp);
		
		sep5 = new TextComp("", TOOLMENU_COLOR3_SHADE, TOOLMENU_COLOR3, TOOLMENU_COLOR3, null);
		sep5.setBounds(521, 50, 2, 40);
		addComp(sep5);
		
		themeComp = new TextComp(DataManager.getTheme(), TOOLMENU_COLOR3_SHADE, back2, TOOLMENU_COLOR3,
		()->{
			Screen.pickTheme(DataManager.getTheme());
			if(!themeComp.getText().equals(DataManager.getTheme())){
				NotificationPopup.create(screen)
				.size(300, 120)
				.title("Theme Manager")
				.dialogIcon(IconManager.fluentupdateImage)
				.message("IDE's Restart is Required!", TOOLMENU_COLOR4)
				.shortMessage("Click this to Restart", TOOLMENU_COLOR2)
				.iconButton(IconManager.fluentcloseImage, ()->{
					Screen.notify("Terminating Running Applications");
					try{
						for(Process p : Screen.getRunView().runningApps) {
							if(p.isAlive())
								p.destroyForcibly();
						}
					}
					catch(Exception e2) {
						
					}
					Screen.notify("Saving UI and Data");
					screen.getUIManager().save();
					screen.getDataManager().saveData();
					Screen.notify("Saving Project");
					screen.saveAllEditors();
					try{
						Screen.getFileView().getProjectManager().save();
					}
					catch(Exception e2) {
						
					}
					
					new Thread(()->{
						try{
							if(Screen.onWindows())
								new ProcessBuilder("java", "-jar", "Omega IDE.jar").start();
							else
								new ProcessBuilder("omega-ide").start();
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}).start();
					
					screen.dispose();
				}, "")
				.build()
				.locateOnBottomLeft()
				.showIt();
			}
			themeComp.setText(DataManager.getTheme());
		}
		);
		themeComp.setFont(omega.utils.UIManager.PX16);
		themeComp.setBounds(532, 55, 60, 30);
		themeComp.setArcVisible(true, false, true, false);
		add(themeComp);
		
		pathBox = new ToolMenuPathBox();
		add(pathBox);
		
		reshapeComp();
		
		putAnimationLayer(openProjectComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(openFileComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(newProjectComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(newFileComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(runComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(instantRunComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(buildComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(instantBuildComp, getImageSizeAnimationLayer(25, 5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(shellComp, getImageSizeAnimationLayer(25, -8, false), ACTION_MOUSE_ENTERED);
		putAnimationLayer(structureViewComp, getImageSizeAnimationLayer(25, -5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(searchComp, getImageSizeAnimationLayer(25, -5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(langComp, getImageSizeAnimationLayer(25, -5, true), ACTION_MOUSE_ENTERED);
		putAnimationLayer(iconComp, getImageSizeAnimationLayer(25, -5, true), ACTION_MOUSE_ENTERED);

		putComp(langComp, getImageSizeAnimationLayer(50, -5, true));
		putComp(iconComp, getImageSizeAnimationLayer(50, -5, true));
	}
	
	public void minimize(){
		screen.setState(Screen.ICONIFIED);
		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_MINIMIZED, this, true));
	}
	
	public void maximize(){
		screen.setExtendedState(!maximized ? Screen.MAXIMIZED_BOTH : Screen.NORMAL);
		maximized = !maximized;
		Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(maximized ? PluginReactionEvent.EVENT_TYPE_IDE_MAXIMIZED : PluginReactionEvent.EVENT_TYPE_IDE_RESTORED, this, maximized));
	}
	
	public void disposeAll(){
		screen.dispose();
	}
	
	public void setTask(String task) {
		taskMenu.setText(task);
		taskMenu.repaint();
	}
	
	public static omega.utils.ToolMenuPathBox getPathBox() {
		return pathBox;
	}
	
	public void setMsg(String msg) {
		taskMenu.setMsg(msg);
		taskMenu.repaint();
	}
	
	public void reshapeComp() {
		searchComp.setBounds(getWidth() - 30, 55, 30, 30);
		sep4.setBounds(getWidth() - 40, 50, 2, 40);
		structureViewComp.setBounds(getWidth() - 110, 55, 60, 30);
		taskMenu.setLocation(getWidth() - taskMenu.getWidth(), 30);
		pathBox.setBounds(0, 90, getWidth(), 25);
		
		//Window Decorations
		titleComp.setBounds(60, 0, getWidth() - (30 * 5), 30);
		closeComp.setBounds(getWidth() - 30, 0, 30, 30);
		maximizeComp.setBounds(getWidth() - (30 * 2), 0, 30, 30);
		minimizeComp.setBounds(getWidth() - (30 * 3), 0, 30, 30);
	}
	public void changeLocations(boolean non_java){
		if(non_java){
			buildComp.setBounds(204 - 30, 55, 30, 30);
			contentComp.setBounds(282 - 62, 55, 30, 30);
			structureComp.setBounds(316 - 4 - 60, 55, 30, 30);
			operateComp.setBounds(348 - 4 - 60, 55, 30, 30);
			sep1.setBounds(272 - 60, 50, 2, 40);
			sep3.setBounds(382 - 4 - 60, 50, 2, 40);
			shellComp.setBounds(392 - 4 - 60, 55, 60, 30);
			sep5.setBounds(457 - 4 - 60, 50, 2, 40);
			themeComp.setBounds(472 - 4 - 60, 55, 60, 30);
		}
		else {
			buildComp.setBounds(204, 55, 30, 30);
			contentComp.setBounds(282, 55, 30, 30);
			structureComp.setBounds(378, 55, 30, 30);
			operateComp.setBounds(410, 55, 30, 30);
			sep1.setBounds(272, 50, 2, 40);
			sep3.setBounds(444, 50, 2, 40);
			shellComp.setBounds(454, 55, 60, 30);
			sep5.setBounds(521, 50, 2, 40);
			themeComp.setBounds(532, 55, 60, 30);
		}
		reloadItems(non_java);
		repaint();
	}
	public void reloadItems(boolean non_java){
		jdkItem.setEnabled(!non_java);
		jdkRootItem.setToolTipText(DataManager.getPathToJava());
		allSettingsItem.setEnabled(non_java);
		instantModeItem.setEnabled(!non_java);
		parsingEnabledItem.setEnabled(!non_java);
		if(Screen.getFileView().getJDKManager() != null)
			jdkItem.setName("Project JDK : Java " + Screen.getFileView().getJDKManager().getVersionAsInt());
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
		langComp.image = LanguageTagView.getRespectiveTagImage(Screen.getFileView().getProjectManager().getLanguageTag());
		langComp.repaint();
	}
	
	private void initSetMenu() {
		FontChooser fontC = new FontChooser(screen);
		setPopup.createItem("Change Font", IconManager.settingsImage, ()->{
			Font font = fontC.chooseFont(new Font(UIManager.fontName, UIManager.fontState, UIManager.fontSize));
			UIManager.setData(font.getSize(), font.getName(), font.getStyle());
			screen.getUIManager().save();
			screen.loadThemes();
		})
		.createItem("Change Content Assist Font", IconManager.settingsImage, ()->{
			Font font = fontC.chooseFont(DataManager.getHintFont());
			DataManager.setHintFont(font);
		})
		.createItem("Change Workspace", IconManager.settingsImage, ()->new omega.utils.WorkspaceSelector(screen).setVisible(true))
		.createItem("Set System Terminal", IconManager.fluentconsoleImage, ()->{
			consoleSelector.setVisible(true);
		})
		.createItem("Set Gradle Script", IconManager.fluentgradleImage, ()->{
			gradleBuildScriptManager.setVisible(true);
		});
		
		typeItem = new OPopupItem(setPopup, "Project Type : Non-Java", IconManager.settingsImage, null);
		typeItem.setAction(()->{
			omega.Screen.getFileView().getProjectManager().non_java = !omega.Screen.getFileView().getProjectManager().non_java;
			typeItem.setName(omega.Screen.getFileView().getProjectManager().non_java ? "Project Type : Other/Gradle" : "Project Type : Java");
			omega.Screen.getScreen().manageTools(omega.Screen.getFileView().getProjectManager());
			omega.Screen.getFileView().getProjectManager().save();
			typeItem.setToolTipText("Please Relaunch the IDE");
			NotificationPopup.create(screen)
			.title("Project Management")
			.dialogIcon(IconManager.fluentfolderImage)
			.message("IDE's Restart is Required!", TOOLMENU_COLOR4)
			.shortMessage("Click this to Exit", TOOLMENU_COLOR2)
			.iconButton(IconManager.fluentcloseImage, ()->{
				Screen.notify("Terminating Running Applications");
				try{
					for(Process p : Screen.getRunView().runningApps) {
						if(p.isAlive())
							p.destroyForcibly();
					}
				}
				catch(Exception e2) {
					
				}
				Screen.notify("Saving UI and Data");
				Screen.getPluginManager().save();
				screen.getUIManager().save();
				screen.getDataManager().saveData();
				Screen.notify("Saving Project");
				screen.saveAllEditors();
				try{
					Screen.getFileView().getProjectManager().save();
				}
				catch(Exception e2) {
					
				}
				System.exit(0);
			}, "You need to start the IDE again manually!")
			.build()
			.locateOnBottomLeft()
			.showIt();
		});
		
		SDKSelector sdkSelector = new SDKSelector(screen);
		
		jdkItem = new OPopupItem(setPopup, "Project JDK : None", IconManager.fluentsourceImage, ()->{
			sdkSelector.setVisible(true);
			String sel = sdkSelector.getSelection();
			if(Screen.isNotNull(sel)) {
				Screen.getFileView().getProjectManager().setJDKPath(sel);
				jdkItem.setName("Project JDK : Java " + Screen.getFileView().getJDKManager().getVersionAsInt());
			}
		});
		
		FileSelectionDialog fs = new FileSelectionDialog(screen);
		fs.setTitle("Select JDK Root");
		
		jdkRootItem = new OPopupItem(setPopup, "Set JDK Root", IconManager.fluentsourceImage, ()->{
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
		
		allSettingsItem = new OPopupItem(setPopup, "Settings (Non-Java)", IconManager.settingsImage, ()->{
			if(GradleProcessManager.isGradleProject()) {
				Screen.getScreen().loadFile(new File(Screen.getFileView().getProjectPath(), "settings.gradle"));
				return;
			}
			if(Screen.getFileView().getProjectManager().non_java)
				Screen.getUniversalSettingsView().setVisible(true);
		});
		
		setPopup.addItem(typeItem);
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
		.createItem("Plugin Store", IconManager.ideImage64, ()->Screen.getPluginStore().setVisible(true))
		.createItem("Plugin Manager", IconManager.ideImage64, ()->Screen.getPluginsView().setVisible(true))
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
	private void initToolMenu() {
		toolsPopup
		.createItem("Source Defender", IconManager.fluentsourceImage, ()->sourceDefender.setVisible(true))
		.createItem("Process Wizard", IconManager.fluentbuildImage, ()->processWizard.setVisible(true))
		.createItem("Snippet Manager", IconManager.buildImage, ()->Screen.snippetView.setVisible(true))
		.createItem("Generate Getter/Setter", IconManager.buildImage, ()->omega.gset.Generator.gsView.genView(screen.getCurrentEditor()))
		.createItem("Override/Implement Methods", IconManager.buildImage, ()->omega.gset.Generator.overView.genView(screen.getCurrentEditor()))
		.createItem("Color Picker", IconManager.fluentcolorwheelImage, ()->colorPicker.pickColor());
	}
	private void initProjectPopup() {
		JFileChooser fileC = new JFileChooser();
		projectPopup.createItem("Manage Build-Path", IconManager.projectImage, ()->Screen.getFileView().getDependencyView().setVisible(true))
		.createItem("Add Additional Flags", IconManager.projectImage, ()->{
			Screen.getFileView().getExtendedDependencyView().setVisible(true);
		})
		.createItem("Refresh", IconManager.projectImage, ()->Screen.getProjectView().reload())
		.createItem("Initialize Gradle", IconManager.fluentgradleImage, GradleProcessManager::init)
		.createItem("Create Gradle Module", IconManager.fluentgradleImage, ()->ToolMenu.gradleModuleWizard.setVisible(true))
		.createItem("Delete Project", IconManager.fluentdemonImage, ()->projectDistructionWizard.setVisible(true));
	}
	private void initFilePopup() {
		//New Menu Items
		filePopup.createItem("Open File", IconManager.fileImage, ()->Screen.getFileView().open("File"))
		.createItem("Open Project", IconManager.projectImage, ()->Screen.getFileView().open("Project"))
		.createItem("New Project", IconManager.projectImage, ()->projectWizard.setVisible(true))
		.createItem("New Project (non-java project)", IconManager.projectImage, ()->universalProjectWizard.setVisible(true));
		recentFilePopup = OPopupWindow.gen("Recent Files Menu", screen, 0, true).width(350).height(250);
		fileMenu = new OPopupItem(recentFilePopup, "Recent Files", IconManager.fileImage, ()->{
			recentFilePopup.setLocationRelativeTo(null);
			recentFilePopup.setVisible(true);
		});
		filePopup.addItem(fileMenu);
		
		recentProjectPopup = OPopupWindow.gen("Recent Projects Menu", screen, 0, true).width(350).height(250);
		projectMenu = new OPopupItem(recentProjectPopup, "Recent Projects", IconManager.projectImage, ()->{
			recentProjectPopup.setLocationRelativeTo(null);
			recentProjectPopup.setVisible(true);
		});
		filePopup.addItem(projectMenu);
		
		allProjectsPopup = OPopupWindow.gen("All Projects Menu", screen, 0, true).width(350).height(250);
		allMenu = new OPopupItem(allProjectsPopup, "All Projects", IconManager.projectImage, ()->{
			allProjectsPopup.setLocationRelativeTo(null);
			allProjectsPopup.setVisible(true);
		});
		filePopup.addItem(allMenu);
		filePopup.createItem("Close Project", IconManager.projectImage, ()->Screen.getFileView().closeProject())
		.createItem("Save All Editors", IconManager.fileImage, ()->screen.saveAllEditors())
		.createItem("Restart", IconManager.fluentcloseImage, ()->{
			
			Screen.notify("Terminating Running Applications");
			try{
				for(Process p : Screen.getRunView().runningApps) {
					if(p.isAlive())
						p.destroyForcibly();
				}
			}
			catch(Exception e2) {
				
			}
			Screen.notify("Saving UI and Data");
			Screen.getPluginManager().save();
			screen.getUIManager().save();
			screen.getDataManager().saveData();
			Screen.notify("Saving Project");
			screen.saveAllEditors();
			try{
				Screen.getFileView().getProjectManager().save();
			}
			catch(Exception e2) {
				
			}
			
			new Thread(()->{
				try{
					if(Screen.onWindows())
						new ProcessBuilder("java", "-jar", "Omega IDE.jar").start();
					else
						new ProcessBuilder("omega-ide").start();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}).start();
			
			screen.dispose();
		})
		.createItem("Save Everything and Exit -Without terminating running apps", IconManager.closeImage,
		()->{
			Screen.notify("Saving UI and Data");
			Screen.getPluginManager().save();
			screen.getUIManager().save();
			screen.getDataManager().saveData();
			Screen.notify("Saving Project");
			screen.saveAllEditors();
			try{
				Screen.getFileView().getProjectManager().save();
			}
			catch(Exception e2) {
			}
			Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_CLOSING, this, 0));
			System.exit(0);
		})
		.createItem("Terminate Running Apps and Exit -Without saving opened editors", IconManager.closeImage,
		()->{
			Screen.notify("Terminating Running Applications");
			try{
				for(Process p : Screen.getRunView().runningApps) {
					if(p.isAlive())
						p.destroyForcibly();
				}
			}
			catch(Exception e2) {}
			Screen.notify("Saving UI and Data");
			Screen.getPluginManager().save();
			screen.getUIManager().save();
			screen.getDataManager().saveData();
			try{
				Screen.getFileView().getProjectManager().save();
			}
			catch(Exception e2) {
			}
			Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_CLOSING, this, 0));
			System.exit(0);
		})
		.createItem("Exit", IconManager.closeImage,
		()->{
			Screen.notify("Terminating Running Applications");
			try{
				for(Process p : Screen.getRunView().runningApps) {
					if(p.isAlive())
						p.destroyForcibly();
				}
			}
			catch(Exception e2) {
				
			}
			Screen.notify("Saving UI and Data");
			Screen.getPluginManager().save();
			screen.getUIManager().save();
			screen.getDataManager().saveData();
			Screen.notify("Saving Project");
			screen.saveAllEditors();
			try{
				Screen.getFileView().getProjectManager().save();
			}
			catch(Exception e2) {
			}
			Screen.getPluginReactionManager().triggerReaction(PluginReactionEvent.genNewInstance(PluginReactionEvent.EVENT_TYPE_IDE_CLOSING, this, 0));
			System.exit(0);
		});
	}
	
	private void addComp(Component c) {
		add(c);
	}
	
	public void deleteDir(File file) throws Exception {
		if (file.isDirectory())
			{
			/*
			* If directory is empty, then delete it
			*/
			if (file.list().length == 0)
				{
				deleteEmptyDir(file);
			}
			else
				{
				// list all the directory contents
				File files[] = file.listFiles();
				for (File fileDelete : files)
					{
					/*
					* Recursive delete
					*/
					deleteDir(fileDelete);
				}
				/*
				* check the directory again, if empty then
				* delete it.
				*/
				if (file.list().length == 0)
					{
					deleteEmptyDir(file);
				}
			}
		}
		else
			{
			/*
			* if file, then delete it
			*/
			deleteEmptyDir(file);
		}
	}
	private void deleteEmptyDir(File file) {
		file.delete();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		reshapeComp();
	}
	public class LabelMenu extends JComponent {
		private String text;
		private String msg;
		private volatile boolean enter;
		
		public LabelMenu(String text, Runnable r, Runnable x) {
			this.text = "";
			setToolTipText(text);
			UIManager.setData(this);
			setForeground(TOOLMENU_COLOR3);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					enter = true;
					x.run();
					repaint();
				}
				@Override
				public void mouseExited(MouseEvent e) {
					enter = false;
					if(!CodeFramework.resolving)
						setTask("Hover to see Memory Statistics");
					repaint();
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					r.run();
					repaint();
				}
			});
			addComp(this);
		}
		public void setText(String text) {
			if(this.msg == null)
				this.text = text;
			repaint();
		}
		public void setMsg(String msg) {
			this.msg = msg;
			if(msg != null)
				this.text = msg;
			else
				this.text = "Hover to see Memory Statistics";
			repaint();
		}
		@Override
		public void setFont(Font f) {
			super.setFont(f);
			setSize(100, ToolMenu.this.getHeight());
			setPreferredSize(getSize());
		}
		@Override
		public void paint(Graphics g2D) {
			Graphics2D g = (Graphics2D)g2D;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setPaint(new GradientPaint(0, 0, TOOLMENU_COLOR2, getWidth(), getHeight(), TOOLMENU_COLOR4));
			g.setFont(getFont());
			int x = g.getFontMetrics().stringWidth(text);
			int cx = x;
			if(getWidth() + 3 != x + 3) {
				setSize(x + 3, 20);
				setPreferredSize(getSize());
				reshapeComp();
			}
			x = getWidth()/2 - x/2;
			g.drawString(text, x, getFont().getSize());
			if(enter) {
				g.setFont(getFont());
				g.drawString(text, x, getFont().getSize());
			}
		}
	}
	
	public class Menu extends JComponent {
		private String text;
		private volatile boolean enter;
		public Menu(OPopupWindow popup, String text) {
			this.text = text;
			UIManager.setData(this);
			setForeground(TOOLMENU_COLOR2);
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
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(getForeground());
			g.setFont(getFont());
			int x = g.getFontMetrics().stringWidth(text);
			int cx = x;
			x = getWidth()/2 - x/2;
			g.drawString(text, x, getFont().getSize());
			if(enter) {
				g.setColor(getForeground());
				g.fillRect(x, getHeight() - 3, cx, 2);
				g.setFont(getFont());
				g.drawString(text, x, getFont().getSize());
			}
		}
	}
}

