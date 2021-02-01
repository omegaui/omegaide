package ide.utils;
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
import gset.Generator;
import ide.Manuals;
import creator.UniversalProjectWizard;
import java.awt.Dimension;
import popup.OPopupWindow;
import popup.OPopupItem;

import java.nio.file.Files;
import java.awt.Panel;
import java.awt.Menu;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import creator.ProjectWizard;
import deassembler.CodeFramework;
import ide.Screen;
import ide.utils.systems.EditorTools;
import static ide.utils.UIManager.*;
import importIO.ImportManager;
import say.swing.JFontChooser;
import settings.comp.TextComp;
import tabPane.IconManager;

public class ToolMenu extends JPanel {

	private Screen screen;
	public OPopupWindow filePopup;
	public OPopupWindow projectPopup;
	public OPopupWindow toolsPopup;
	public OPopupWindow viewPopup;
	public OPopupWindow setPopup;
	public OPopupWindow helpPopup;

     public static OPopupWindow recentFilePopup;
     public static OPopupItem fileMenu;
     public static OPopupWindow recentProjectPopup;
     public static OPopupItem projectMenu;
     public static OPopupWindow allProjectsPopup;
     public static OPopupItem allMenu;

	public TextComp openProjectComp;
	public TextComp openFileComp;
	public TextComp newProjectComp;
	public TextComp sep0;
	public TextComp runComp;
	public TextComp buildComp;
	public TextComp sep1;
	public TextComp classComp;
	public TextComp intComp;
	public TextComp annComp;
	public TextComp enComp;
	public TextComp fileComp;
	public TextComp sep2;
	public TextComp contentComp;
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
	public volatile boolean oPHidden = true;
	public static JMenu openFileMenu;
	public static JMenu openProjectMenu;
	public static info.Screen infoScreen;
     public static structure.Screen structureView;
	public boolean hidden;
     public static ProjectWizard projectWizard;
     public static UniversalProjectWizard universalProjectWizard;
     private static final Font font = new Font("Ubuntu Mono", Font.BOLD, 14);
     private int pressX;
     private int pressY;
     

	public ToolMenu(Screen screen) {
		this.screen = screen;
		if(projectWizard == null){
               projectWizard = new ProjectWizard(screen);
               universalProjectWizard = new UniversalProjectWizard(screen);
               infoScreen = new info.Screen(screen);
               structureView = new structure.Screen(screen);
	     }
		setLayout(null);
		setSize(screen.getWidth(), 60);
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
                    screen.setLocation(e.getXOnScreen() - pressX, e.getYOnScreen() - pressY - 30);
               }
          });
		init();
	}

	private void init() {
		filePopup = OPopupWindow.gen("File Menu", screen, 0, false).width(600);
          initFilePopup();
          Menu fileMenu = new Menu(filePopup, "File");
		fileMenu.setBounds(0, 0, 60, 20);
		addComp(fileMenu);

          projectPopup = OPopupWindow.gen("Project Menu", screen, 0, false).width(400);
		initProjectPopup();
		Menu projectMenu = new Menu(projectPopup, "Project");
		projectMenu.setBounds(60, 0, 60, 20);
		addComp(projectMenu);

		toolsPopup = OPopupWindow.gen("Tools Menu", screen, 0, false).width(300);
		initToolMenu();
		Menu toolsMenu = new Menu(toolsPopup, "Tools");
		toolsMenu.setBounds(120, 0, 60, 20);
		addComp(toolsMenu);

		viewPopup = OPopupWindow.gen("View Menu", screen, 0, false).width(300);
		initViewMenu();
		Menu viewMenu = new Menu(viewPopup, "View ");
		viewMenu.setBounds(180, 0, 60, 20);
		addComp(viewMenu);

		setPopup = OPopupWindow.gen("Settings Menu", screen, 0, false).width(400);
		initSetMenu();
		Menu setMenu = new Menu(setPopup, "Settings");
		setMenu.setBounds(240, 0, 60, 20);
		addComp(setMenu);

		helpPopup = OPopupWindow.gen("Help Menu", screen, 0, false).width(300);
		initHelpMenu();
		Menu helpMenu = new Menu(helpPopup, "Help");
		helpMenu.setBounds(300, 0, 60, 20);
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
          taskMenu.setFont(font);
		setTask("Hover to see Memory Statistics");

		openProjectComp = new TextComp("", "Open Project", c1, c2, c3, ()->Screen.getFileView().open("Project")) {
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillRoundRect(7, 10, 16, 13, 5, 5);
				g.drawRect(7, 6, 8, 4);
				g.drawRect(7, 7, 8, 1);
			}
		};
		openProjectComp.setBounds(0, 25, 30, 30);
		openProjectComp.setFont(settings.Screen.PX14);
		addComp(openProjectComp);

		openFileComp = new TextComp("", "Open File", c1, c2, c3, ()->Screen.getFileView().open("File")) {
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillRoundRect(7, 7, 16, 16, 5, 5);
				g.setColor(color2);
				g.fillRect(10, 10, 3, 1);
				g.fillRect(14, 10, 3, 1);
				g.fillRect(12, 14, 3, 1);
				g.fillRect(16, 14, 3, 1);
				g.fillRect(10, 18, 3, 1);
				g.fillRect(14, 18, 3, 1);
			}
		};
		openFileComp.setBounds(32, 25, 30, 30);
		openFileComp.setFont(settings.Screen.PX14);
		addComp(openFileComp);

		newProjectComp = new TextComp("", "Create New Project", c1, c2, c3, ()->projectWizard.setVisible(true)) {
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillRoundRect(7, 10, 16, 13, 5, 5);
				g.drawRect(7, 6, 8, 4);
				g.drawRect(7, 7, 8, 1);
				g.setColor(color2);
				g.fillRect(18 - 3, 12, 1, 8);
				g.fillRect(14 - 3, 16, 9, 1);
			}
		};
		newProjectComp.setBounds(64, 25, 30, 30);
		newProjectComp.setFont(settings.Screen.PX14);
		addComp(newProjectComp);

		sep0 = new TextComp("", c1, c3, c3, ()->{});
		sep0.setBounds(100, 20, 2, 40);
		addComp(sep0);

		runComp = new TextComp(">", "Lets Run", c1, c2, c3, ()->{
			if(runComp.isClickable() && buildComp.isClickable())
				Screen.getRunView().run();
		});
		runComp.setBounds(110, 25, 30, 30);
		runComp.setFont(settings.Screen.PX28);
		add(runComp);

		buildComp = new TextComp("", "Time to Build", c1, c2, c3, ()->{
			if(runComp.isClickable() && buildComp.isClickable())
				Screen.getBuildView().compileProject();
		}) {
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.fillOval(12, 5, 14, 14);
				g.drawLine(13, 15, 5, 24);
				g.drawLine(13, 16, 5, 25);
				g.setColor(color2);
				g.fillOval(20, 5, 8, 8);
			}
		};
		buildComp.setBounds(142, 25, 30, 30);
		buildComp.setFont(settings.Screen.PX18);
		add(buildComp);

		sep1 = new TextComp("", c1, c3, c3, ()->{});
		sep1.setBounds(177, 20, 2, 40);
		addComp(sep1);

		classComp = new TextComp("C", "Create Class", c1, c2, c3, ()->Screen.getFileView().getFileCreator().show("class"));
		classComp.setBounds(187, 25, 30, 30);
		classComp.setFont(settings.Screen.PX18);
		addComp(classComp);

		intComp = new TextComp("I", "Create Interface", c1, c2, c3, ()->Screen.getFileView().getFileCreator().show("interface"));
		intComp.setBounds(219, 25, 30, 30);
		intComp.setFont(settings.Screen.PX18);
		addComp(intComp);

		annComp = new TextComp("A", "Create Annotation", c1, c2, c3, ()->Screen.getFileView().getFileCreator().show("@interface"));
		annComp.setBounds(251, 25, 30, 30);
		annComp.setFont(settings.Screen.PX18);
		addComp(annComp);

		enComp = new TextComp("E", "Create Enum", c1, c2, c3, ()->Screen.getFileView().getFileCreator().show("enum"));
		enComp.setBounds(283, 25, 30, 30);
		enComp.setFont(settings.Screen.PX18);
		addComp(enComp);

		fileComp = new TextComp("+", "Create Custom File", c1, c2, c3, ()->Screen.getFileView().getFileCreator().show("Custom File"));
		fileComp.setBounds(315, 25, 30, 30);
		fileComp.setFont(settings.Screen.PX18);
		addComp(fileComp);

		sep2 = new TextComp("", c1, c3, c3, ()->{});
		sep2.setBounds(350, 20, 2, 40);
		addComp(sep2);

		contentComp = new TextComp("", c1, c2, c3, ()->{
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
		contentComp.setBounds(360, 25, 30, 30);
		contentComp.setToolTipText(DataManager.isContentAssistRealTime() ? "Content Assist is ON" : "Content Assist is Stopped");
		addComp(contentComp);

		asteriskComp = new TextComp("", c1, c2, c3, ()->{
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
		asteriskComp.setBounds(392, 25, 30, 30);
		asteriskComp.setToolTipText(DataManager.isUsingStarImports() ? "Using Asterisk Imports" : "Using Named Imports");
		addComp(asteriskComp);

		structureComp = new TextComp("", c1, c2, c3, ()->{
			hidden = !hidden;
			screen.screenHasProjectView = !screen.screenHasProjectView;
			Screen.getProjectView().organizeProjectViewDefaults();
			screen.doLayout();
			screen.revoke();
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
		structureComp.setBounds(424, 25, 30, 30);
		structureComp.setToolTipText(hidden ? "Project Structure Hidden" : "Project Structure Visible");
		addComp(structureComp);

		operateComp = new TextComp("", c1, c2, c3, ()->{
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
		operateComp.setBounds(456, 25, 30, 30);
		operateComp.setToolTipText(oPHidden ? "Operation Panel Hidden" : "Operation Panel Visible");
		addComp(operateComp);

		searchComp = new TextComp("", "Search and Open File", c1, c2, c3, ()->Screen.getFileView().getSearchWindow().setVisible(true)) {
			@Override
			public void draw(Graphics2D g) {
				g.setColor(color3);
				g.drawLine(16, 15, 25, 23);
				g.drawLine(16, 16, 25, 24);
				g.fillOval(5, 4, 14, 14);
				g.setColor(color2);
				g.fillOval(8, 7, 8, 8);
				g.setColor(color1);
				g.fillOval(8, 7, 8, 8);
			}
		};
		addComp(searchComp);
          
          sep4 = new TextComp("", c1, c3, c3, ()->{});
          addComp(sep4);

          structureViewComp = new TextComp("<\\>", c1, c2, c3, ()->structureView.setVisible(true));
          structureViewComp.setFont(settings.Screen.PX16);
          structureViewComp.setToolTipText("Lets see that class");
          addComp(structureViewComp);

          sep3 = new TextComp("", c1, c3, c3, ()->{});
          sep3.setBounds(500, 20, 2, 40);
          addComp(sep3);

          shellComp = new TextComp("</>", "Click to open a non-root terminal inside the IDE", c1, c2, c3, ()->{
               ide.Screen.getTerminalComp().showTerminal(true);
          });
          shellComp.setFont(settings.Screen.PX16);
          shellComp.setBounds(510, 25, 60, 30);
          add(shellComp);

          sep5 = new TextComp("", c1, c3, c3, ()->{});
          sep5.setBounds(580, 20, 2, 40);
          addComp(sep5);

          Color l1 = c3;
          Color l2 = c1;
          Color l3 = c2;
          if(!ide.utils.UIManager.isDarkMode()){
               l1 = c1;
               l2 = c3;
               l3 = c2;
          }
          
          themeComp = new TextComp(DataManager.getTheme(), "Switching theme needs IDE restart", l1, l2, l3, ()->{
               DataManager.setTheme(DataManager.getTheme().equals("light") ? "dark" : "light");
               themeComp.setText(DataManager.getTheme());
          });
          themeComp.setFont(settings.Screen.PX16);
          themeComp.setBounds(600, 25, 60, 30);
          add(themeComp);
          
		reshape();
	}

	public void setTask(String task) {
		taskMenu.setText(task);
		taskMenu.repaint();
	}

	public void setMsg(String msg) {
		taskMenu.setMsg(msg);
		taskMenu.repaint();
	}

	public void reshape() {
		reshapeTask();
	}

	private void reshapeTask() {
		searchComp.setBounds(getWidth() - 30, 25, 30, 30);
          sep4.setBounds(getWidth() - 40, 20, 2, 40);
          structureViewComp.setBounds(getWidth() - 110, 25, 60, 30);
		taskMenu.setLocation(getWidth() - taskMenu.getWidth(), 0);
	}

	private void initSetMenu() {
		JFontChooser fontC = new JFontChooser();
          setPopup.createItem("Change Font", IconManager.settingsImage, ()->{
               UIManager.setData(fontC);
               fontC.setSelectedFontStyle(Font.BOLD);
               fontC.setSelectedFont(new Font(UIManager.fontName, Font.BOLD, UIManager.fontSize));
               int res = fontC.showDialog(screen);
               if(res ==JFontChooser.OK_OPTION) {
                    Font font = fontC.getSelectedFont();
                    UIManager.fontName = font.getName();
                    UIManager.fontSize = font.getSize();
                    screen.getUIManager().save();
                    screen.loadThemes();
               }
          })
          .createItem("Change Workspace", IconManager.settingsImage, ()->new ide.utils.WorkspaceSelector(screen).setVisible(true));

          OPopupItem typeItem = new OPopupItem(setPopup, "Project Type : Non-Java (Needs IDE Restart)", IconManager.settingsImage, ()->{});
          typeItem.setAction(()->{
               ide.Screen.getFileView().getProjectManager().non_java = !ide.Screen.getFileView().getProjectManager().non_java;
               typeItem.setName(ide.Screen.getFileView().getProjectManager().non_java ? "Project Type : Non-Java" : "Project Type : Java");
               ide.Screen.getScreen().manageTools(ide.Screen.getFileView().getProjectManager());
               ide.Screen.getFileView().getProjectManager().save();
          });
          typeItem.setToolTipText("Wait... If You have changed \"Project Type\" Please Relaunch the IDE");
          setPopup.addItem(typeItem);
          
          setPopup.createItem("All Settings", IconManager.settingsImage, ()->{
               if(Screen.getFileView().getProjectManager().non_java)
                    Screen.getUniversalSettingsView().setVisible(true);
               else
                    Screen.getSettingsView().setVisible(true);
          });
	}

	private void initHelpMenu() {
          helpPopup.createItem("See Basic Manual", IconManager.ideImage, ()->ide.Manuals.showBasicManual())
          .createItem("Plugin Store", IconManager.ideImage, ()->Screen.getPluginStore().setVisible(true))
          .createItem("Plugin Manager", IconManager.ideImage, ()->Screen.getPluginView().setVisible(true))
          .createItem("Check for Update", IconManager.ideImage, ()->Screen.updateIDE())
          .createItem("About", IconManager.ideImage, ()->{
               infoScreen.setVisible(true);
          });
	}

	private void initViewMenu() {
          viewPopup.createItem("Show Project Panel", IconManager.projectImage, ()->{
                if(!screen.screenHasProjectView)
                    Screen.getProjectView().setVisible(true);
          });
	}

	private void initToolMenu() {
          toolsPopup.createItem("Import Picker", IconManager.buildImage, ()->EditorTools.showIS())
          .createItem("Snippet Manager", IconManager.buildImage, ()->Screen.snippetView.setVisible(true))
          .createItem("Generate Getter/Setter", IconManager.buildImage, ()->gset.Generator.gsView.genView(screen.getCurrentEditor()))
          .createItem("Override/Implement Methods", IconManager.buildImage, ()->gset.Generator.overView.genView(screen.getCurrentEditor()));
	}

	private void initProjectPopup() {
		FileFilter allFileFilter = new FileFilter() {

			@Override
			public String getDescription() {
				return "All Files And Directories";
			}

			@Override
			public boolean accept(File arg0) {
				return true;
			}
		};

		JFileChooser fileC = new JFileChooser();

          projectPopup.createItem("Manage Class-Path", IconManager.projectImage, ()->Screen.getFileView().getDependencyView().setVisible(true))
          .createItem("Manage Module-Path", IconManager.projectImage, ()->Screen.getFileView().getModuleView().setVisible(true))
          .createItem("Delete Files", IconManager.closeImage, 
               ()->{
                    fileC.setCurrentDirectory(new File(Screen.getFileView().getProjectPath() + File.separator + "src"));
                    fileC.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    fileC.setApproveButtonText("Delete");
                    fileC.setFileFilter(allFileFilter);
                    fileC.setMultiSelectionEnabled(true);
     
                    int res = fileC.showOpenDialog(screen);
                    if(res == JFileChooser.APPROVE_OPTION) {
                         File f = fileC.getSelectedFile();
                         File[] files = fileC.getSelectedFiles();
                         String names = "";
                         boolean v = false;
                         for(File file : files)
                         {
                              names += file.getName() + " ";
                              v = true;
                         }
                         if(v) names = names.substring(0, names.length() - 4);
     
                         int del = JOptionPane.showConfirmDialog(screen, "Do you want to delete "+names+"?", "Delete or not?", JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
                         if(del == JOptionPane.OK_OPTION)
                         {
                              new Thread(()->{
                                   for(File file : files) {
                                        try{
                                             deleteDir(file);
                                        }catch(Exception e2) {}
                                   }
                                   ImportManager.readSource(EditorTools.importManager);
                              }).start();
                         }
                    }
               })
          .createItem("Mark As Main Class", IconManager.fileImage, 
               ()->{
                    
                    if(Screen.getFileView().getProjectManager().non_java) return;
                    fileC.setCurrentDirectory(new File(Screen.getFileView().getProjectPath() + File.separator + "src"));
                    fileC.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileC.setApproveButtonText("Mark This As Main");
                    fileC.setFileFilter(new FileFilter() {
                         @Override
                         public String getDescription() {
                              return "Select a java class";
                         }
     
                         @Override
                         public boolean accept(File f) {
                              if(f.isDirectory()) return false;
                              else if(f.getName().endsWith(".java")) return true;
                              return false;
                         }
                    });
                    fileC.setMultiSelectionEnabled(false);
     
                    int res = fileC.showOpenDialog(screen);
                    if(res == JFileChooser.APPROVE_OPTION)
                         Screen.getRunView().setMainClassPath(fileC.getSelectedFile().getAbsolutePath());
               })
          .createItem("Refresh", IconManager.projectImage, ()->Screen.getProjectView().reload());
	}

	private void initFilePopup() {
		//New Menu Items
          filePopup.createItem("Open File", IconManager.fileImage, ()->Screen.getFileView().open("File"))
          .createItem("Open Project", IconManager.projectImage, ()->Screen.getFileView().open("Project"))
          .createItem("New Project", IconManager.projectImage, ()->projectWizard.setVisible(true))
          .createItem("New Project (non-java project)", IconManager.projectImage, ()->universalProjectWizard.setVisible(true));

          recentFilePopup = OPopupWindow.gen("Recent Files Menu", screen, 0, true).width(300).height(250);
          fileMenu = new OPopupItem(recentFilePopup, "Recent Files", IconManager.fileImage, ()->{
               recentFilePopup.setLocation(filePopup.getX(), filePopup.getY());
               recentFilePopup.setVisible(true);
          });
          filePopup.addItem(fileMenu);
          
          recentProjectPopup = OPopupWindow.gen("Recent Projects Menu", screen, 0, true).width(300).height(250);
          projectMenu = new OPopupItem(recentProjectPopup, "Recent Projects", IconManager.projectImage, ()->{
               recentProjectPopup.setLocation(filePopup.getX(), filePopup.getY());
               recentProjectPopup.setVisible(true);
          });
          filePopup.addItem(projectMenu);
          
          allProjectsPopup = OPopupWindow.gen("All Projects Menu", screen, 0, true).width(300).height(250);
          allMenu = new OPopupItem(allProjectsPopup, "All Projects", IconManager.projectImage, ()->{
               allProjectsPopup.setLocation(filePopup.getX(), filePopup.getY());
               allProjectsPopup.setVisible(true);
          });
          filePopup.addItem(allMenu);

          filePopup.createItem("Close Project", IconManager.projectImage, ()->Screen.getFileView().closeProject())
          .createItem("Save All Editors", IconManager.fileImage, ()->screen.saveAllEditors())
          .createItem("Preferences", IconManager.settingsImage, ()->Screen.getSettingsView().setVisible(true))
          .createItem("Save Everything and Exit -Without terminating running apps", IconManager.closeImage, 
               ()->{
                    Screen.notify("Saving UI and Data");
                    screen.getUIManager().save();
                    screen.getDataManager().saveData();
                    Screen.notify("Saving Project");
                    screen.saveAllEditors();
                    try{Screen.getFileView().getProjectManager().save();}catch(Exception e2) {}
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
                    }catch(Exception e2) {}
                    Screen.notify("Saving UI and Data");
                    screen.getUIManager().save();
                    screen.getDataManager().saveData();
                    try{Screen.getFileView().getProjectManager().save();}catch(Exception e2) {}
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
                    }catch(Exception e2) {}
                    Screen.notify("Saving UI and Data");
                    screen.getUIManager().save();
                    screen.getDataManager().saveData();
                    Screen.notify("Saving Project");
                    screen.saveAllEditors();
                    try{Screen.getFileView().getProjectManager().save();}catch(Exception e2) {}
                    System.exit(0);
               });
	}

	private void addComp(Component c) {
		add(c);
	}

	public void deleteDir(File file) throws Exception
	{

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
		reshape();
	}

	public class LabelMenu extends JComponent {
		private String text;
		private String msg;
		private volatile boolean enter;
		public LabelMenu(String text, Runnable r, Runnable x) {
			this.text = "";
			setToolTipText(text);
			UIManager.setData(this);
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
			g.setColor(getForeground());
			g.setFont(getFont());
			int x = g.getFontMetrics().stringWidth(text);
			int cx = x;
			if(getWidth() + 3 != x + 3) {
				setSize(x + 3, 20);
				setPreferredSize(getSize());
				reshapeTask();
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
			super.setFont(font);
			f = font;
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
