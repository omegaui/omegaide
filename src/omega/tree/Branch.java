/**
* The FileTree Component
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

package omega.tree;
import omega.Screen;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import omega.utils.PopupManager;
import omega.utils.UIManager;
import omega.utils.IconManager;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.Image;

import java.util.Locale;
import java.util.LinkedList;

import java.awt.image.BufferedImage;

import omega.popup.OPopupWindow;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.ImageIcon;

import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class Branch extends JComponent{
	public File file;
	
	private String name;
	private String type = "?";
	private OPopupWindow popupMenu;
	
	private BufferedImage icon;
	private BufferedImage originalIcon;
	private LinkedList<BufferedImage> images = new LinkedList<>();
	
	public volatile boolean enter;
	
	private boolean expand;
	
	private Locale l;
	
	public static final int OPTIMAL_HEIGHT = 30;
	public static final int OPTIMAL_X = 40;
	public static final int IMAGE_SIZE = 16;
	
	public static final Color ANY_COLOR = TOOLMENU_COLOR5;
	public static final Color SOURCE_COLOR = ANY_COLOR;
	public static final Color BYTE_COLOR = new Color(150, 150, 50, 220);
	public static final Color IMAGE_COLOR = new Color(50, 100, 50, 220);
	public static final Color LINUX_COLOR = new Color(175, 50, 50, 220);
	public static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
	public static final Color WEB_COLOR = TOOLMENU_COLOR3;
	public static final Color XML_COLOR = LINUX_COLOR;
	public static final Color ARCHIVE_COLOR = ANY_COLOR;
	
	public interface Locale {
		void locate(Branch b);
	}
	
	public Branch(File file, Locale l){
		this.file = file;
		this.l = l;
		this.name = file.getName();
		this.expand = file.isDirectory();
		
		this.icon = file.getName().endsWith(".java") ? IconManager.fluentsourcefileImage : getPreferredImage(file);
		this.originalIcon = icon;
		Image iconX = this.icon.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH);
		this.icon = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = this.icon.getGraphics();
		g.drawImage(iconX, 0, 0, null);
		g.dispose();
		
		setBackground(UIManager.back1);
		setFont(UBUNTU_PX14);
		if(expand){
			type = "";
			if(file.listFiles().length == 0){
				setForeground(EMPTY_COLOR);
				type = "Empty";
			}
			else
				setForeground(UIManager.glow);
		}
		else{
			setForeground(ANY_COLOR);
			if(file.getName().endsWith(".java") || file.getName().endsWith(".rs") || file.getName().endsWith(".py")
			|| file.getName().endsWith(".groovy")) {
				setForeground(SOURCE_COLOR);
				type = "SourceCode";
			}
			else if(file.getName().endsWith(".class")){
				setForeground(BYTE_COLOR);
				type = "ByteCode";
			}
			else if(file.getName().endsWith(".exe") || file.getName().endsWith(".msi") || file.getName().endsWith(".cmd") || file.getName().endsWith(".bat")){
				type = "Windows";
			}
			else if(file.getName().endsWith(".dmg")){
				type = "Mac";
			}
			else if(file.getName().endsWith(".dll") || file.getName().endsWith(".so") || file.getName().endsWith(".dylib")){
				type = "Native Lib";
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
		popupMenu= OPopupWindow.gen("Tree Menu", omega.Screen.getScreen(), 0, false).width(250);
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
				if(e.getButton() == 1){
					new Thread(()->{
						l.locate(Branch.this);
					}).start();
				}
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
	
	public void prepareAnimationImages(){
		int distance = 9;
		int size = IMAGE_SIZE;
		while(distance >= 0){
			BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)image.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(UIManager.back1);
			g.fillRect(0, 0, size, size);
			g.drawImage(originalIcon.getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, size, size, null);
			g.dispose();
			images.add(image);
			
			size++;
			distance--;
		}
	}
	
	public synchronized void playEnterAnimation(){
		if(!isAnimationsOn())
			return;
		new Thread(()->{
			if(images.isEmpty())
				prepareAnimationImages();
			if(getGraphics() == null)
				return;
			Graphics2D g = (Graphics2D)getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int size = IMAGE_SIZE;
			int distance = 9;
			int i = 0;
			while(distance >= 0 && enter){
				try{
					Thread.sleep(20);
				}
				catch(Exception e){
					
				}
				g.drawImage(images.get(i++), 12 - (size - IMAGE_SIZE), getHeight()/2 - size/2, size, size, null);
				size++;
				distance--;
			}
			g.dispose();
			if(!enter)
				repaint();
		}).start();
	}
	
	public void set(boolean v){
		this.enter = v;
		if(!expand){
			Color back = getBackground();
			setBackground(getForeground());
			setForeground(back);
		}
		repaint();
		if(v)
			playEnterAnimation();
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
		
		if(file.isDirectory())
			g.setColor(getBackground());
		else
			g.setPaint(new GradientPaint(enter ? (OPTIMAL_X - 2) : 0, 0, UIManager.back1, getWidth(), getHeight(), getBackground()));
		g.fillRect(enter ? (OPTIMAL_X - 2) : 0, 0, getWidth(), getHeight());
		
		if(file.isDirectory())
			g.setColor(getForeground());
		else
			g.setColor(enter ? UIManager.glow : getForeground());
		g.drawString(name, OPTIMAL_X, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());

		g.setFont(PX14);
		
		if(!type.equals("?"))
			g.drawString(type, getWidth() - g.getFontMetrics().stringWidth(type) - 2, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent());
		
		g.drawImage(icon, 12, getHeight()/2 - IMAGE_SIZE/2, IMAGE_SIZE, IMAGE_SIZE, null);
	}
	
	public static Color getColor(String fileName){
		Color res = ANY_COLOR;
		if(!fileName.contains("."))
			return res;
		File file = new File(fileName);
		if(file.getName().endsWith(".java") || file.getName().endsWith(".rs") || file.getName().endsWith(".py")
		|| file.getName().endsWith(".groovy")) {
			res = SOURCE_COLOR;
		}
		else if(file.getName().endsWith(".class")){
			res = BYTE_COLOR;
		}
		else if(file.getName().endsWith(".exe") || file.getName().endsWith(".msi")){
			res = UIManager.TOOLMENU_COLOR1;
		}
		else if(file.getName().endsWith(".dmg")){
			res = UIManager.TOOLMENU_COLOR1;
		}
		else if(file.getName().endsWith(".dll") || file.getName().endsWith(".so") || file.getName().endsWith(".dylib")){
			res = UIManager.TOOLMENU_COLOR2;
		}
		else if(file.getName().endsWith(".deb") || file.getName().endsWith(".run")
		|| file.getName().endsWith(".sh")){
			res = LINUX_COLOR;
		}
		else if(file.getName().endsWith(".dependencies") || file.getName().endsWith(".sources")
		|| file.getName().endsWith(".natives") || file.getName().endsWith(".resources")
		|| file.getName().endsWith(".projectInfo") || file.getName().endsWith(".modules")
		|| file.getName().endsWith(".snippets") || file.getName().endsWith(".args")){
			res = UIManager.TOOLMENU_COLOR1;
		}
		else if(file.getName().startsWith(".")){
			res = UIManager.TOOLMENU_COLOR4;
		}
		else if(file.getName().endsWith(".js") || file.getName().endsWith(".html")){
			res = WEB_COLOR;
		}
		else if(file.getName().endsWith(".xml") || file.getName().endsWith(".fxml")){
			res = XML_COLOR;
		}
		else if(file.getName().endsWith(".txt")){
			res = UIManager.TOOLMENU_COLOR3;
		}
		else if(file.getName().endsWith(".png") || file.getName().endsWith(".jpg")
		|| file.getName().endsWith(".jpeg") || file.getName().endsWith(".gif")
		|| file.getName().endsWith(".jp2") || file.getName().endsWith(".bmp")
		|| file.getName().endsWith(".ico") || file.getName().endsWith(".svg")){
			res = IMAGE_COLOR;
		}
		else if(file.getName().endsWith(".zip") || file.getName().endsWith(".7z") ||
		file.getName().endsWith(".tar") || file.getName().endsWith(".tar.gz")
		|| file.getName().endsWith(".jar")){
			res = ARCHIVE_COLOR;
		}
		return res;
	}
	
	public static BufferedImage getPreferredImage(File file){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			if(files != null && files.length != 0){
				for(File fx : files){
					if(fx.getName().equals(".projectInfo"))
						return IconManager.fluentfolderImage;
					if(fx.getName().startsWith("build.gradle"))
						return IconManager.fluentmoduleImage;
				}
			}
			if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "src"))
				return IconManager.fluentsourceImage;
			else if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "bin"))
				return IconManager.fluentbinaryImage;
			else if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "res"))
				return IconManager.fluentresourceImage;
			else if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "out"))
				return IconManager.fluentoutImage;
			else if(file.getAbsolutePath().startsWith(Screen.getFileView().getProjectPath() + File.separator + "src") || file.getAbsolutePath().startsWith(Screen.getFileView().getProjectPath() + File.separator + "bin"))
				return IconManager.fluentpackageImage;
			return IconManager.fluentplainfolderImage;
		}
		if(file.getName().contains(".")){
			String ext = file.getName().substring(file.getName().lastIndexOf('.'));
			if(ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".bmp") || ext.equals(".gif") || ext.equals(".svg") || ext.equals(".ico") || ext.equals(".jp2"))
				return IconManager.fluentimagefileImage;
			if(ext.equals(".java"))
				return IconManager.fluentjavaImage;
			if(ext.equals(".py"))
				return IconManager.fluentpythonImage;
			if(ext.equals(".kt"))
				return IconManager.fluentkotlinImage;
			if(ext.equals(".groovy"))
				return IconManager.fluentgroovyImage;
			if(ext.equals(".dart"))
				return IconManager.fluentdartImage;
			if(ext.equals(".js") || ext.equals(".css") || ext.equals(".html") || ext.equals(".jsx") || ext.equals(".ts"))
				return IconManager.fluentwebImage;
			if(ext.equals(".c"))
				return IconManager.fluentcImage;
			if(ext.equals(".cpp"))
				return IconManager.fluentcplusplusImage;
			if(ext.equals(".rs"))
				return IconManager.fluentfileImage;
			if(ext.equals(".js") || ext.equals(".html") || ext.equals(".php") || ext.equals(".css"))
				return IconManager.fluentwebImage;
			if(ext.equals(".sh") || ext.equals(".run") || ext.equals(".dll") || ext.equals(".so") || file.getName().endsWith(".dylib"))
				return IconManager.fluentshellImage;
			if(ext.equalsIgnoreCase(".appimage") || ext.equals(".deb"))
				return IconManager.fluentlinuxImage;
			if(ext.equalsIgnoreCase(".fxml") || ext.equals(".xml"))
				return IconManager.fluentxmlImage;
			if(ext.equals(".cmd") || ext.equals(".bat") || ext.equals(".exe") || ext.equals(".msi"))
				return IconManager.fluentwindowsImage;
			if(ext.equals(".dmg"))
				return IconManager.fluentmacImage;
			if(file.getName().endsWith(".zip") || file.getName().endsWith(".7z") || file.getName().endsWith(".tar") || file.getName().endsWith(".tar.gz") || file.getName().endsWith(".jar"))
				return IconManager.fluentarchiveImage;
		}
		return IconManager.fluentanyfileImage;
	}
}

