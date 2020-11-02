package tree;
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
	private JPopupMenu popupMenu;
	private volatile boolean enter;
	private boolean expand;
	private Locale l;
	private static final Font FONT = new Font("Ubuntu Mono", Font.BOLD, 18);
	private static final Font FONT_BOLD = new Font("Ubuntu Mono", Font.BOLD, 16);
	public static final int OPTIMAL_HEIGHT = 30;
	public static final int OPTIMAL_X = 20;
	private static final Color SOURCE_COLOR = new Color(50, 50, 250);
	private static final Color ANY_COLOR = ide.utils.ToolMenu.HIGHLIGHT;
	private static final Color BYTE_COLOR = new Color(150, 150, 50);
	private static final Color IMAGE_COLOR = new Color(50, 150, 50);
	private static final Color LINUX_COLOR = new Color(250, 50, 50);
	private static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
	private static final Color WEB_COLOR = Color.ORANGE;
	private static final Color XML_COLOR = Color.PINK;
	private static final Color ARCHIVE_COLOR = Color.DARK_GRAY;
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
			if(file.getName().endsWith(".java") || file.getName().endsWith(".fxml") || file.getName().endsWith(".py")){
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
					|| file.getName().endsWith(".snippets") || file.getName().endsWith(".content")){
				type = "IDE";
			}
			else if(file.getName().endsWith(".js") || file.getName().endsWith(".html")){
				setForeground(WEB_COLOR);
				type = "Web";
			}
			else if(file.getName().endsWith(".xml")){
				setForeground(XML_COLOR);
				type = "Xml";
			}
			else if(file.getName().endsWith(".txt")){
				type = "Text";
			}
			else if(file.getName().endsWith(".png") || file.getName().endsWith(".jpg")
					|| file.getName().endsWith(".jpeg") || file.getName().endsWith(".gif")
					|| file.getName().endsWith(".jp2") || file.getName().endsWith(".bmp")){
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
		popupMenu = new JPopupMenu();
		popupMenu.setInvoker(this);
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
