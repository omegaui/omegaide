package creator;
import java.lang.reflect.Member;
/*
    This class creates the GUI of the Project Wizard.
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
import popup.*;

import settings.comp.TextComp;
import settings.comp.Comp;

import java.awt.Color;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import org.fife.ui.rtextarea.RTextArea;

import ide.Screen;
import ide.utils.DataManager;
import ide.utils.DependencyManager;
import ide.utils.NativesManager;
import ide.utils.ProjectDataBase;
import ide.utils.ResourceManager;
import tabPane.IconManager;
import ui.SDKSelector;
public class ProjectWizard extends JDialog{
	private static final Font font = new Font("Ubuntu Mono", Font.BOLD, 18);
     private TextComp rootBtn;
	public ProjectWizard(JFrame f){
		super(f, true);
		setLayout(null);
		setTitle("Project Wizard -Omega IDE");
		setSize(700, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		setUndecorated(true);
		init();
	}

	private void init(){
		final SDKSelector sdkSel = new SDKSelector((JFrame)getOwner());
		final JFileChooser fileC = new JFileChooser();
		fileC.setMultiSelectionEnabled(false);

		JTextField projectNameField = new JTextField();
		addHoverEffect(projectNameField, "Enter Project Name (do not include \'" + File.separator + "\')");
		projectNameField.setBounds(0, 0, getWidth() - 40, 40);
          projectNameField.setBackground(ide.utils.UIManager.c2);
          projectNameField.setForeground(ide.utils.UIManager.c3);
		add(projectNameField);

		rootBtn = new TextComp(":", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{});
		rootBtn.setBounds(projectNameField.getWidth(), 0, 40, 40);
		rootBtn.setToolTipText("Choose Project Parent Folder e.g: user.home/Documents/Omega Projects");
          rootBtn.setRunnable(()->{
               fileC.setDialogTitle("Select the folder in which the project will be created");
               fileC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
               fileC.setApproveButtonText("Select");
               int res = fileC.showOpenDialog(this);
               if(res == JFileChooser.APPROVE_OPTION)
                    rootBtn.setToolTipText(fileC.getSelectedFile().getAbsolutePath());
          });
          rootBtn.setToolTipText(ide.utils.DataManager.getProjectsHome());
          rootBtn.setArc(0, 0);
		add(rootBtn);

		JTextField jdkPath = new JTextField("Choose Java SE Environment");
		jdkPath.setBounds(0, projectNameField.getHeight(), projectNameField.getWidth(), 40);
		jdkPath.setEditable(false);
          jdkPath.setBackground(ide.utils.UIManager.c2);
          jdkPath.setForeground(ide.utils.UIManager.c3);
		add(jdkPath);

		TextComp javaRoot = new TextComp(":", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{
               String javaPath = DataManager.getPathToJava();
               if(javaPath == null || javaPath.equals("")) {
                    fileC.setDialogTitle("Select the folder containing the jdks");
                    fileC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    fileC.setApproveButtonText("Select");
                    int res = fileC.showOpenDialog(this);
                    if(res == JFileChooser.APPROVE_OPTION)
                         DataManager.setPathToJava(fileC.getSelectedFile().getAbsolutePath());
               }
               sdkSel.setVisible(true);
               String path = sdkSel.getSelection();
               if(path == null) return;
               jdkPath.setToolTipText(path);
               jdkPath.setText(path.substring(path.lastIndexOf(File.separatorChar) + 1));
	     });
		javaRoot.setBounds(projectNameField.getWidth(), projectNameField.getHeight(), 40, 40);
		javaRoot.setToolTipText("Choose Java Development Kit");
          javaRoot.setArc(0, 0);
		add(javaRoot);

		TextComp packLabel = new TextComp("Source Files", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{});
		packLabel.setBounds(0, jdkPath.getY() + jdkPath.getHeight(), getWidth(), 30);
		packLabel.setClickable(false);
          packLabel.setArc(0, 0);
		add(packLabel);

		JPanel memberPanel = new JPanel();
		memberPanel.setLayout(new BorderLayout());
		memberPanel.setBounds(0, packLabel.getY() + packLabel.getHeight(), getWidth(), 200);
		JTextArea packArea = new JTextArea();
		addHoverEffect(packArea, "type a source name with"+"\n"+"package (according to the java conventions) "+"\n"+"e.g: package.MySourceFile -type"+"\n"+"separated by new line "+"\n"+"\n\nanima.Animation -@interface"+"\n"+"ide.Screen -class");
		setData(packArea);
          packArea.setBackground(ide.utils.UIManager.c2);
          packArea.setForeground(ide.utils.UIManager.c3);

		memberPanel.add(new JScrollPane(packArea), BorderLayout.CENTER);
		add(memberPanel);

		//Dependency Panel

		TextComp addDepenLabel = new TextComp("Dependencies and Resources Roots", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{});
		addDepenLabel.setBounds(0, memberPanel.getY() + memberPanel.getHeight(), getWidth() - 60, 30);
		addDepenLabel.setClickable(false);
          addDepenLabel.setArc(0, 0);
		add(addDepenLabel);

		JPanel depenPanel = new JPanel();
		depenPanel.setLayout(new BorderLayout());
		depenPanel.setBounds(0, addDepenLabel.getY() + addDepenLabel.getHeight(), getWidth(), 200);
		RTextArea depenArea = new RTextArea();
		setData(depenArea);
          depenArea.setBackground(ide.utils.UIManager.c2);
          depenArea.setForeground(ide.utils.UIManager.c3);
          depenArea.setCurrentLineHighlightColor(ide.utils.UIManager.isDarkMode() ? new Color(133, 46, 196) : new Color(0, 0, 255, 20));
		depenArea.setEditable(false);
		depenPanel.add(new JScrollPane(depenArea), BorderLayout.CENTER);
		add(depenPanel);

		final LinkedList<String> depenRoots = new LinkedList<>();
		final JFileChooser fileCX = new JFileChooser();
		final FileFilter jarFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Select Jar File(s) (*.jar)";
			}

			@Override
			public boolean accept(File f) {
				if(f.isDirectory()) return true;
				else if(f.getName().endsWith(".jar")) return true;
				return false;
			}
		};
		final FileFilter dirFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Select a Directory";
			}

			@Override
			public boolean accept(File f) {
				if(f.isDirectory()) return true;
				return false;
			}
		};

		final OPopupWindow optionMenu = OPopupWindow.gen("Wizard Menu", this, 0, false).width(300);
		optionMenu.createItem("Add Java Archive (Jar File)", IconManager.projectImage, ()->{
               fileCX.setFileFilter(jarFilter);
               fileCX.setFileSelectionMode(JFileChooser.FILES_ONLY);
               fileCX.setApproveButtonText("Select");
               fileCX.setMultiSelectionEnabled(true);
               fileCX.setDialogTitle("Select Jar File(s)");
               int res = fileCX.showOpenDialog(this);
               if(res == JFileChooser.APPROVE_OPTION) {
                    for(File f : fileCX.getSelectedFiles())
                         offer(depenRoots, "Jar      |"+f.getAbsolutePath());
               }
               String text = "";
               for(String root : depenRoots) 
                    text += root + "\n";
               if(text.endsWith("\n"))
                    text = text.substring(0, text.length() - 1);
               depenArea.setText(text);
	     })
		.createItem("Add Native Library Root", IconManager.projectImage,	 ()->{
			fileCX.setFileFilter(dirFilter);
			fileCX.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileCX.setApproveButtonText("Select");
			fileCX.setMultiSelectionEnabled(true);
			fileCX.setDialogTitle("Select directories containing the native libraries");
			int res = fileCX.showOpenDialog(this);
			if(res == JFileChooser.APPROVE_OPTION) {
				for(File f : fileCX.getSelectedFiles())
					offer(depenRoots, "Native   |"+f.getAbsolutePath());
			}
			String text = "";
			for(String root : depenRoots) 
				text += root + "\n";
			if(text.endsWith("\n"))
				text = text.substring(0, text.length() - 1);
			depenArea.setText(text);
		})
		.createItem("Add Resource Root", IconManager.projectImage, ()->{
			fileCX.setFileFilter(dirFilter);
			fileCX.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileCX.setApproveButtonText("Select");
			fileCX.setMultiSelectionEnabled(true);
			fileCX.setDialogTitle("Select directories containing the resources");
			int res = fileCX.showOpenDialog(this);
			if(res == JFileChooser.APPROVE_OPTION) {
				for(File f : fileCX.getSelectedFiles())
					offer(depenRoots, "Resource |"+f.getAbsolutePath());
			}
			String text = "";
			for(String root : depenRoots)
				text += root + "\n";
			if(text.endsWith("\n"))
				text = text.substring(0, text.length() - 1);
			depenArea.setText(text);
		});
		TextComp addRootBtn = new TextComp("+", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{});
		addRootBtn.setBounds(getWidth() - 60, addDepenLabel.getY(), 30, 30);
		addRootBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				optionMenu.setLocation(e.getXOnScreen(), e.getYOnScreen());
				optionMenu.setVisible(true);
			}
		});
		setData(addRootBtn);
          addRootBtn.setArc(0, 0);
		add(addRootBtn);

		TextComp remRootBtn = new TextComp("-", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{
	          int n = depenArea.getCaretLineNumber();
               String text = depenArea.getText();
               StringTokenizer tokenizer = new StringTokenizer(text, "\n");
               int i = -1;
               text = "";
               depenRoots.clear();
               while(tokenizer.hasMoreTokens()) {
                    i++;
                    String token = tokenizer.nextToken();
                    if(n != i) {
                         text += token + '\n';
                         depenRoots.add(token);
                    }
               }
               if(text.endsWith("\n"))
                    text = text.substring(0, text.length() - 1);
               depenArea.setText(text);
	     });
		remRootBtn.setBounds(getWidth() - 30, addDepenLabel.getY(), 30, 30);
		setData(remRootBtn);
          remRootBtn.setArc(0, 0);
		add(remRootBtn);

		Comp cancelBtn = new Comp("Cancel", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->setVisible(false));
		cancelBtn.setBounds(0, getHeight() - 60, getWidth()/2, 60);
		setData(cancelBtn);
		add(cancelBtn);

		Comp createBtn = new Comp("Create", ide.utils.UIManager.c1, ide.utils.UIManager.c3, ide.utils.UIManager.c2, ()->{
               setVisible(false);
               String proRoot = projectNameField.getText();
               String proRootX = rootBtn.getToolTipText();
               String metaProRoot = proRootX + File.separator + proRoot;
               final String jdkRoot = jdkPath.getToolTipText();
               final String memberList = packArea.getText();
               LinkedList<Member> members = new LinkedList<>();
               if(!memberList.contains(":")) {
                    final StringTokenizer tokenizer = new StringTokenizer(memberList, "\n");
                    while(tokenizer.hasMoreTokens()){
                         try {
                              String token = tokenizer.nextToken();
                              String path = token.substring(0, token.lastIndexOf(' '));
                              String type = token.substring(token.indexOf('-') + 1);
                              members.add(new Member(path, type));
                         }catch(Exception ex) {
                              packArea.setText("Enter As Descripted in the toolTip!");
                         }
                    }
               }
               LinkedList<Member> dependencies = new LinkedList<>();
               final String roots = depenArea.getText();
               if(!roots.equals("")) {
                    final StringTokenizer tokenizer = new StringTokenizer(roots, "\n");
                    while(tokenizer.hasMoreTokens()){
                         String token = tokenizer.nextToken();
                         String path = token.substring(token.lastIndexOf('|') + 1).trim();
                         String type = token.substring(0, token.indexOf('|'));
                         dependencies.add(new Member(path, type));
                    }
               }
               
               boolean res = create(metaProRoot, jdkRoot, members, dependencies);
               if(res) {
                    if(Screen.launcher != null)
                         Screen.launcher.setVisible(false);
                    Screen.getScreen().setVisible(true);
               }
               setVisible(!res);
               if(!res) {
                    projectNameField.setText("This Project Already Exists");
               }
	     });
		createBtn.setBounds(getWidth()/2, getHeight() - 60, getWidth()/2, 60);
		setData(createBtn);
		add(createBtn);
	}

	public static void offer(LinkedList<String> roots, String root) {
		boolean found = false;
		for(String r : roots) {
			if(r.equals(root))
				found = true;
		}
		if(!found)
			roots.add(root);
	}

	public static boolean create(String projectRoot, String jdkPath, LinkedList<Member> members, LinkedList<Member> dependencies) {
		File file = new File(projectRoot);
		if(!file.exists())
			file.mkdir();
		else return false;
		new File(file.getAbsolutePath() + File.separator + "bin").mkdir();
		new File(file.getAbsolutePath() + File.separator + "out").mkdir();
		new File(file.getAbsolutePath() + File.separator + "src").mkdir();
		new File(file.getAbsolutePath() + File.separator + "res").mkdir();
		LinkedList<File> sources = new LinkedList<>();
		members.forEach(m->{
			if(m.name.contains(".")) {
				String pack = m.name.substring(0, m.name.lastIndexOf('.'));
				StringTokenizer tok = new StringTokenizer(pack, ".");
				pack = projectRoot + File.separator + "src";
				while(tok.hasMoreTokens()) {
					String token = tok.nextToken();
					pack += File.separator + token;
					File packX = new File(pack);
					packX.mkdir();
					System.out.println("Making Directory "+packX);
				}	
				File srcFile = new File(pack + File.separator + m.name.substring(m.name.lastIndexOf('.') + 1) + ".java");
				createSRCFile(srcFile, m.type, m.name.substring(0, m.name.lastIndexOf('.')), m.name.substring(m.name.lastIndexOf('.') + 1));
				if(srcFile.getName().endsWith(".java"))
					sources.add(srcFile);
			}
		});
		//Here Create the ProjectDataBaseSystem from ide.utils.ProjectDataBase
          ProjectDataBase.genInfo(file.getAbsolutePath(), false);
		ide.Screen.getScreen().loadProject(file);
		DependencyManager depenManager = ide.Screen.getFileView().getDependencyManager();
		NativesManager nativeManager = ide.Screen.getFileView().getNativesManager();
		ResourceManager resManager = ide.Screen.getFileView().getResourceManager();
		for(Member m : dependencies) {
			System.out.println(m.type);
			if(m.type.trim().equals("Jar")) {
				depenManager.add(m.name);
			}
			else if(m.type.trim().equals("Native")) {
				nativeManager.add(m.name);
			}
			else if(m.type.trim().equals("Resource")) {
				resManager.add(m.name);
			}
		}
		ProjectDataBase dataBase = ide.Screen.getFileView().getProjectManager();
		dataBase.setJDKPath(jdkPath);
		depenManager.saveFile();
		nativeManager.saveFile();
		resManager.saveData();
		sources.forEach(s->ide.Screen.getScreen().loadFile(s));
		return true;
	}

	public static void createSRCFile(File file, String type, String pack, String name){
		try{
			PrintWriter writer = new PrintWriter(new FileOutputStream(file));
			String header = type;
			if(!header.equals("")){
				writer.println("package " + pack + ";");
				writer.println("public " + header + " " + name + " {\n}");
			}
			writer.close();
			ide.Screen.getScreen().loadFile(file);
			Screen.getProjectView().reload();
		}catch(Exception e){e.printStackTrace();}
	}

	public static void setData(Component c){
		c.setFont(font);
	}

	public static void addHoverEffect(JTextComponent c, String tip){
		c.setToolTipText(tip);
		c.setText(tip);
		c.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e){
				if(c.getText().equals(tip))
					c.setText("");
			}
			@Override
			public void focusLost(FocusEvent e){
				if(c.getText().equals(""))
					c.setText(tip);
			}
		});
	}

	@Override
	public Component add(Component c){
		super.add(c);
		setData(c);
		return c;
	}

     @Override
     public void setVisible(boolean value){
          if(value){
               rootBtn.setToolTipText(ide.utils.DataManager.getProjectsHome());
          }
     	super.setVisible(value);
     }

	protected class Member{
		public String name;
		public String type;
		protected Member(String name, String type) {
			this.name = name;
			this.type = type;
		}
	}

}
