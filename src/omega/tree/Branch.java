package omega.tree;
import omega.popup.*;
import omega.Screen;
import omega.utils.IconManager;
import omega.utils.UIManager;
import omega.utils.PopupManager;
import java.awt.image.BufferedImage;
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

import static omega.settings.Screen.*;
public class Branch extends JComponent{
	public File file;
	private String name;
	private String type = "?";
	private OPopupWindow popupMenu;
	private BufferedImage icon;
	public volatile boolean enter;
	private boolean expand;
	private Locale l;
	public static final int OPTIMAL_HEIGHT = 30;
	public static final int OPTIMAL_X = 40;
	public static final Color ANY_COLOR = omega.utils.UIManager.TOOLMENU_COLOR2;
	public static final Color SOURCE_COLOR = ANY_COLOR;
	public static final Color BYTE_COLOR = new Color(150, 150, 50, 160);
	public static final Color IMAGE_COLOR = new Color(50, 250, 50, 160);
	public static final Color LINUX_COLOR = new Color(250, 50, 50, 160);
	public static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
	public static final Color WEB_COLOR = omega.utils.UIManager.TOOLMENU_COLOR3;
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
		this.icon = expand ? IconManager.fluentfolderImage : IconManager.fluentfileImage;
		setFont(PX16);
		if(expand){
			type = "";
			if(file.listFiles().length == 0){
				setForeground(EMPTY_COLOR);
				type = "Empty";
			}
		}
		else{
			setForeground(ANY_COLOR);
			setFont(PX14);
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
			else if(file.getName().endsWith(".dll") || file.getName().endsWith(".so")){
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
		g.fillRect(enter ? (OPTIMAL_X - 2) : 0, 0, getWidth(), getHeight());
		g.setColor(getForeground());
		g.drawString(name, OPTIMAL_X, (getHeight()/2) + 2);
          if(!type.equals("?"))
		     g.drawString(type, getWidth() - g.getFontMetrics().stringWidth(type) - 2, (getHeight()/2) + 2);
		g.drawImage(icon, 16, 8, 16, 16, null);
		if(enter){
			g.fillRect(OPTIMAL_X, (getHeight()/2) + getFont().getSize()/2 - 2, g.getFontMetrics().stringWidth(name), 2);
		}
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
		else if(file.getName().endsWith(".dll") || file.getName().endsWith(".so")){
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
}
