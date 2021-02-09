package tabPane;
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
import java.awt.image.BufferedImage;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;

import popup.*;

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

     private static final String TITLE = "Open a text file to start editing here";
     private static final String HINT = "Navigate the File Tree (on left)";
     private static final String HINT1 = "Open the File tree by clicking the fourth ";

     private BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
	
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
          Graphics graphics = image.getGraphics();
          Graphics2D g = (Graphics2D)graphics;
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setColor(ide.utils.UIManager.c3);
          g.fillOval(7, 7, 16, 16);
          g.setColor(ide.utils.UIManager.c2);
          g.fillOval(10, 10, 10, 10);
          g.setColor(ide.utils.UIManager.c1);
          g.fillOval(10, 10, 10, 10);
          g.dispose();
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
			},toolTip, createMenu(editor)));
			return;
		}
		tabPane.setTabComponentAt(editors.indexOf(editor), CloseButton.create(editor, name, ()->{
			remove(editor);
		}, ()->{
			tabPane.setSelectedIndex(editors.indexOf(editor));
		},toolTip, createMenu(editor)));
		tabPane.setSelectedIndex(names.lastIndexOf(name));
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
			System.err.println(editor.currentFile + "does not exxits");
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

     @Override
     public void paint(Graphics graphics){
          if(tabPane.getTabCount() == 0){
               Graphics2D g = (Graphics2D)graphics;
               g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
               g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               g.setColor(ide.utils.UIManager.c2);
               g.fillRect(0, 0, getWidth(), getHeight());
               g.setColor(ide.utils.UIManager.c3);
               g.setFont(settings.Screen.PX28);
               FontMetrics f = g.getFontMetrics();
               String hint = screen.getToolMenu().hidden ? HINT1 : HINT;
               g.drawString(TITLE, getWidth()/2 - f.stringWidth(TITLE)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 1);
               g.drawString(hint, getWidth()/2 - f.stringWidth(hint)/2, getHeight()/2 - f.getHeight()/2 + f.getAscent() - f.getDescent() + 10 + f.getHeight());
               if(hint.equals(HINT1)){
                    g.drawImage(image, getWidth()/2 + f.stringWidth(hint)/2, getHeight()/2 - f.getHeight()/2 - 13 + f.getAscent() - f.getDescent() + f.getHeight(), null);
               }
               g.dispose();
          }
          else super.paint(graphics);
     }
}
