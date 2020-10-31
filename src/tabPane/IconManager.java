package tabPane;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconManager {

	public static final LinkedList<BufferedImage> icons = new LinkedList<>();
	public static final ImageIcon methodIcon = getImageIcon("/function_icon.png");
	public static final ImageIcon variableIcon = getImageIcon("/var_icon.png");
	public static final Icon runSingleIcon = getIcon("/run.png");
	public static final Icon runAsMainIcon = runSingleIcon;
	public static final Icon runProjectIcon = runSingleIcon;
	public static final Icon run_20px = getIcon("/run_20px.png");
	public static final Icon search_20px = getIcon("/searchIcon.png");
	public static final Icon compile_20px = getIcon("/build_20px.png");
	public static final Icon show = getIcon("/show.png");
	public static final Icon hide = getIcon("/hide.png");
	public static final Icon info = getIcon("/info.png");
	public static final Icon scp = getIcon("/scp.png");
	public static final Icon is = getIcon("/is_.png");
	public static final Icon markAsMain = getIcon("/mark_As_main.png");
	public static final Icon project = getIcon("/project.png");
	public static final Icon open_file = getIcon("/open_file.png");
	public static final Icon java_20px = getIcon("/java_20px.png");
	public static final Icon file_20px = getIcon("/file_20px.png");
	public static final Icon class_20px = getIcon("/class_20px.png");
	public static final Icon enum_20px = getIcon("/enum_20px.png");
	public static final Icon interface_20px = getIcon("/interface_20px.png");
	public static final Icon annotation_20px = getIcon("/annotation_20px.png");
	public static final Icon addLib = getIcon("/add_lib.png");
	public static final Icon removeLib = getIcon("/remove_lib.png");
	public static final Icon compileProjectIcon = getIcon("/build_project_icon.png");
	public static final Icon renameIcon = getIcon("/rename_icon.png");
	public static final Icon saveIcon = getIcon("/save_icon.png");
	public static final Icon saveAsIcon = getIcon("/save_as_icon.png");
	public static final Icon discardIcon = getIcon("/discard_icon.png");
	public static final Icon deleteIcon = getIcon("/delete_icon.png");
	public static final Icon reloadIcon = getIcon("/reload_icon.png");
	public static final Icon runIcon = getIcon("/run.png");
	public static final Icon fileIcon = getIcon("/file.png");
	public static final Icon saveAllIcon = saveAsIcon;
	public static final Icon toolsIcon = getIcon("/tools.png");
	public static final Icon settingsIcon = getIcon("/settings.png");
	//File Icons
	public static final Icon javaIcon = getIcon("/java_icon.png");
	public static final Icon jvmIcon = getIcon("/jvm_icon.png");
	public static final Icon pythonIcon = getIcon("/python_icon.png");
	public static final Icon htmlIcon = getIcon("/html_icon.png");
	public static final Icon javaScriptIcon = getIcon("/javaScript_icon.png");
	public static final Icon xmlIcon = getIcon("/xml_icon.png");
	public static final Icon fxmlIcon = getIcon("/jfx_icon.png");
	public static final Icon exeIcon = getIcon("/exe_icon.png");
	public static final Icon zipIcon = getIcon("/zip_icon.png");
	public static final Icon imgIcon = getIcon("/img_icon.png");
	public static final Icon dmgIcon = getIcon("/mac_icon.png");
	public static final Icon icoIcon = getIcon("/icon_icon.png");
	public static final Icon unknownIcon = getIcon("/unknown_icon.png");
	public static final Icon newFileIcon = getIcon("/new_file_icon.png");
	public static final Icon txtIcon = getIcon("/txt_icon.png");
	public static final Icon windowsIcon = getIcon("/windows_icon.png");
	public static final Icon refreshIcon = getIcon("/refresh_icon.png");

	private static final Icon getIcon(String path) {
		Color c = (Color) javax.swing.UIManager.getDefaults().get("Button.background");
		if(c.getRed() <= 53) {
			path = path.substring(0, path.lastIndexOf('.'));
			path = path + "_dark" + ".png";
		}
		try {
			BufferedImage image = ImageIO.read(IconManager.class.getResource(path));
			icons.add(image);
			return new ImageIcon(image);
		}catch(Exception e) {e.printStackTrace();}
		return null;
	}

	public static final ImageIcon getImageIcon(String path) {
		Color c = (Color) javax.swing.UIManager.getDefaults().get("Button.background");
		if(c.getRed() <= 53
				&& !path.contains("Theme")) {
			path = path.substring(0, path.lastIndexOf('.'));
			path = path + "_dark" + ".png";
		}
		try {
			BufferedImage image = ImageIO.read(IconManager.class.getResource(path));
			return new ImageIcon(image);
		}catch(Exception e) {e.printStackTrace();}
		return null;
	}

}
