package omega.tree;
import omega.utils.systems.creators.FileOperationManager;

import omega.Screen;

import omega.utils.KeyStrokeListener;
import omega.utils.UIManager;
import omega.utils.IconManager;
import omega.utils.PopupManager;

import omega.popup.OPopupWindow;

import omega.comp.RTextField;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;

import java.awt.image.BufferedImage;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import static java.awt.event.KeyEvent.*;
import static omega.utils.UIManager.*;
import static omega.comp.Animations.*;
public class FileTreeBranch extends JComponent {
	public static final int VIEW_FILE_MODE = 0;
	public static final int EDIT_FILE_NAME_MODE = 1;
	
	public static final Color ANY_COLOR = TOOLMENU_COLOR5;
	public static final Color SOURCE_COLOR = TOOLMENU_COLOR3;
	public static final Color BYTE_COLOR = new Color(150, 150, 50, 220);
	public static final Color IMAGE_COLOR = new Color(50, 100, 50, 220);
	public static final Color LINUX_COLOR = new Color(175, 50, 50, 220);
	public static final Color EMPTY_COLOR = Color.LIGHT_GRAY;
	public static final Color WEB_COLOR = TOOLMENU_COLOR3;
	public static final Color XML_COLOR = LINUX_COLOR;
	public static final Color ARCHIVE_COLOR = ANY_COLOR;
	
	private File file;
	private FileTreePanel fileTreePanel;
	private Runnable clickAction;
	
	private int mode = VIEW_FILE_MODE;
	
	private String displayName;
	private BufferedImage image;
	private Color fileColor;
	
	private RTextField nameField;

	private OPopupWindow popupMenu;
	
	private volatile boolean enter = false;
	private volatile boolean expanded = false;
	private volatile boolean modeLocked = false;
	private volatile boolean rootMode = false;
	
	public FileTreeBranch(FileTreePanel fileTreePanel, File file){
		this.file = file;
		this.fileTreePanel = fileTreePanel;
		
		if(file.isDirectory()){
			lockMode();
			this.clickAction = ()->{
				fileTreePanel.expandBranch(FileTreeBranch.this);
			};
		}
		else{
			this.clickAction = ()->{
				fileTreePanel.onFileBranchClicked(FileTreeBranch.this);
			};
		}
		
		displayName = file.getName();
		
		image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().drawImage(getPreferredImageForFile(file).getScaledInstance(20, 20, BufferedImage.SCALE_SMOOTH), 0, 0, null);
		image.getGraphics().dispose();
		
		fileColor = getPreferredColorForFile(file);
		
		setSize(25 + 5 + computeWidth(displayName, PX14) + 5, 25);
		setPreferredSize(getSize());
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e){
				if(mode == EDIT_FILE_NAME_MODE)
					return;
				enter = true;
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e){
				if(mode == EDIT_FILE_NAME_MODE)
					return;
				enter = false;
				repaint();
			}
			@Override
			public void mousePressed(MouseEvent e){
				grabFocus();
				if(e.getButton() == 1 && e.getClickCount() == 2){
					clickAction.run();
				}
				else if(e.getButton() == 3) {
					if(popupMenu == null){
						popupMenu = OPopupWindow.gen("Tree Menu", Screen.getScreen(), 0, false).width(250);
						PopupManager.createTreePopup(popupMenu, file);
					}
					popupMenu.setLocation(e.getLocationOnScreen());
					popupMenu.setVisible(true);
				}
				repaint();
			}
		});

		addFocusListener(new FocusAdapter(){
			@Override
			public void focusLost(FocusEvent e){
				repaint();
			}
		});
		initKeyStrokes();
	}
	
	public void initKeyStrokes(){
		KeyStrokeListener listener = new KeyStrokeListener(this);
		listener.putKeyStroke((e)->{
			if(setMode(EDIT_FILE_NAME_MODE)){
				fileTreePanel.collapseBranch(FileTreeBranch.this);
				showRenameField();
			}
		}, VK_F2).setStopKeys(VK_CONTROL, VK_ALT);
		
		listener.putKeyStroke((e)->{
			clickAction.run();
		}, VK_ENTER).setStopKeys(VK_CONTROL, VK_ALT);
		
		addKeyListener(listener);
	}
	
	@Override
	public void paintComponent(Graphics graphics){
		if(mode != VIEW_FILE_MODE){
			super.paintComponent(graphics);
			return;
		}
		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(c2);
		g.setFont(PX14);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, (enter || rootMode) ? 3 : 0, getHeight()/2 - 20/2, null);
		g.setColor(fileColor);
		g.drawString(displayName, 30, getHeight()/2 - g.getFontMetrics().getHeight()/2 + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent() + 1);
		if(isFocusOwner()){
			g.fillRect(30, getHeight() - 3, g.getFontMetrics().stringWidth(displayName), 2);
		}
		if(enter){
			g.fillRect(0, 2, 2, getHeight() - 4);
		}
		super.paintComponent(g);
	}

	public boolean isRootMode() {
		return rootMode;
	}
	
	public void setRootMode(boolean rootMode) {
		this.rootMode = rootMode;
		repaint();
	}
	
	public boolean setMode(int mode){
		if(modeLocked)
			return false;
		this.mode = mode;
		return true;
	}
	
	public int getMode(){
		return mode;
	}
	
	public void lockMode(){
		modeLocked = true;
		repaint();
	}
	
	public void unlockMode(){
		modeLocked = false;
		repaint();
	}
	
	public void performRename(ActionEvent e){
		if(setMode(VIEW_FILE_MODE)){
			FileTreeBranch.this.remove(nameField);
			FileTreeBranch.this.repaint();
		}
		
		FileOperationManager.silentMoveFile(file, new File(file.getParentFile().getAbsolutePath(), e.getActionCommand()));
		fileTreePanel.refresh();
	}
	
	public void showRenameField(){
		if(mode != EDIT_FILE_NAME_MODE)
			return;
		//Entering Edit File Name Mode
		if(nameField == null){
			nameField = new RTextField(displayName, "", fileColor, c2, glow);
			nameField.setBounds(0, 0, getWidth(), getHeight());
			nameField.setFont(PX14);
			nameField.setArc(0, 0);
			nameField.addActionListener(this::performRename);
			nameField.addFocusListener(new FocusAdapter(){
				@Override
				public void focusLost(FocusEvent e){
					if(setMode(VIEW_FILE_MODE)){
						FileTreeBranch.this.remove(nameField);
						FileTreeBranch.this.repaint();
					}
				}
			});
		}
		add(nameField);
		
		nameField.grabFocus();
	}
	
	public java.io.File getFile() {
		return file;
	}
	
	public void setFile(java.io.File file) {
		this.file = file;
	}
	
	public java.awt.image.BufferedImage getImage() {
		return image;
	}
	
	public void setImage(java.awt.image.BufferedImage image) {
		this.image = image;
		repaint();
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	
	public boolean isParentOf(FileTreeBranch branch){
		return branch.getFile().getAbsolutePath().startsWith(getFile().getAbsolutePath());
	}

	public static BufferedImage getPreferredImageForFile(File file){
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
			else if(files.length == 0)
				return IconManager.fluentemptyBoxImage;
			if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "src"))
				return IconManager.fluentsourceImage;
			else if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "bin"))
				return IconManager.fluentbinaryImage;
			else if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "res"))
				return IconManager.fluentresourceImage;
			else if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "out"))
				return IconManager.fluentoutImage;
			else if(file.getAbsolutePath().equals(Screen.getFileView().getProjectPath() + File.separator + "lib"))
				return IconManager.fluentpowerImage;
			else if(file.getAbsolutePath().startsWith(Screen.getFileView().getProjectPath() + File.separator + "src") || file.getAbsolutePath().startsWith(Screen.getFileView().getProjectPath() + File.separator + "bin"))
				return IconManager.fluentwindRoseImage;
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

	public static Color getPreferredColorForFile(File file){
		if(file.isDirectory()){
			if(file.getName().charAt(0) == '.')
				return TOOLMENU_COLOR4;
			return glow;
		}
		Color res = ANY_COLOR;
		if(!file.getName().contains("."))
			return res;
		if(file.getName().endsWith(".java") || file.getName().endsWith(".rs") || file.getName().endsWith(".py") || file.getName().endsWith(".groovy") || file.getName().endsWith(".kt")) {
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
}
