package tree;
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
import popup.*;

import java.util.Locale;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import tabPane.PopupManager;
public class Branch extends JComponent{
	public File file;
	private String name;
	private String type = "?";
	private OPopupWindow popupMenu;
	public volatile boolean enter;
	private boolean expand;
	private Locale l;
	private static final Font FONT = new Font("Ubuntu Mono", Font.BOLD, 18);
	private static final Font FONT_BOLD = new Font("Ubuntu Mono", Font.BOLD, 16);
	public static final int OPTIMAL_HEIGHT = 30;
	public static final int OPTIMAL_X = 20;
     public static final Color SOURCE_COLOR = new Color(250, 50, 50, 160);
	public static final Color ANY_COLOR = ide.utils.UIManager.c3;
	public static final Color BYTE_COLOR = new Color(150, 150, 50, 160);
	public static final Color IMAGE_COLOR = new Color(50, 150, 50, 160);
	public static final Color LINUX_COLOR = new Color(250, 50, 50, 160);
	public static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
	public static final Color WEB_COLOR = new Color(255, 200, 0, 160);
	public static final Color XML_COLOR = new Color(255, 175, 175, 160);
	public static final Color ARCHIVE_COLOR = new Color(64, 64, 64, 160);
  
	public interface Locale {
		void locate(Branch b);
	}
     
	public Branch(File file, Locale l){
		this.file = file;
		this.l = l;
		this.name = file.getName();
		this.expand = file.isDirectory();
		setFont(FONT);
		if(expand){
			type = "";
			if(file.listFiles().length == 0){
				setForeground(EMPTY_COLOR);
				type = "Empty";
			}
		}
		else{
			setForeground(ANY_COLOR);
			setFont(FONT_BOLD);
			if(file.getName().endsWith(".java") || file.getName().endsWith(".rs") || file.getName().endsWith(".py")
			   || file.getName().endsWith(".groovy")) {
				setForeground(SOURCE_COLOR);
				type = "SourceCode";
			}
			else if(file.getName().endsWith(".class")){
				setForeground(BYTE_COLOR);
				type = "ByteCode";
			}
			else if(file.getName().endsWith(".exe") || file.getName().endsWith(".msi")){
				type = "Windows";
			}
			else if(file.getName().endsWith(".dmg")){
				type = "Mac";
			}
			else if(file.getName().endsWith(".deb") || file.getName().endsWith(".run")
					|| file.getName().endsWith(".sh")){
				setForeground(LINUX_COLOR);
				type = "Linux";
			}
			else if(file.getName().endsWith(".dependencies") || file.getName().endsWith(".sources")
					|| file.getName().endsWith(".natives") || file.getName().endsWith(".resources")
					|| file.getName().endsWith(".projectInfo") || file.getName().endsWith(".modules")
					|| file.getName().endsWith(".snippets") || file.getName().endsWith(".args")){
				type = "IDE";
			}
               else if(file.getName().startsWith("."))
                    type = "File";
			else if(file.getName().endsWith(".js") || file.getName().endsWith(".html")){
				setForeground(WEB_COLOR);
				type = "Web";
			}
			else if(file.getName().endsWith(".xml") || file.getName().endsWith(".fxml")){
				setForeground(XML_COLOR);
				type = "Xml";
			}
			else if(file.getName().endsWith(".txt")){
				type = "Text";
			}
			else if(file.getName().endsWith(".png") || file.getName().endsWith(".jpg")
					|| file.getName().endsWith(".jpeg") || file.getName().endsWith(".gif")
					|| file.getName().endsWith(".jp2") || file.getName().endsWith(".bmp")
					|| file.getName().endsWith(".ico") || file.getName().endsWith(".svg")){
				setForeground(IMAGE_COLOR);
				type = "Image";
			}
			else if(file.getName().endsWith(".zip") || file.getName().endsWith(".7z") ||
					file.getName().endsWith(".tar") || file.getName().endsWith(".tar.gz")
					|| file.getName().endsWith(".jar")){
				setForeground(ARCHIVE_COLOR);
				type = "Archive";
			}
		}
		popupMenu= OPopupWindow.gen("Tree Menu", ide.Screen.getScreen(), 0, false).width(300);
		PopupManager.createTreePopup(popupMenu, file);
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e){
				set(true);
			}
			@Override
			public void mouseExited(MouseEvent e){
				set(false);
			}
			@Override
			public void mouseClicked(MouseEvent e){
				//other button is 3
				if(e.getButton() == 1)
					l.locate(Branch.this);
				else if(e.getButton() == 3) {
					popupMenu.setLocation(e.getLocationOnScreen());
					popupMenu.setVisible(true);
				}
			}
		});
	}

	public void force() {
		l.locate(this);
	}
     
	public void set(boolean v){
		this.enter = v;
		if(!expand){
			Color back = getBackground();
			setBackground(getForeground());
			setForeground(back);
		}
		repaint();
	}

	@Override
	public void paint(Graphics graphics){
		super.paint(graphics);
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(getFont());
		int wx = g.getFontMetrics().stringWidth(name + "    ");
		int wy = g.getFontMetrics().stringWidth(type);
		int w  = OPTIMAL_X + wx + wy;
		if(getWidth() < w)
			setSize(w, getHeight());
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		g.drawString(name, OPTIMAL_X, (getHeight()/2) + 2);
		g.drawString(type, getWidth() - g.getFontMetrics().stringWidth(type) - 2, (getHeight()/2) + 2);
		if(enter){
			g.fillRect(OPTIMAL_X, (getHeight()/2) + FONT.getSize()/2 - 2, g.getFontMetrics().stringWidth(name), 2);
		}
	}
}
