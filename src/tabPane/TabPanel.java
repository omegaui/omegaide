package tabPane;

import java.awt.BorderLayout;
import java.io.File;
import java.util.LinkedList;
import java.util.zip.ZipFile;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import ide.Screen;
import ide.utils.Editor;
import ide.utils.UIManager;

public class TabPanel extends JPanel{

	private static Screen screen;
	private JTabbedPane tabPane;
	private LinkedList<String> names;
	private LinkedList<Editor> editors;
	private LinkedList<JPanel> panels;
	
	public TabPanel(Screen screen) {
		TabPanel.screen = screen;
		tabPane = new JTabbedPane();
		tabPane.setUI(new TabPaneUI());
		tabPane.setFocusable(false);
		
		editors = new LinkedList<>();
		names = new LinkedList<>();
		panels = new LinkedList<>();
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);

		tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		UIManager.setData(tabPane);
	}

	public void addTab(String name, Editor editor, String toolTip) {
		if(editors.indexOf(editor) >= 0) {
			remove(editor);
		}
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(editor.getAttachment(), BorderLayout.CENTER);
		panel.add(editor.getFAndR(), BorderLayout.NORTH);
		panels.add(panel);
		names.add(name);
		editors.add(editor);
		tabPane.addTab(name, panel);
		if(editor.currentFile == null)
		{
			tabPane.setTabComponentAt(editors.indexOf(editor), CloseButton.create(editor, name, ()->{
				editor.closeFile();
				remove(editor);
			}, ()->{
				tabPane.setSelectedIndex(editors.indexOf(editor));
			},toolTip, IconManager.unknownIcon, createMenu(editor)));
			return;
		}
		tabPane.setTabComponentAt(editors.indexOf(editor), CloseButton.create(editor, name, ()->{
			remove(editor);
		}, ()->{
			tabPane.setSelectedIndex(editors.indexOf(editor));
		},toolTip, getIconFor(editor.currentFile.getName().substring(editor.currentFile.getName().lastIndexOf('.') >= 0 ? editor.currentFile.getName().lastIndexOf('.') : 0)), createMenu(editor)));
		tabPane.setSelectedIndex(names.indexOf(name));
		Icon icon = getIconFor(editor.currentFile.getName().substring(editor.currentFile.getName().lastIndexOf('.') >= 0 ? editor.currentFile.getName().lastIndexOf('.') : 0));
		if(icon == IconManager.unknownIcon
				|| icon == IconManager.txtIcon) {
			editor.setSyntaxEditingStyle(Editor.SYNTAX_STYLE_NONE);
		}
	}
	
	public boolean viewImage(File file) {
		if(observableImage(file)) {
			Screen.openInDesktop(file);
			return true;
		}
		return false;
	}
	
	public static boolean observableImage(File file){
		String n = file.getName();
		return n.endsWith(".png") || n.endsWith(".jpg")
				|| n.endsWith(".jpeg") || n.endsWith(".bmp") || n.endsWith(".gif");
	}
	
	public boolean viewArchive(File file) {
		if(observableArchive(file)) {
			Screen.openInDesktop(file);
			return true;
		}
		return false;
	}
	
	public static boolean observableArchive(File root){
		try{
			ZipFile zipFile = new ZipFile(root);
			zipFile.close();
			return true;
		}catch(Exception e){}
		return false;
	}
	
	private static JPopupMenu createMenu(Editor editor) {
		if(editor.currentFile != null) {
			if(editor.currentFile.getName().endsWith(".java"))
				return PopupManager.createPopup(PopupManager.SOURCE_FILE, editor, screen, IconManager.javaIcon);
			else
				return PopupManager.createPopup(PopupManager.NON_SOURCE_FILE, editor, screen,
						getIconFor(editor.currentFile.getName().substring(editor.currentFile.getName().lastIndexOf('.') >= 0 ? editor.currentFile.getName().lastIndexOf('.') : 0)));
		}
		return null;
	}
	
	public static Icon getIconFor(String ext) {
		if(ext == null)
			return IconManager.unknownIcon;
		if(ext.equals(".java"))
			return IconManager.javaIcon;
		else if(ext.equals(".class"))
			return IconManager.jvmIcon;
		else if(ext.equals(".txt"))
			return IconManager.txtIcon;
		else if(ext.equals(".exe") || ext.equals(".cmd") || ext.equals(".bat") || ext.equals(".dll"))
			return IconManager.windowsIcon;
		else if(ext.equals(".py"))
			return IconManager.pythonIcon;
		else if(ext.equals(".html"))
			return IconManager.htmlIcon;
		else if(ext.equals(".js"))
			return IconManager.javaScriptIcon;
		else if(ext.equals(".xml"))
			return IconManager.xmlIcon;
		else if(ext.equals(".fxml"))
			return IconManager.fxmlIcon;
		else if(ext.equals(".exe"))
			return IconManager.exeIcon;
		else if(ext.equals(".zip") || ext.equals(".rar") || ext.equals(".iso") || ext.equals(".img"))
			return IconManager.zipIcon;
		else if(ext.equals(".png") || ext.equals(".bmp") || ext.equals(".jpg") || ext.equals(".gif") || ext.equals(".jpeg"))
			return IconManager.imgIcon;
		else if(ext.equals(".dmg"))
			return IconManager.dmgIcon;
		else if(ext.equals(".ico"))
			return IconManager.icoIcon;
		return IconManager.unknownIcon;
	}
	
	public Editor findEditor(File file) {
		for(Editor e : editors)
		{
			if(e != null) {
				if(e.currentFile != null) {
					if(e.currentFile.getAbsolutePath().equals(file.getAbsolutePath()))
						return e;
				}
			}
		}
		return null;
	}
	
	public void remove(Editor editor) {
		if(editors.indexOf(editor) < 0) {
			System.out.println(editor.currentFile + "does not exxits");
			return;
		}
		editor.saveCurrentFile();
		editor.currentFile = null;
		tabPane.removeTabAt(editors.indexOf(editor));
		names.remove(editors.indexOf(editor));
		panels.remove(editors.indexOf(editor));
		editors.remove(editor);
		if(tabPane.getTabCount() > 0)
			tabPane.setSelectedIndex(tabPane.getTabCount() - 1);
	}
	
	public void remove(String name) {
		if(names.indexOf(name) < 0) return;
		tabPane.removeTabAt(names.indexOf(name));
		editors.get(names.indexOf(name)).saveCurrentFile();
		editors.get(names.indexOf(name)).currentFile = null;
		editors.remove(names.indexOf(name));
		panels.remove(names.indexOf(name));
		names.remove(name);
	}
	
	public void closeAllTabs() {
		editors.forEach(editor->{
			try {
					if(editor.currentFile != null) {
						editor.saveCurrentFile();
				}
			}catch(Exception e) {}
		});
		editors.clear();
		names.clear();
		panels.clear();
		tabPane.removeAll();
	}
	
	public JPanel getPanel(Editor e) {
		return panels.get(editors.indexOf(e));
	}
	
	public Editor getCurrentEditor() {
		return editors.get(tabPane.getSelectedIndex());
	}
	
	public JTabbedPane getTabPane() {
		return tabPane;
	}
	
	public LinkedList<Editor> getEditors(){
		return editors;
	}
	
	public void resize() {
		setBounds(0,30,screen.getWidth() - 14, screen.getHeight() - 30 - 38);
		repaint();
	}
	
}
