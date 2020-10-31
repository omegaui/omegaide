package ide.utils;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import creator.ProjectWizard;
import deassembler.CodeFramework;
import ide.Screen;
import ide.utils.systems.EditorTools;
import importIO.ImportManager;
import say.swing.JFontChooser;
import tabPane.IconManager;

public class ToolMenu extends JPanel {

	private Screen screen;
	public JMenuItem buildPro;
	public JMenuItem compilePro;
	public MenuButton runBtn;
	public MenuButton compileBtn;
	public MenuButton packBtn;
	public MenuButton classBtn;
	public MenuButton interfaceBtn;
	public MenuButton annotationBtn;
	public MenuButton enumBtn;
	public MenuButton fileBtn;
	public MenuButton searchBtn;
	public JMenuItem visCheckbox;
	public JMenuItem operateItem;
	public LabelMenu taskMenu;
	public volatile boolean oPHidden;
	public static JMenu openFileMenu;
	public static JMenu openProjectMenu;
	private static info.Screen infoScreen;
	public boolean hidden;
	private static final Font font = new Font(UIManager.fontName, Font.BOLD, 14);
	public static ProjectWizard projectWizard;
	public static final Color HIGHLIGHT = (((Color)javax.swing.UIManager.get("Button.background")).getRed() > 53) ? Color.decode("#000080") : Color.decode("#93C763");

	public ToolMenu(Screen screen) {
		this.screen = screen;
		if(projectWizard == null)
			projectWizard = new ProjectWizard(screen);
		setLayout(null);
		setSize(screen.getWidth(), 20);
		setPreferredSize(getSize());
		UIManager.setData(this);
		init();
	}

	private void init() {
		JPopupMenu filePopup = new JPopupMenu("File");
		initFilePopup(filePopup);
		Menu fileMenu = new Menu(filePopup, "File");
		fileMenu.setBounds(0, 0, 60, 20);
		addComp(fileMenu);

		JPopupMenu projectPopup = new JPopupMenu("Project");
		initProjectPopup(projectPopup);
		Menu projectMenu = new Menu(projectPopup, "Project");
		projectMenu.setBounds(60, 0, 60, 20);
		addComp(projectMenu);

		JPopupMenu toolsPopup = new JPopupMenu("Tools");
		initToolMenu(toolsPopup);
		Menu toolsMenu = new Menu(toolsPopup, "Tools");
		toolsMenu.setBounds(120, 0, 60, 20);
		addComp(toolsMenu);

		JPopupMenu viewPopup = new JPopupMenu("View");
		initViewMenu(viewPopup);
		Menu viewMenu = new Menu(viewPopup, "View ");
		viewMenu.setBounds(180, 0, 60, 20);
		addComp(viewMenu);

		JPopupMenu setPopup = new JPopupMenu("Settings");
		initSetMenu(setPopup);
		Menu setMenu = new Menu(setPopup, "Settings");
		setMenu.setBounds(240, 0, 60, 20);
		addComp(setMenu);

		JPopupMenu helpPopup = new JPopupMenu("Help");
		initHelpMenu(helpPopup);
		Menu helpMenu = new Menu(helpPopup, "Help");
		helpMenu.setBounds(300, 0, 60, 20);
		addComp(helpMenu);

		runBtn = new MenuButton("Run Project", IconManager.run_20px, ()->{
			if(runBtn.isEnabled() && compileBtn.isEnabled())
				Screen.getRunView().run();
		});

		compileBtn = new MenuButton("Compile Project", IconManager.compile_20px, ()->{
			if(runBtn.isEnabled() && compileBtn.isEnabled())
				Screen.getBuildView().compileProject();
		});

		packBtn = new MenuButton("Create Java Package", IconManager.java_20px, ()->{
			Screen.getFileView().getFileCreator().show("class");
		});

		classBtn = new MenuButton("Create Class", IconManager.class_20px, ()->{
			Screen.getFileView().getFileCreator().show("class");
		});

		interfaceBtn = new MenuButton("Create Interface", IconManager.interface_20px, ()->{
			Screen.getFileView().getFileCreator().show("interface");
		});

		annotationBtn = new MenuButton("Create Annotation", IconManager.annotation_20px, ()->{
			Screen.getFileView().getFileCreator().show("@interface");
		});

		enumBtn = new MenuButton("Create Enum", IconManager.enum_20px, ()->{
			Screen.getFileView().getFileCreator().show("enum");
		});

		fileBtn = new MenuButton("Create Custom File", IconManager.file_20px, ()->{
			Screen.getFileView().getFileCreator().show("Custom File");
		});
		taskMenu = new LabelMenu("Click to Run Garbage Collector", ()->{
			if(CodeFramework.resolving) return;
			setTask("Running Java Garbage Collector");
			taskMenu.repaint();
			System.gc();
			if(!CodeFramework.resolving) {
				long ram = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				ram = (long)(ram / 1000000);
				setTask("Using "+(ram) + " MB of Physical Memory Excluding JVM");
			}
		}, ()->{
			if(!CodeFramework.resolving) {
				long ram = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				ram = (long)(ram / 1000000);
				setTask("Using "+(ram) + " MB of Physical Memory Excluding JVM");
			}
		});
		setTask("Hover to see Memory Statistics");
		
		searchBtn = new MenuButton("Click to search Files", IconManager.search_20px, ()->{
			Screen.getFileView().getSearchWindow().setVisible(true);
		});
		
		reshape();
	}

	public void setTask(String task) {
		taskMenu.setText(task);
		taskMenu.repaint();
	}

	public void reshape() {
		int x = screen.getWidth()/2 - 300/2;
		if(x < 450)
			x = 450;
		packBtn.setLocation(x, 0);
		classBtn.setLocation(packBtn.getX() + 20 + 1, 0);
		interfaceBtn.setLocation(classBtn.getX() + 20 + 1, 0);
		enumBtn.setLocation(interfaceBtn.getX() + 20 + 1, 0);
		annotationBtn.setLocation(enumBtn.getX() + 20 + 1, 0);
		fileBtn.setLocation(annotationBtn.getX() + 20 + 1, 0);

		runBtn.setLocation(fileBtn.getX() + 20 + 20, 0);
		compileBtn.setLocation(runBtn.getX() + 20 + 1, 0);
		searchBtn.setLocation(screen.getWidth() - 20, 0);
		reshapeTask();
	}

	private void reshapeTask() {
		taskMenu.setLocation(compileBtn.getX() + 20 + 10, 0);
	}

	private void initSetMenu(JPopupMenu popup) {
		JFontChooser fontC = new JFontChooser();
		JMenuItem changeFont = new JMenuItem("Change Font", IconManager.show);
		changeFont.addActionListener((e)->{
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
		});
		popup.add(changeFont);

		JMenuItem all = new JMenuItem("All Settings", IconManager.settingsIcon);
		all.addActionListener((e)->{
			Screen.getSettingsView().setVisible(true);
		});
		popup.add(all);
	}

	private void initHelpMenu(JPopupMenu popup) {
		JMenuItem dev = new JMenuItem("About", IconManager.info);
		dev.addActionListener(e->{
			if(infoScreen == null)
				infoScreen = new info.Screen(Screen.getScreen());
			infoScreen.setVisible(true);
		});
		popup.add(dev);
	}

	private void initViewMenu(JPopupMenu popup) {
		operateItem = new JMenuItem("Show Operation Panel", IconManager.show);
		operateItem.addActionListener((e)->{
			if(!oPHidden) {
				screen.getOperationPanel().setVisible(false);
				operateItem.setText("Show Operation Panel");
				operateItem.setIcon(IconManager.show);
				oPHidden = true;
			}
			else {
				screen.getOperationPanel().setVisible(true);
				operateItem.setText("Hide Operation Panel");
				operateItem.setIcon(IconManager.hide);
				oPHidden = false;
			}
		});
		popup.add(operateItem);

		JMenuItem projMenu = new JMenuItem("Show Project Menu", IconManager.project);
		projMenu.addActionListener((e)->{
			if(!screen.screenHasProjectView)
				Screen.getProjectView().setVisible(true);
		});
		popup.add(projMenu);

		visCheckbox = new JMenuItem("Hide Project Structure", IconManager.hide);
		visCheckbox.addActionListener((e)->{
			if(!hidden) {
				hidden = true;
				visCheckbox.setText("Show Project Structure");
				visCheckbox.setIcon(IconManager.show);
				screen.screenHasProjectView = false;
				Screen.getProjectView().organizeProjectViewDefaults();
				screen.doLayout();
				screen.revoke();
				Screen.getProjectView().setVisible(false);
			}
			else {
				visCheckbox.setText("Hide Project Structure");
				visCheckbox.setIcon(IconManager.hide);
				hidden = false;
				screen.screenHasProjectView = true;
				Screen.getProjectView().organizeProjectViewDefaults();
				screen.doLayout();
				screen.revoke();
				Screen.getProjectView().setVisible(false);
			}

		});
		popup.add(visCheckbox);
	}

	private void initToolMenu(JPopupMenu popup) {
		JMenuItem is = new JMenuItem("Import Picker", IconManager.is);
		is.addActionListener(e->EditorTools.showIS());
		popup.add(is);
		
		JMenuItem ss = new JMenuItem("Snippet Manager", IconManager.scp);
		ss.addActionListener(e->Screen.snippetView.setVisible(true));
		popup.add(ss);
	}

	private void initProjectPopup(JPopupMenu popup) {
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

		JMenuItem libView = new JMenuItem("Manage Class-Path", IconManager.scp);
		libView.addActionListener((e)->Screen.getFileView().getDependencyView().setVisible(true));

		JMenuItem modView = new JMenuItem("Manage Module-Path", IconManager.scp);
		modView.addActionListener((e)->Screen.getFileView().getModuleView().setVisible(true));
		
		JMenuItem delBtn = new JMenuItem("Delete File(s)", IconManager.deleteIcon);
		delBtn.addActionListener((e)->{

			fileC.setCurrentDirectory(new File(Screen.getFileView().getProjectPath()+"/src"));
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
					names += file.getName() + " && ";
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

		});

		JMenuItem markAsMainBtn = new JMenuItem("Mark As Main Class", IconManager.markAsMain);
		markAsMainBtn.addActionListener((e)->{

			fileC.setCurrentDirectory(new File(Screen.getFileView().getProjectPath()+"/src"));
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
		});
		add(markAsMainBtn, FlowLayout.LEFT);

		JMenuItem refreshBtn = new JMenuItem("Refresh", IconManager.refreshIcon);
		refreshBtn.addActionListener(e->Screen.getProjectView().reload());

		popup.add(refreshBtn);
		popup.add(libView);
		popup.add(modView);
		popup.add(markAsMainBtn);
		popup.add(delBtn);
	}

	private void initFilePopup(JPopupMenu popup) {
		//New Menu Items
		JMenu newMenu = new JMenu("New");
		newMenu.setIcon(IconManager.newFileIcon);
		JMenuItem newPack = new JMenuItem("Java Package", IconManager.java_20px);
		newPack.addActionListener((e)->Screen.getFileView().getFileCreator().show("class"));
		newMenu.add(newPack);
		JMenuItem newClass = new JMenuItem("Java Class", IconManager.class_20px);
		newClass.addActionListener((e)->Screen.getFileView().getFileCreator().show("class"));
		newMenu.add(newClass);
		JMenuItem newInt = new JMenuItem("Java Interface", IconManager.interface_20px);
		newInt.addActionListener((e)->Screen.getFileView().getFileCreator().show("interface"));
		newMenu.add(newInt);
		JMenuItem newEnum = new JMenuItem("Java Enum", IconManager.enum_20px);
		newEnum.addActionListener((e)->Screen.getFileView().getFileCreator().show("enum"));
		newMenu.add(newEnum);
		JMenuItem newAnn = new JMenuItem("Java Annotation", IconManager.annotation_20px);
		newAnn.addActionListener((e)->Screen.getFileView().getFileCreator().show("@interface"));
		newMenu.add(newAnn);
		JMenuItem newOth = new JMenuItem("Other file", IconManager.file_20px);
		newMenu.addActionListener((e)->Screen.getFileView().getFileCreator().show("Custom File"));
		newMenu.add(newOth);
		popup.add(newMenu);
		
		//Project Menu Item
		JMenuItem createProItem = new JMenuItem("New Project", IconManager.project);
		createProItem.addActionListener((e)->projectWizard.setVisible(true));
		popup.add(createProItem);
		
		//Open Menu Item
		JMenuItem openFile = new JMenuItem("Open File", IconManager.open_file);
		openFile.addActionListener((e)->Screen.getFileView().open("File"));
		popup.add(openFile);
		JMenuItem openPro = new JMenuItem("Open Project", IconManager.project);
		openPro.addActionListener((e)->Screen.getFileView().open("Project"));
		popup.add(openPro);
		openFileMenu = new JMenu("Open Recent Files");
		openFileMenu.setIcon(IconManager.open_file);
		popup.add(openFileMenu);
		openProjectMenu = new JMenu("Open Recent Projects");
		openProjectMenu.setIcon(IconManager.project);
		popup.add(openProjectMenu);
		
		//Close Menu Item
		JMenuItem closePro = new JMenuItem("Close Project", IconManager.project);
		closePro.addActionListener((e)->{
			Screen.getFileView().closeProject();
		});
		popup.add(closePro);
		

		//Build Menu Item
		compilePro = new JMenuItem("Compile Project", IconManager.compileProjectIcon);
		compilePro.addActionListener((e)->Screen.getBuildView().compileProject());
		popup.add(compilePro);
		buildPro = new JMenuItem("Create Jar", IconManager.compileProjectIcon);
		buildPro.addActionListener((e)->Screen.getBuildView().createJar());
		popup.add(buildPro);

		//Save Menu Item
		JMenuItem saveAll = new JMenuItem("Save All", IconManager.saveIcon);
		saveAll.addActionListener((e)->screen.saveAllEditors());
		popup.add(saveAll);

		//Settings Menu Item
		JMenuItem prefs = new JMenuItem("Preferences", IconManager.settingsIcon);
		prefs.addActionListener((e)->Screen.getSettingsView().setVisible(true));
		popup.add(prefs);

		//Quit Menu Item
		JMenuItem exitDnotTerminate = new JMenuItem("Save Everything and Exit -Without terminating running apps", IconManager.hide);
		exitDnotTerminate.addActionListener((e)->{
			Screen.notify("Saving UI and Data");
			screen.getUIManager().save();
			screen.getDataManager().saveData();
			Screen.notify("Saving Project");
			screen.saveAllEditors();
			try{Screen.getFileView().getProjectManager().save();}catch(Exception e2) {}
			System.exit(0);
		});
		popup.add(exitDnotTerminate);

		JMenuItem exitDnotSave = new JMenuItem("Terminate Running Apps and Exit -Without saving opened editors", IconManager.hide);
		exitDnotSave.addActionListener((e)->{
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
		});
		popup.add(exitDnotSave);

		JMenuItem exit = new JMenuItem("Exit", IconManager.hide);
		exit.addActionListener((e)->{
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
		popup.add(exit);
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

	private void deleteEmptyDir(File file)
	{
		file.delete();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		reshape();
	}

	public class MenuButton extends JComponent{
		private volatile boolean enter;
		private ImageIcon icon;
		public MenuButton(String text, Icon icon, Runnable r) {
			setToolTipText(text);
			this.icon = (ImageIcon)icon;
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
				public void mouseClicked(MouseEvent e) {
					r.run();
				}
			});
			addComp(this);
		}

		@Override
		public void setFont(Font f) {
			super.setFont(font);
			setSize(ToolMenu.this.getHeight(), ToolMenu.this.getHeight());
			setPreferredSize(getSize());
		}

		@Override
		public void paint(Graphics g2D) {
			Graphics2D g = (Graphics2D)g2D;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
			if(enter) {
				g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
				g.setColor(HIGHLIGHT);
				g.fillRect(0, 0, getWidth(), 3);
			}
		}
	}

	private class LabelMenu extends JComponent {
		private String text;
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
			this.text = text;
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
			if(getWidth() + 3 != x + 3) {
				setSize(x + 3, ToolMenu.this.getHeight());
				setPreferredSize(getSize());
				reshapeTask();
			}
			x = getWidth()/2 - x/2;
			g.drawString(text, x, getFont().getSize());
			if(enter) {
				g.setColor(HIGHLIGHT);
				g.fillRect(x, getHeight() - 3, cx, 2);
				g.setFont(getFont());
				g.drawString(text, x, getFont().getSize());
			}
		}
	}

	private class Menu extends JComponent {
		private String text;
		private volatile boolean enter;
		public Menu(JPopupMenu popup, String text) {
			this.text = text;
			UIManager.setData(this);
			popup.setInvoker(this);
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
				public void mouseClicked(MouseEvent e) {
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
