/**
* The Main Tab Panel.
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

package omega.tabPane;
import omega.comp.FlexPanel;

import omega.Screen;

import omega.popup.OPopupWindow;

import java.util.zip.ZipFile;

import java.io.File;

import omega.utils.TabComp;
import omega.utils.PopupManager;
import omega.utils.ToolMenu;
import omega.utils.Editor;
import omega.utils.UIManager;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

import java.awt.image.BufferedImage;

import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JComponent;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class TabPanel extends JPanel{
	
	private static Screen screen;
	public JTabbedPane tabPane;
	
	private LinkedList<String> names;
	private LinkedList<Editor> editors;
	private LinkedList<TabHolder> tabHolders;
	
	private static final String TITLE = "Open a text file to start editing here";
	private static final String HINT = "Navigate the File Tree (on left)";
	private static final String HINT1 = "Open the File tree by clicking the fourth ";
	private static final String HINT1_2 = "Open the File tree by clicking the second ";
	
	private BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
	
	private Runnable addAction;
	private Runnable removeAction;
	
	public TabPanel(Screen screen) {
		addAction = ()->{};
		removeAction = ()->{};
		setBackground(UIManager.c2);
		TabPanel.screen = screen;
		tabPane = new JTabbedPane();
		tabPane.setUI(new TabPaneUI());
		
		names = new LinkedList<>();
		editors = new LinkedList<>();
		tabHolders = new LinkedList<>();
		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.CENTER);
		
		tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabPane.setBorder(null);
		tabPane.setFocusable(false);
		UIManager.setData(tabPane);
		
		Graphics graphics = image.getGraphics();
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(omega.utils.UIManager.TOOLMENU_COLOR3);
		g.fillOval(7, 7, 16, 16);
		g.setColor(omega.utils.UIManager.c2);
		g.fillOval(10, 10, 10, 10);
		g.setColor(omega.utils.UIManager.TOOLMENU_COLOR3_SHADE);
		g.fillOval(10, 10, 10, 10);
		g.dispose();
	}
	
	public void addTab(String name, String fullQualifiedName, JComponent component, String toolTip) {
		if(contains(component)) {
			remove(component);
		}
		
		TabHolder panel = (component instanceof Editor) ? new EditorTabHolder((Editor)component) : new TabHolder(component);
		
		tabHolders.add(panel);
		names.add(fullQualifiedName);
		
		tabPane.addTab(name, panel);
		tabPane.setBackgroundAt(tabPane.getTabCount() - 1, c2);

		boolean isEditor = component instanceof Editor;
		
		if(isEditor) {
			editors.add((Editor)component);
			Editor editor = (Editor)component;
			if(editor.currentFile == null) {
				tabPane.setTabComponentAt(getIndexOf(component), TabComp.create(editor, null, name, ()->{
					((Editor)component).closeFile();
					remove(component);
					}, ()->{
					tabPane.setSelectedIndex(getIndexOf(component));
				},toolTip, isEditor ? createMenu((Editor)component) : null));
				return;
			}
		}
		tabPane.setTabComponentAt(getIndexOf(component), TabComp.create(component, null, name, ()->{
			remove(component);
			}, ()->{
			tabPane.setSelectedIndex(getIndexOf(component));
		},toolTip, isEditor ? createMenu((Editor)component) : null));
		tabPane.setSelectedIndex(names.lastIndexOf(fullQualifiedName));
		addAction.run();
		
		panel.relocate();
	}
	
	public void addTab(String name, String fullQualifiedName, BufferedImage image, JComponent component, String toolTip, OPopupWindow popup) {
		if(contains(component)) {
			remove(component);
		}
		
		TabHolder panel = (component instanceof Editor) ? new EditorTabHolder((Editor)component) : new TabHolder(component);
		
		tabHolders.add(panel);
		names.add(fullQualifiedName);
		
		tabPane.addTab(name, panel);
		tabPane.setBackgroundAt(tabPane.getTabCount() - 1, c2);

		boolean isEditor = component instanceof Editor;
		
		if(isEditor) {
			editors.add((Editor)component);
			Editor editor = (Editor)component;
			if(editor.currentFile == null) {
				tabPane.setTabComponentAt(getIndexOf(component), TabComp.create(editor, image, name, ()->{
					((Editor)component).closeFile();
					remove(component);
					}, ()->{
					tabPane.setSelectedIndex(getIndexOf(component));
				},toolTip, isEditor ? createMenu((Editor)component) : popup));
				return;
			}
		}
		tabPane.setTabComponentAt(getIndexOf(component), TabComp.create(component, image, name, ()->{
			remove(component);
			}, ()->{
			tabPane.setSelectedIndex(getIndexOf(component));
		},toolTip, isEditor ? createMenu((Editor)component) : popup));
		tabPane.setSelectedIndex(names.indexOf(fullQualifiedName));
		addAction.run();
		
		panel.relocate();
	}
	
	public boolean contains(JComponent comp){
		for(TabHolder holder : tabHolders){
			if(holder.component == comp)
				return true;
		}
		return false;
	}
	
	public int getIndexOf(JComponent component){
		for(TabHolder holder : tabHolders) {
			if(holder.component == component){
				return tabHolders.indexOf(holder);
			}
		}
		return -1;
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
		}
		catch(Exception e){}
		return false;
	}
	
	public static OPopupWindow createMenu(Editor editor) {
		if(editor.currentFile != null) {
			if(editor.currentFile.getName().endsWith(".java"))
				return PopupManager.createPopup(PopupManager.SOURCE_FILE, editor, screen);
			else
				return PopupManager.createPopup(PopupManager.NON_SOURCE_FILE, editor, screen);
		}
		return null;
	}
	
	public Editor findEditor(File file) {
		for(Editor e : editors){
			if(e != null) {
				if(e.currentFile != null) {
					if(e.currentFile.getAbsolutePath().equals(file.getAbsolutePath()))
						return e;
				}
			}
		}
		return null;
	}
	
	public void remove(JComponent component) {
		remove(names.get(getIndexOf(component)));
	}
	
	public void remove(String name) {
		if(names.indexOf(name) < 0) 
			return;
		tabPane.removeTabAt(names.indexOf(name));
		if(tabHolders.get(names.indexOf(name)) instanceof EditorTabHolder){
			((Editor)(tabHolders.get(names.indexOf(name)).component)).saveCurrentFile();
			((Editor)(tabHolders.get(names.indexOf(name)).component)).currentFile = null;
			editors.remove(((Editor)(tabHolders.get(names.indexOf(name)).component)));
		}
		tabHolders.remove(names.indexOf(name));
		names.remove(name);
		removeAction.run();
	}
	
	public void closeAllTabs() {
		for(TabHolder holder : tabHolders){
			if(holder instanceof EditorTabHolder)
				((Editor)holder.component).saveCurrentFile();
		}
		names.clear();
		tabHolders.clear();
		editors.clear();
		tabPane.removeAll();
		removeAction.run();
		ToolMenu.getPathBox().setPath(null);
	}
	
	public JPanel getPanel(Editor e) {
		return tabHolders.get(getIndexOf(e));
	}
	
	public Editor getCurrentEditor() {
		if(tabHolders.get(tabPane.getSelectedIndex()) instanceof EditorTabHolder)
			return (Editor)(tabHolders.get(tabPane.getSelectedIndex()).component);
		return null;
	}
	
	public TabHolder getCurrentTabComponent() {
		return tabHolders.get(tabPane.getSelectedIndex());
	}
	
	public JTabbedPane getTabPane() {
		return tabPane;
	}
	
	public LinkedList<Editor> getEditors(){
		return editors;
	}
	
	public Runnable getAddAction() {
		return addAction;
	}
	
	public void setAddAction(Runnable addAction) {
		this.addAction = addAction;
	}
	
	public Runnable getRemoveAction() {
		return removeAction;
	}
	
	public void setRemoveAction(Runnable removeAction) {
		this.removeAction = removeAction;
	}
	
	public void setActiveEditor(Editor editor){
		int index = getIndexOf(editor);
		if(index < 0)
			return;
		tabPane.setSelectedIndex(index);
	}
	
	public void setActiveComponent(JComponent comp){
		int index = getIndexOf(comp);
		if(index < 0)
			return;
		tabPane.setSelectedIndex(index);
	}
	
	@Override
	public void paint(Graphics graphics){
		if(tabPane.getTabCount() == 0){
			Graphics2D g = (Graphics2D)graphics;
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(omega.utils.UIManager.c2);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(omega.utils.UIManager.TOOLMENU_COLOR3);
			g.setFont(omega.utils.UIManager.PX28);
			FontMetrics f = g.getFontMetrics();
			String hint = screen.getToolMenu().hidden ? ((Screen.getFileView().getProjectManager() != null && Screen.getFileView().getProjectManager().non_java) ? HINT1_2 : HINT1) : HINT;
			g.drawString(TITLE, getWidth()/2 - f.stringWidth(TITLE)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 1);
			g.setColor(omega.utils.UIManager.TOOLMENU_COLOR1);
			g.drawString(hint, getWidth()/2 - f.stringWidth(hint)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 10 + f.getHeight());
			if(hint.equals(HINT1) || hint.equals(HINT1_2)){
				g.drawImage(image, getWidth()/2 + f.stringWidth(hint)/2, getHeight()/2 - f.getHeight()/2 - 13 + f.getAscent() - f.getDescent() + f.getHeight(), null);
			}
			g.dispose();
		}
		else
			super.paint(graphics);
	}
}

